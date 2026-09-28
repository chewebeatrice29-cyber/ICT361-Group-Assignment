const express = require('express');
const router = express.Router();
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const { v4: uuidv4 } = require('crypto');
const pool = require('../db');
const { JWT_SECRET, authenticateToken } = require('../middleware/auth');

// Register a new Student account
router.post('/register', async (req, res) => {
  const { claimCode, username, password, studentNumber, studentName, programme } = req.body;

  // Input Validation
  if (!username || !password || password.length < 6) {
    return res.status(400).json({ success: false, message: 'Username and password (min 6 chars) are required.' });
  }

  // Trim and validate 9-digit student number
  const cleanNumber = studentNumber ? studentNumber.toString().trim() : '';
  if (!/^\d{9}$/.test(cleanNumber)) {
    return res.status(400).json({ success: false, message: 'Student number must be exactly 9 digits with no spaces.' });
  }

  // Validate Name
  const cleanName = studentName ? studentName.trim() : '';
  if (!cleanName || cleanName.length < 2 || cleanName.length > 100) {
    return res.status(400).json({ success: false, message: 'Student name must be 2 to 100 characters long.' });
  }

  // Validate Programme
  if (!['CS', 'IT', 'DS'].includes(programme)) {
    return res.status(400).json({ success: false, message: 'Programme must be CS, IT, or DS.' });
  }

  const connection = await pool.getConnection();
  try {
    await connection.beginTransaction();

    // Check if account username exists
    const [existingUsers] = await connection.query('SELECT account_id FROM accounts WHERE username = ?', [username]);
    if (existingUsers.length > 0) {
      await connection.rollback();
      return res.status(400).json({ success: false, message: 'Username already taken.' });
    }

    // Check if claim code is required & valid if provided
    if (claimCode) {
      const [claim] = await connection.query('SELECT * FROM claim_codes WHERE claim_code = ? AND is_claimed = 0', [claimCode]);
      if (claim.length === 0) {
        await connection.rollback();
        return res.status(400).json({ success: false, message: 'Invalid or already used claim code.' });
      }
      if (claim[0].student_number !== cleanNumber) {
        await connection.rollback();
        return res.status(400).json({ success: false, message: 'Claim code does not match provided student number.' });
      }
      // Mark claim code as claimed
      await connection.query('UPDATE claim_codes SET is_claimed = 1 WHERE claim_code = ?', [claimCode]);
    }

    // Hash password
    const hashedPassword = await bcrypt.hash(password, 10);
    const accountId = uuidv4();

    // Insert Account
    await connection.query(
      'INSERT INTO accounts (account_id, username, password_hash, role) VALUES (?, ?, ?, ?)',
      [accountId, username, hashedPassword, 'STUDENT']
    );

    // Check if Lecturer already added this student record (by student_number)
    const [existingStudent] = await connection.query(
      'SELECT student_id, account_id FROM students WHERE student_number = ? AND is_deleted = 0',
      [cleanNumber]
    );

    let studentId;
    if (existingStudent.length > 0) {
      // Link to existing student profile! (Never create a second profile)
      if (existingStudent[0].account_id) {
        await connection.rollback();
        return res.status(400).json({ success: false, message: 'An account is already linked to this student number.' });
      }
      studentId = existingStudent[0].student_id;
      await connection.query(
        'UPDATE students SET account_id = ?, student_name = ?, programme = ? WHERE student_id = ?',
        [accountId, cleanName, programme, studentId]
      );
    } else {
      // Create new student profile
      studentId = uuidv4();
      await connection.query(
        'INSERT INTO students (student_id, student_number, student_name, programme, lab_group, account_id, version, is_deleted) VALUES (?, ?, ?, ?, ?, ?, 1, 0)',
        [studentId, cleanNumber, cleanName, programme, 'Unassigned', accountId]
      );
    }

    await connection.commit();

    // Generate JWT
    const token = jwt.sign(
      { accountId, username, role: 'STUDENT', studentId },
      JWT_SECRET,
      { expiresIn: '7d' }
    );

    res.status(201).json({
      success: true,
      message: 'Registration successful.',
      token,
      account: {
        accountId,
        username,
        role: 'STUDENT'
      },
      student: {
        studentId,
        studentNumber: cleanNumber,
        studentName: cleanName,
        programme,
        labGroup: 'Unassigned',
        version: 1
      }
    });

  } catch (err) {
    await connection.rollback();
    console.error('Registration Error:', err);
    res.status(500).json({ success: false, message: 'Server error during registration.' });
  } finally {
    connection.release();
  }
});

// Login for Student or Lecturer
router.post('/login', async (req, res) => {
  const { username, password } = req.body;

  if (!username || !password) {
    return res.status(400).json({ success: false, message: 'Username and password required.' });
  }

  try {
    const [users] = await pool.query('SELECT * FROM accounts WHERE username = ?', [username]);
    if (users.length === 0) {
      return res.status(401).json({ success: false, message: 'Invalid username or password.' });
    }

    const user = users[0];
    const passwordMatch = await bcrypt.compare(password, user.password_hash);
    if (!passwordMatch) {
      return res.status(401).json({ success: false, message: 'Invalid username or password.' });
    }

    // Get linked student profile if STUDENT role
    let studentProfile = null;
    if (user.role === 'STUDENT') {
      const [students] = await pool.query(
        'SELECT student_id, student_number, student_name, programme, lab_group, version FROM students WHERE account_id = ? AND is_deleted = 0',
        [user.account_id]
      );
      if (students.length > 0) {
        studentProfile = {
          studentId: students[0].student_id,
          studentNumber: students[0].student_number,
          studentName: students[0].student_name,
          programme: students[0].programme,
          labGroup: students[0].lab_group,
          version: students[0].version
        };
      }
    }

    const token = jwt.sign(
      {
        accountId: user.account_id,
        username: user.username,
        role: user.role,
        studentId: studentProfile ? studentProfile.studentId : null
      },
      JWT_SECRET,
      { expiresIn: '7d' }
    );

    res.json({
      success: true,
      token,
      account: {
        accountId: user.account_id,
        username: user.username,
        role: user.role
      },
      student: studentProfile
    });

  } catch (err) {
    console.error('Login Error:', err);
    res.status(500).json({ success: false, message: 'Server error during login.' });
  }
});

// Verify token session state
router.get('/me', authenticateToken, async (req, res) => {
  try {
    let studentProfile = null;
    if (req.user.role === 'STUDENT') {
      const [students] = await pool.query(
        'SELECT student_id, student_number, student_name, programme, lab_group, version FROM students WHERE account_id = ? AND is_deleted = 0',
        [req.user.accountId]
      );
      if (students.length > 0) {
        studentProfile = {
          studentId: students[0].student_id,
          studentNumber: students[0].student_number,
          studentName: students[0].student_name,
          programme: students[0].programme,
          labGroup: students[0].lab_group,
          version: students[0].version
        };
      }
    }

    res.json({
      success: true,
      account: {
        accountId: req.user.accountId,
        username: req.user.username,
        role: req.user.role
      },
      student: studentProfile
    });
  } catch (err) {
    res.status(500).json({ success: false, message: 'Server error checking session.' });
  }
});

module.exports = router;
