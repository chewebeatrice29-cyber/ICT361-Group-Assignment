const express = require('express');
const router = express.Router();
const pool = require('../db');
const { authenticateToken } = require('../middleware/auth');

// POST /api/sync/batch - Batch sync offline operations idempotently
router.post('/batch', authenticateToken, async (req, res) => {
  const { operations } = req.body; // Array of operations

  if (!Array.isArray(operations) || operations.length === 0) {
    return res.status(400).json({ success: false, message: 'Operations array required.' });
  }

  const results = [];

  for (const op of operations) {
    const { operationId, studentId, type, payload, baseVersion } = op;

    if (!operationId) {
      results.push({ operationId, status: 'FAILED', message: 'Missing operationId.' });
      continue;
    }

    const connection = await pool.getConnection();
    try {
      await connection.beginTransaction();

      // Check if operation receipt already exists (Idempotency - Challenge 2)
      const [existingReceipts] = await connection.query(
        'SELECT result_json FROM processed_operations WHERE operation_id = ?',
        [operationId]
      );

      if (existingReceipts.length > 0) {
        await connection.commit();
        const savedResult = JSON.parse(existingReceipts[0].result_json);
        results.push({
          operationId,
          status: 'SYNCED',
          isDuplicateReceipt: true,
          result: savedResult
        });
        connection.release();
        continue;
      }

      // Execute Operation based on type
      let opResult = null;
      let opSuccess = true;
      let errorMessage = '';

      if (type === 'CREATE_STUDENT') {
        const { studentNumber, studentName, programme, labGroup } = payload;
        const [existing] = await connection.query('SELECT student_id FROM students WHERE student_number = ?', [studentNumber]);
        if (existing.length > 0) {
          opSuccess = false;
          errorMessage = `Student number ${studentNumber} already exists.`;
        } else {
          let targetGroup = labGroup || 'Unassigned';
          if (targetGroup !== 'Unassigned') {
            const [c] = await connection.query('SELECT COUNT(*) as count FROM students WHERE lab_group = ? AND is_deleted = 0 FOR UPDATE', [targetGroup]);
            if (c[0].count >= 15) {
              targetGroup = 'Unassigned';
            }
          }
          await connection.query(
            'INSERT INTO students (student_id, student_number, student_name, programme, lab_group, version, is_deleted) VALUES (?, ?, ?, ?, ?, 1, 0)',
            [studentId, studentNumber, studentName, programme, targetGroup]
          );
          opResult = { studentId, studentNumber, studentName, programme, labGroup: targetGroup, version: 1 };
        }

      } else if (type === 'UPDATE_STUDENT') {
        const [rows] = await connection.query('SELECT version, is_deleted FROM students WHERE student_id = ? FOR UPDATE', [studentId]);
        if (rows.length === 0 || rows[0].is_deleted === 1) {
          opSuccess = false;
          errorMessage = 'Student record not found or deleted on server.';
        } else if (baseVersion !== undefined && rows[0].version > parseInt(baseVersion)) {
          opSuccess = false;
          errorMessage = 'VERSION_CONFLICT: Record modified remotely.';
        } else {
          const { studentName, programme, labGroup } = payload;
          const newVersion = rows[0].version + 1;
          await connection.query(
            'UPDATE students SET student_name = ?, programme = ?, lab_group = ?, version = ? WHERE student_id = ?',
            [studentName, programme, labGroup, newVersion, studentId]
          );
          opResult = { studentId, studentName, programme, labGroup, version: newVersion };
        }

      } else if (type === 'GROUP_CHANGE') {
        const { targetGroup } = payload;
        const [rows] = await connection.query('SELECT lab_group, version, is_deleted FROM students WHERE student_id = ? FOR UPDATE', [studentId]);
        if (rows.length === 0 || rows[0].is_deleted === 1) {
          opSuccess = false;
          errorMessage = 'Student not found.';
        } else {
          let assigned = targetGroup;
          if (targetGroup !== 'Unassigned') {
            const [c] = await connection.query('SELECT COUNT(*) as count FROM students WHERE lab_group = ? AND is_deleted = 0 FOR UPDATE', [targetGroup]);
            if (c[0].count >= 15) {
              opSuccess = false;
              errorMessage = `GROUP_FULL: Group ${targetGroup} is full.`;
            }
          }
          if (opSuccess) {
            const newVersion = rows[0].version + 1;
            await connection.query('UPDATE students SET lab_group = ?, version = ? WHERE student_id = ?', [assigned, newVersion, studentId]);
            opResult = { studentId, labGroup: assigned, version: newVersion };
          }
        }

      } else if (type === 'DELETE_STUDENT') {
        await connection.query('UPDATE students SET is_deleted = 1, lab_group = "Unassigned" WHERE student_id = ?', [studentId]);
        await connection.query('INSERT INTO sync_tombstones (student_id, student_number) VALUES (?, "RESERVED") ON DUPLICATE KEY UPDATE deleted_at = CURRENT_TIMESTAMP', [studentId]);
        opResult = { studentId, isDeleted: true };
      }

      if (opSuccess) {
        // Save idempotency receipt
        const receiptJson = JSON.stringify(opResult || { success: true });
        await connection.query(
          'INSERT INTO processed_operations (operation_id, account_id, result_json) VALUES (?, ?, ?)',
          [operationId, req.user.accountId, receiptJson]
        );

        await connection.commit();
        results.push({
          operationId,
          status: 'SYNCED',
          result: opResult
        });
      } else {
        await connection.rollback();
        results.push({
          operationId,
          status: errorMessage.includes('VERSION_CONFLICT') ? 'CONFLICT' : 'FAILED',
          message: errorMessage
        });
      }

    } catch (err) {
      await connection.rollback();
      console.error(`Sync Operation ${operationId} Error:`, err);
      results.push({
        operationId,
        status: 'FAILED',
        message: err.message
      });
    } finally {
      connection.release();
    }
  }

  res.json({
    success: true,
    processedCount: results.length,
    results
  });
});

// GET /api/sync/pull - Pull server updates and deletion tombstones
router.get('/pull', authenticateToken, async (req, res) => {
  const { since } = req.query; // ISO timestamp or unix epoch

  try {
    let query = 'SELECT student_id, student_number, student_name, programme, lab_group, version, updated_at FROM students WHERE is_deleted = 0';
    const params = [];

    if (since) {
      query += ' AND updated_at > ?';
      params.push(new Date(since));
    }

    const [updatedStudents] = await pool.query(query, params);

    // Get tombstones (deleted students)
    let tombstoneQuery = 'SELECT student_id, student_number, deleted_at FROM sync_tombstones';
    const tombstoneParams = [];
    if (since) {
      tombstoneQuery += ' WHERE deleted_at > ?';
      tombstoneParams.push(new Date(since));
    }

    const [tombstones] = await pool.query(tombstoneQuery, tombstoneParams);

    const students = updatedStudents.map(r => ({
      studentId: r.student_id,
      studentNumber: r.student_number,
      studentName: r.student_name,
      programme: r.programme,
      labGroup: r.lab_group,
      version: r.version,
      updatedAt: r.updated_at
    }));

    const deletedIds = tombstones.map(t => t.student_id);

    res.json({
      success: true,
      timestamp: new Date().toISOString(),
      students,
      deletedStudentIds: deletedIds
    });

  } catch (err) {
    console.error('Sync Pull Error:', err);
    res.status(500).json({ success: false, message: 'Server error pulling sync data.' });
  }
});

module.exports = router;
