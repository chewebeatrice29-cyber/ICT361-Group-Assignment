const express = require('express');
const router = express.Router();
const { v4: uuidv4 } = require('crypto');
const pool = require('../db');
const { authenticateToken, requireRole } = require('../middleware/auth');

// Maximum active students allowed per lab group
const MAX_GROUP_CAPACITY = 15;

// Helper function to count active members in a lab group with lock inside transaction
async function getGroupMemberCountLocked(connection, labGroup) {
  if (labGroup === 'Unassigned') return 0;
  const [rows] = await connection.query(
    'SELECT COUNT(*) as count FROM students WHERE lab_group = ? AND is_deleted = 0 FOR UPDATE',
    [labGroup]
  );
  return rows[0].count;
}

// GET /api/students - Lecturer list students with pagination, search, and combined filters
router.get('/', authenticateToken, requireRole('LECTURER'), async (req, res) => {
  try {
    const { search = '', group = 'All', programme = 'All', page = 1, limit = 20 } = req.query;

    let whereClause = 'WHERE is_deleted = 0';
    const params = [];

    if (group && group !== 'All') {
      whereClause += ' AND lab_group = ?';
      params.push(group);
    }

    if (programme && programme !== 'All') {
      whereClause += ' AND programme = ?';
      params.push(programme);
    }

    if (search && search.trim() !== '') {
      const queryStr = `%${search.trim()}%`;
      whereClause += ' AND (student_name LIKE ? OR student_number LIKE ?)';
      params.push(queryStr, queryStr);
    }

    // Get total matching count
    const [countRows] = await pool.query(
      `SELECT COUNT(*) as total FROM students ${whereClause}`,
      params
    );
    const totalRecords = countRows[0].total;

    // Get paginated results
    const offset = (parseInt(page) - 1) * parseInt(limit);
    const [rows] = await pool.query(
      `SELECT student_id, student_number, student_name, programme, lab_group, account_id, version, created_at, updated_at
       FROM students ${whereClause}
       ORDER BY student_name ASC
       LIMIT ? OFFSET ?`,
      [...params, parseInt(limit), offset]
    );

    // Get group summary counts (G01..G04, Unassigned)
    const [summaryRows] = await pool.query(
      'SELECT lab_group, COUNT(*) as count FROM students WHERE is_deleted = 0 GROUP BY lab_group'
    );

    const groupCounts = {
      G01: 0,
      G02: 0,
      G03: 0,
      G04: 0,
      Unassigned: 0
    };
    summaryRows.forEach(r => {
      if (groupCounts[r.lab_group] !== undefined) {
        groupCounts[r.lab_group] = r.count;
      }
    });

    const students = rows.map(r => ({
      studentId: r.student_id,
      studentNumber: r.student_number,
      studentName: r.student_name,
      programme: r.programme,
      labGroup: r.lab_group,
      accountId: r.account_id,
      version: r.version,
      createdAt: r.created_at,
      updatedAt: r.updated_at
    }));

    res.json({
      success: true,
      totalRecords,
      page: parseInt(page),
      totalPages: Math.ceil(totalRecords / parseInt(limit)),
      students,
      groupCounts
    });

  } catch (err) {
    console.error('Fetch Students Error:', err);
    res.status(500).json({ success: false, message: 'Server error retrieving students.' });
  }
});

// GET /api/students/summary - Sharesheet group counts (No personal student data exposed)
router.get('/summary', authenticateToken, async (req, res) => {
  try {
    const [summaryRows] = await pool.query(
      'SELECT lab_group, COUNT(*) as count FROM students WHERE is_deleted = 0 GROUP BY lab_group'
    );

    const groupCounts = {
      G01: 0,
      G02: 0,
      G03: 0,
      G04: 0,
      Unassigned: 0
    };
    let totalActive = 0;

    summaryRows.forEach(r => {
      if (groupCounts[r.lab_group] !== undefined) {
        groupCounts[r.lab_group] = r.count;
        totalActive += r.count;
      }
    });

    res.json({
      success: true,
      groupCounts,
      totalActiveStudents: totalActive,
      maxCapacityPerGroup: MAX_GROUP_CAPACITY
    });
  } catch (err) {
    res.status(500).json({ success: false, message: 'Server error retrieving group summary.' });
  }
});

// GET /api/students/me - Student own profile and occupancy
router.get('/me', authenticateToken, async (req, res) => {
  try {
    if (!req.user.accountId) {
      return res.status(400).json({ success: false, message: 'No account linked.' });
    }

    const [rows] = await pool.query(
      'SELECT student_id, student_number, student_name, programme, lab_group, version FROM students WHERE account_id = ? AND is_deleted = 0',
      [req.user.accountId]
    );

    if (rows.length === 0) {
      return res.status(404).json({ success: false, message: 'Student profile not found.' });
    }

    const student = rows[0];

    // Get current occupancy for student's group
    let groupOccupancy = 0;
    if (student.lab_group !== 'Unassigned') {
      const [countRows] = await pool.query(
        'SELECT COUNT(*) as count FROM students WHERE lab_group = ? AND is_deleted = 0',
        [student.lab_group]
      );
      groupOccupancy = countRows[0].count;
    }

    res.json({
      success: true,
      student: {
        studentId: student.student_id,
        studentNumber: student.student_number,
        studentName: student.student_name,
        programme: student.programme,
        labGroup: student.lab_group,
        version: student.version
      },
      groupOccupancy,
      maxGroupCapacity: MAX_GROUP_CAPACITY
    });

  } catch (err) {
    res.status(500).json({ success: false, message: 'Server error fetching profile.' });
  }
});

// POST /api/students - Lecturer Create Student
router.post('/', authenticateToken, requireRole('LECTURER'), async (req, res) => {
  const { studentNumber, studentName, programme, labGroup = 'Unassigned' } = req.body;

  // Validation: Student number (9 digits)
  const cleanNumber = studentNumber ? studentNumber.toString().trim() : '';
  if (!/^\d{9}$/.test(cleanNumber)) {
    return res.status(400).json({ success: false, message: 'Student number must be exactly 9 digits.' });
  }

  // Validation: Student Name (2-100 characters)
  const cleanName = studentName ? studentName.trim() : '';
  if (!cleanName || cleanName.length < 2 || cleanName.length > 100) {
    return res.status(400).json({ success: false, message: 'Student name must be 2 to 100 characters.' });
  }

  // Validation: Programme
  if (!['CS', 'IT', 'DS'].includes(programme)) {
    return res.status(400).json({ success: false, message: 'Programme must be CS, IT, or DS.' });
  }

  // Validation: Lab Group
  if (!['G01', 'G02', 'G03', 'G04', 'Unassigned'].includes(labGroup)) {
    return res.status(400).json({ success: false, message: 'Invalid lab group selection.' });
  }

  const connection = await pool.getConnection();
  try {
    await connection.beginTransaction();

    // Check unique student_number (active or soft-deleted reserved number)
    const [existing] = await connection.query('SELECT student_id FROM students WHERE student_number = ?', [cleanNumber]);
    if (existing.length > 0) {
      await connection.rollback();
      return res.status(400).json({ success: false, message: `Student number ${cleanNumber} is already registered.` });
    }

    // Check target group capacity if assigned
    if (labGroup !== 'Unassigned') {
      const currentCount = await getGroupMemberCountLocked(connection, labGroup);
      if (currentCount >= MAX_GROUP_CAPACITY) {
        await connection.rollback();
        return res.status(400).json({
          success: false,
          code: 'GROUP_FULL',
          message: `Group ${labGroup} is full (${MAX_GROUP_CAPACITY}/${MAX_GROUP_CAPACITY} members). Student placed in Unassigned.`
        });
      }
    }

    const studentId = uuidv4();
    await connection.query(
      'INSERT INTO students (student_id, student_number, student_name, programme, lab_group, version, is_deleted) VALUES (?, ?, ?, ?, ?, 1, 0)',
      [studentId, cleanNumber, cleanName, programme, labGroup]
    );

    await connection.commit();

    res.status(201).json({
      success: true,
      message: 'Student added successfully.',
      student: {
        studentId,
        studentNumber: cleanNumber,
        studentName: cleanName,
        programme,
        labGroup,
        version: 1
      }
    });

  } catch (err) {
    await connection.rollback();
    console.error('Create Student Error:', err);
    res.status(500).json({ success: false, message: 'Server error creating student.' });
  } finally {
    connection.release();
  }
});

// PUT /api/students/:id - Lecturer Edit or Student edit own profile/name
router.put('/:id', authenticateToken, async (req, res) => {
  const studentId = req.params.id;
  const { studentName, programme, labGroup, studentNumber, baseVersion } = req.body;

  const connection = await pool.getConnection();
  try {
    await connection.beginTransaction();

    // Lock student record
    const [existingRows] = await connection.query(
      'SELECT student_id, student_number, student_name, programme, lab_group, account_id, version, is_deleted FROM students WHERE student_id = ? FOR UPDATE',
      [studentId]
    );

    if (existingRows.length === 0 || existingRows[0].is_deleted === 1) {
      await connection.rollback();
      return res.status(404).json({ success: false, message: 'Student record not found.' });
    }

    const currentStudent = existingRows[0];

    // Authorization check: Student can only edit own record, Lecturer can edit any
    if (req.user.role === 'STUDENT' && currentStudent.account_id !== req.user.accountId) {
      await connection.rollback();
      return res.status(403).json({ success: false, message: 'Forbidden: You can only edit your own profile.' });
    }

    // Check version conflict (Optimistic concurrency control - Challenge 3)
    if (baseVersion !== undefined && baseVersion !== null && currentStudent.version > parseInt(baseVersion)) {
      await connection.rollback();
      return res.status(409).json({
        success: false,
        code: 'VERSION_CONFLICT',
        message: 'The record was updated on another device. Please review the latest server state.',
        serverRecord: {
          studentId: currentStudent.student_id,
          studentNumber: currentStudent.student_number,
          studentName: currentStudent.student_name,
          programme: currentStudent.programme,
          labGroup: currentStudent.lab_group,
          version: currentStudent.version
        }
      });
    }

    // Validate Name if provided
    let newName = currentStudent.student_name;
    if (studentName !== undefined) {
      const cleanName = studentName.trim();
      if (cleanName.length < 2 || cleanName.length > 100) {
        await connection.rollback();
        return res.status(400).json({ success: false, message: 'Student name must be 2 to 100 characters.' });
      }
      newName = cleanName;
    }

    // Validate Programme if provided
    let newProgramme = currentStudent.programme;
    if (programme !== undefined) {
      if (!['CS', 'IT', 'DS'].includes(programme)) {
        await connection.rollback();
        return res.status(400).json({ success: false, message: 'Programme must be CS, IT, or DS.' });
      }
      newProgramme = programme;
    }

    // Validate Student Number if Lecturer is correcting it
    let newNumber = currentStudent.student_number;
    if (studentNumber !== undefined && req.user.role === 'LECTURER') {
      const cleanNumber = studentNumber.toString().trim();
      if (!/^\d{9}$/.test(cleanNumber)) {
        await connection.rollback();
        return res.status(400).json({ success: false, message: 'Student number must be exactly 9 digits.' });
      }
      // Check duplicate number if changed
      if (cleanNumber !== currentStudent.student_number) {
        const [numCheck] = await connection.query('SELECT student_id FROM students WHERE student_number = ? AND student_id != ?', [cleanNumber, studentId]);
        if (numCheck.length > 0) {
          await connection.rollback();
          return res.status(400).json({ success: false, message: `Student number ${cleanNumber} is already in use.` });
        }
        newNumber = cleanNumber;
      }
    }

    // Handle Group change with strict 15 capacity enforcement
    let newGroup = currentStudent.lab_group;
    if (labGroup !== undefined && labGroup !== currentStudent.lab_group) {
      if (!['G01', 'G02', 'G03', 'G04', 'Unassigned'].includes(labGroup)) {
        await connection.rollback();
        return res.status(400).json({ success: false, message: 'Invalid lab group.' });
      }

      if (labGroup !== 'Unassigned') {
        const targetCount = await getGroupMemberCountLocked(connection, labGroup);
        if (targetCount >= MAX_GROUP_CAPACITY) {
          await connection.rollback();
          return res.status(400).json({
            success: false,
            code: 'GROUP_FULL',
            message: `Target group ${labGroup} is full (${MAX_GROUP_CAPACITY}/${MAX_GROUP_CAPACITY}). Group transfer cancelled and previous group (${currentStudent.lab_group}) retained.`,
            retainedGroup: currentStudent.lab_group
          });
        }
      }
      newGroup = labGroup;
    }

    const newVersion = currentStudent.version + 1;

    await connection.query(
      'UPDATE students SET student_name = ?, programme = ?, lab_group = ?, student_number = ?, version = ? WHERE student_id = ?',
      [newName, newProgramme, newGroup, newNumber, newVersion, studentId]
    );

    await connection.commit();

    res.json({
      success: true,
      message: 'Student record updated successfully.',
      student: {
        studentId,
        studentNumber: newNumber,
        studentName: newName,
        programme: newProgramme,
        labGroup: newGroup,
        version: newVersion
      }
    });

  } catch (err) {
    await connection.rollback();
    console.error('Update Student Error:', err);
    res.status(500).json({ success: false, message: 'Server error updating student.' });
  } finally {
    connection.release();
  }
});

// POST /api/students/:id/group - Dedicated Group Request/Transfer endpoint
router.post('/:id/group', authenticateToken, async (req, res) => {
  const studentId = req.params.id;
  const { targetGroup } = req.body;

  if (!['G01', 'G02', 'G03', 'G04', 'Unassigned'].includes(targetGroup)) {
    return res.status(400).json({ success: false, message: 'Invalid target lab group.' });
  }

  const connection = await pool.getConnection();
  try {
    await connection.beginTransaction();

    const [rows] = await connection.query(
      'SELECT student_id, lab_group, account_id, version, is_deleted FROM students WHERE student_id = ? FOR UPDATE',
      [studentId]
    );

    if (rows.length === 0 || rows[0].is_deleted === 1) {
      await connection.rollback();
      return res.status(404).json({ success: false, message: 'Student not found.' });
    }

    const current = rows[0];

    // Check ownership if student
    if (req.user.role === 'STUDENT' && current.account_id !== req.user.accountId) {
      await connection.rollback();
      return res.status(403).json({ success: false, message: 'Forbidden.' });
    }

    if (current.lab_group === targetGroup) {
      await connection.rollback();
      return res.json({ success: true, message: 'Already in target group.', labGroup: targetGroup });
    }

    // Check capacity if target is a lab group (not Unassigned)
    if (targetGroup !== 'Unassigned') {
      const count = await getGroupMemberCountLocked(connection, targetGroup);
      if (count >= MAX_GROUP_CAPACITY) {
        await connection.rollback();
        return res.status(400).json({
          success: false,
          code: 'GROUP_FULL',
          message: `Target group ${targetGroup} is full (${MAX_GROUP_CAPACITY}/${MAX_GROUP_CAPACITY}). Transfer failed. Retained previous group ${current.lab_group}.`,
          retainedGroup: current.lab_group
        });
      }
    }

    const newVersion = current.version + 1;
    await connection.query(
      'UPDATE students SET lab_group = ?, version = ? WHERE student_id = ?',
      [targetGroup, newVersion, studentId]
    );

    await connection.commit();

    res.json({
      success: true,
      message: `Group successfully updated to ${targetGroup}.`,
      labGroup: targetGroup,
      version: newVersion
    });

  } catch (err) {
    await connection.rollback();
    console.error('Group Transfer Error:', err);
    res.status(500).json({ success: false, message: 'Server error during group transfer.' });
  } finally {
    connection.release();
  }
});

// DELETE /api/students/:id - Lecturer Soft Delete Student
router.delete('/:id', authenticateToken, requireRole('LECTURER'), async (req, res) => {
  const studentId = req.params.id;

  const connection = await pool.getConnection();
  try {
    await connection.beginTransaction();

    const [rows] = await connection.query(
      'SELECT student_id, student_number, account_id, is_deleted FROM students WHERE student_id = ? FOR UPDATE',
      [studentId]
    );

    if (rows.length === 0 || rows[0].is_deleted === 1) {
      await connection.rollback();
      return res.status(404).json({ success: false, message: 'Student record not found or already deleted.' });
    }

    const student = rows[0];

    // Soft delete student: set is_deleted = 1, release place by setting lab_group = 'Unassigned'
    await connection.query(
      'UPDATE students SET is_deleted = 1, lab_group = "Unassigned" WHERE student_id = ?',
      [studentId]
    );

    // Disable linked account if present
    if (student.account_id) {
      await connection.query('DELETE FROM accounts WHERE account_id = ?', [student.account_id]);
    }

    // Insert sync tombstone marker
    await connection.query(
      'INSERT INTO sync_tombstones (student_id, student_number) VALUES (?, ?) ON DUPLICATE KEY UPDATE deleted_at = CURRENT_TIMESTAMP',
      [studentId, student.student_number]
    );

    await connection.commit();

    res.json({
      success: true,
      message: `Student record ${student.student_number} soft-deleted. Place released. Number remains reserved.`
    });

  } catch (err) {
    await connection.rollback();
    console.error('Delete Student Error:', err);
    res.status(500).json({ success: false, message: 'Server error deleting student.' });
  } finally {
    connection.release();
  }
});

module.exports = router;
