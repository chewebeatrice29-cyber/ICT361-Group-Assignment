-- Lab Group Manager Database Schema
-- ICT361 Mobile Application Development

CREATE DATABASE IF NOT EXISTS `lab_group_manager` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `lab_group_manager`;

-- Drop existing tables in reverse dependency order for clean setup
DROP TABLE IF EXISTS `processed_operations`;
DROP TABLE IF EXISTS `sync_tombstones`;
DROP TABLE IF EXISTS `claim_codes`;
DROP TABLE IF EXISTS `students`;
DROP TABLE IF EXISTS `accounts`;

-- Accounts table for authentication & role permissions
CREATE TABLE `accounts` (
  `account_id` VARCHAR(36) NOT NULL,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `password_hash` VARCHAR(255) NOT NULL,
  `role` ENUM('STUDENT', 'LECTURER') NOT NULL DEFAULT 'STUDENT',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Students table
CREATE TABLE `students` (
  `student_id` VARCHAR(36) NOT NULL,
  `student_number` VARCHAR(9) NOT NULL UNIQUE,
  `student_name` VARCHAR(100) NOT NULL,
  `programme` ENUM('CS', 'IT', 'DS') NOT NULL,
  `lab_group` ENUM('G01', 'G02', 'G03', 'G04', 'Unassigned') NOT NULL DEFAULT 'Unassigned',
  `account_id` VARCHAR(36) NULL,
  `version` INT NOT NULL DEFAULT 1,
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`student_id`),
  FOREIGN KEY (`account_id`) REFERENCES `accounts`(`account_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Fictitious Student Claim Codes for initial verification
CREATE TABLE `claim_codes` (
  `claim_code` VARCHAR(20) NOT NULL,
  `student_number` VARCHAR(9) NOT NULL UNIQUE,
  `student_name` VARCHAR(100) NOT NULL,
  `programme` VARCHAR(10) NOT NULL,
  `is_claimed` TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`claim_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Idempotency Receipt tracking for offline operation replay (Challenge 2)
CREATE TABLE `processed_operations` (
  `operation_id` VARCHAR(36) NOT NULL,
  `account_id` VARCHAR(36) NOT NULL,
  `result_json` TEXT NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`operation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Sync Tombstones for soft deletion sync pulling
CREATE TABLE `sync_tombstones` (
  `student_id` VARCHAR(36) NOT NULL,
  `student_number` VARCHAR(9) NOT NULL,
  `deleted_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
