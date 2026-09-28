# ICT361 Lab Group Manager Application & REST API Server

**Mulungushi University** | School of Engineering and Technology | Department of Computer Science and IT  
**Course**: ICT361 Mobile Application Development — Competency Based Group Lab  
**Application Title**: Student Registration and Lab Group Management System

---

## Technical Architecture Overview

- **Android App Stack**:
  - **Language**: Java / XML Views
  - **Architecture**: MVVM (`ViewModel`, `LiveData`, `StudentRepository`)
  - **Local Database**: Room Database (`AppDatabase`, `StudentDao`, `SyncOperationDao`)
  - **Network Client**: Retrofit2 + OkHttp3 + Gson
  - **Background Work**: WorkManager (`SyncWorker`) with `NetworkType.CONNECTED` constraints
  - **Security**: Encrypted session management (`SessionManager`), JWT token headers, role-based screen routing
- **Backend Stack (`server/`)**:
  - **Server Environment**: Node.js / Express REST API
  - **Database**: MySQL / InnoDB Relational Database
  - **Security**: `bcryptjs` password hashing, JWT authentication middleware
  - **Concurrency Control**: MySQL InnoDB `FOR UPDATE` transaction locking enforcing max 15 members per group under simultaneous requests
  - **Sync Engine**: Idempotent operation receipts (`processed_operations`) and soft deletion tombstones (`sync_tombstones`)

---

## Setup & Running Instructions

### 1. Database & Backend Server Setup
1. **Start MySQL Database Server** (e.g., via MySQL Workbench, XAMPP, or command line).
2. **Import Database Schema**:
   ```sql
   mysql -u root -p < server/schema.sql
   ```
3. **Configure Environment Variables** (or edit `server/db.js` / `server/.env`):
   ```env
   PORT=3000
   DB_HOST=localhost
   DB_USER=root
   DB_PASSWORD=root
   DB_NAME=lab_group_manager
   JWT_SECRET=ict361_lab_group_manager_secret_key_2025
   ```
4. **Install Node.js Dependencies & Seed Database**:
   ```bash
   cd server
   npm install
   npm run seed
   ```
5. **Start Express Server**:
   ```bash
   npm start
   ```
   *Health Check*: Open `http://localhost:3000/api/health` in browser.

---

### 2. Running the Android Application
1. Open the `LabGroupManager` project in **Android Studio**.
2. Run Gradle Sync (`File -> Sync Project with Gradle Files`).
3. Start an **Android Virtual Device (Emulator)** or connect a physical Android device.
   - *Note*: The app connects to `http://10.0.2.2:3000/api/` by default when running on the Android Emulator.
4. Build and Run the App (`Shift + F10` / Run 'app').

---

## Default Login Credentials

### Lecturer Account
- **Username**: `lecturer`
- **Password**: `lecturer123`

### Sample Student Registration Claim Codes (Seeded)
- `CLAIM-20250001` (Student Number: `202500001`, Name: Mulenga Chanda, CS)
- `CLAIM-20250002` (Student Number: `202500002`, Name: Bwalya Banda, IT)
- `CLAIM-20250003` (Student Number: `202500003`, Name: Kabwe Mwape, DS)

---

## Key Features & Rules Implemented
1. **Student Registration & Offline Drafts**:
   - Register account online or save a registration draft locally when offline.
   - Links account to existing profile if student was entered beforehand by lecturer.
2. **Strict Member Capacity Enforcer (Challenge 1)**:
   - Maximum 15 active students allowed in each lab group (`G01`–`G04`).
   - Server uses MySQL InnoDB transaction locking (`FOR UPDATE`) to reject simultaneous 16th seat requests with `GROUP_FULL` error and retain previous group.
3. **Lecturer Roster & Multi-Filter Search**:
   - Search by name or 9-digit student number.
   - Combine Programme (`CS`, `IT`, `DS`) and Lab Group (`G01`–`G04`, `Unassigned`) filters.
4. **Offline Sync & Idempotency (Challenge 2 & 3)**:
   - Operation queue stored in Room (`sync_operations`). WorkManager retries synchronization automatically on reconnection.
   - Idempotency receipts prevent duplicate insertions on network retries.
   - Soft deletion tombstones prevent resurrection of deleted students.
5. **Android Sharesheet Export**:
   - Lecturer can export aggregate group count reports through the native Android Sharesheet (excludes personal data).
