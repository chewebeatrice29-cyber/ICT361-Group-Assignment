const bcrypt = require('bcryptjs');
const { v4: uuidv4 } = require('crypto');
const pool = require('./db');

async function seedDatabase() {
  console.log('Seeding ICT361 Lab Group Manager database...');
  const connection = await pool.getConnection();

  try {
    await connection.beginTransaction();

    // 1. Seed Lecturer Account
    const lecturerPasswordHash = await bcrypt.hash('lecturer123', 10);
    const lecturerAccountId = uuidv4();

    await connection.query(
      'INSERT INTO accounts (account_id, username, password_hash, role) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE username=username',
      [lecturerAccountId, 'lecturer', lecturerPasswordHash, 'LECTURER']
    );
    console.log('✔ Lecturer account created: username="lecturer", password="lecturer123"');

    // 2. Seed Admin Account ("gigz" / "12345678")
    const adminPasswordHash = await bcrypt.hash('12345678', 10);
    const adminAccountId = uuidv4();

    await connection.query(
      'INSERT INTO accounts (account_id, username, password_hash, role) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE password_hash=VALUES(password_hash)',
      [adminAccountId, 'gigz', adminPasswordHash, 'LECTURER']
    );
    console.log('✔ Super Admin account created: username="gigz", password="12345678"');

    // 3. Seed Fictitious Claim Codes & Student Claim Records
    const claimCodes = [
      { code: 'CLAIM-20250001', number: '202500001', name: 'Mulenga Chanda', prog: 'CS' },
      { code: 'CLAIM-20250002', number: '202500002', name: 'Bwalya Banda', prog: 'IT' },
      { code: 'CLAIM-20250003', number: '202500003', name: 'Kabwe Mwape', prog: 'DS' },
      { code: 'CLAIM-20250004', number: '202500004', name: 'Natasha Phiri', prog: 'CS' },
      { code: 'CLAIM-20250005', number: '202500005', name: 'Chisomo Zulu', prog: 'IT' }
    ];

    for (const c of claimCodes) {
      await connection.query(
        'INSERT INTO claim_codes (claim_code, student_number, student_name, programme, is_claimed) VALUES (?, ?, ?, ?, 0) ON DUPLICATE KEY UPDATE is_claimed=0',
        [c.code, c.number, c.name, c.prog]
      );
    }
    console.log(`✔ ${claimCodes.length} claim codes seeded.`);

    // 4. Seed Sample Initial Active Students across Groups
    const sampleStudents = [
      { number: '202500010', name: 'Thandiwe Musonda', prog: 'CS', group: 'G01' },
      { number: '202500011', name: 'Kondwani Tembo', prog: 'IT', group: 'G01' },
      { number: '202500012', name: 'Mapalo Katongo', prog: 'DS', group: 'G02' },
      { number: '202500013', name: 'Lombe Chilufya', prog: 'CS', group: 'G02' },
      { number: '202500014', name: 'Subilo Kasonde', prog: 'IT', group: 'G03' },
      { number: '202500015', name: 'Wezi Mwanza', prog: 'DS', group: 'G04' }
    ];

    for (const s of sampleStudents) {
      const studentId = uuidv4();
      await connection.query(
        'INSERT INTO students (student_id, student_number, student_name, programme, lab_group, version, is_deleted) VALUES (?, ?, ?, ?, ?, 1, 0) ON DUPLICATE KEY UPDATE student_name=VALUES(student_name)',
        [studentId, s.number, s.name, s.prog, s.group]
      );
    }
    console.log(`✔ ${sampleStudents.length} initial active student records seeded.`);

    await connection.commit();
    console.log('Database seeding complete successfully!');

  } catch (err) {
    await connection.rollback();
    console.error('Seeding failed:', err);
  } finally {
    connection.release();
    process.exit();
  }
}

seedDatabase();
