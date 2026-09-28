const jwt = require('jsonwebtoken');

const JWT_SECRET = process.env.JWT_SECRET || 'ict361_lab_group_manager_secret_key_2025';

function authenticateToken(req, res, next) {
  const authHeader = req.headers['authorization'];
  const token = authHeader && authHeader.split(' ')[1];

  if (!token) {
    return res.status(401).json({ success: false, message: 'Authentication token required.' });
  }

  jwt.verify(token, JWT_SECRET, (err, user) => {
    if (err) {
      return res.status(403).json({ success: false, message: 'Invalid or expired authentication token.' });
    }
    req.user = user; // { accountId, username, role, studentId }
    next();
  });
}

function requireRole(role) {
  return (req, res, next) => {
    if (!req.user || req.user.role !== role) {
      return res.status(403).json({ success: false, message: `Forbidden: Requires ${role} role permissions.` });
    }
    next();
  };
}

module.exports = {
  authenticateToken,
  requireRole,
  JWT_SECRET
};
