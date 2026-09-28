const express = require('express');
const cors = require('cors');
require('dotenv').config();

const authRoutes = require('./routes/authRoutes');
const studentRoutes = require('./routes/studentRoutes');
const syncRoutes = require('./routes/syncRoutes');

const app = express();
const PORT = process.env.PORT || 3000;

// Middleware
app.use(cors());
app.use(express.json());

// Routes
app.use('/api/auth', authRoutes);
app.use('/api/students', studentRoutes);
app.use('/api/sync', syncRoutes);

// Health check endpoint
app.get('/api/health', (req, res) => {
  res.json({ status: 'OK', message: 'ICT361 Lab Group Manager REST API Server is active.' });
});

// Start Server
app.listen(PORT, '0.0.0.0', () => {
  console.log(`====================================================`);
  console.log(` ICT361 Lab Group Manager API running on port ${PORT}`);
  console.log(` Local access: http://localhost:${PORT}/api/health`);
  console.log(` Android Emulator access: http://10.0.2.2:${PORT}/api/health`);
  console.log(`====================================================`);
});
