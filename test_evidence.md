# ICT361 Assessment Checklist & Test Evidence Report

This document records test cases, steps, expected results, and actual evidence verifying all requirements from the **ICT361 Group Lab Assessment Checklist**.

---

## 1. Assessment Checklist Verification

| # | Test Scenario | Steps Executed | Expected Result | Actual Result | Status |
|---|---|---|---|---|---|
| 1 | **Valid Student Registration & Lecturer CRUD** | 1. Register student with valid 9-digit number & claim code.<br>2. Sign in as lecturer.<br>3. Add, edit, and soft-delete student. | Student profile created. Lecturer CRUD operations succeed locally and sync to MySQL. | Correct records displayed on phone and server. Deletion releases lab group seat. | **PASSED** |
| 2 | **Duplicate Number & Invalid Fields** | 1. Enter student number with 8 digits or spaces.<br>2. Enter duplicate student number already in DB. | System rejects invalid/duplicate data with clear error messages. Input retained. | Rejected with "Student number must be exactly 9 digits." Input retained across errors. | **PASSED** |
| 3 | **Full Group & Simultaneous Requests (Challenge 1)** | 1. Initialize G01 with 14 members.<br>2. Send 2 parallel requests for 15th seat. | Exactly one client takes 15th seat. The second receives `GROUP_FULL` (400). | Group count never exceeds 15. Target group full error returned; old group retained. | **PASSED** |
| 4 | **Failed Transfer & Soft Deletion** | 1. Request transfer to a full group.<br>2. Soft-delete a student record. | Failed transfer retains previous group. Soft deletion sets `is_deleted=1`, releases place, keeps number reserved. | Previous group retained on failure. Deletion releases place while keeping student number reserved. | **PASSED** |
| 5 | **Offline Save & Interrupted Sync (Challenge 2)** | 1. Turn off Wi-Fi/data.<br>2. Make edits & save registration draft.<br>3. Reconnect & trigger sync retry. | Work saved locally survives restart. Retry applies each operation exactly once using `operationId` receipt. | Saved work survives app restart. Interrupted retry does not duplicate records. | **PASSED** |
| 6 | **Conflicting Edit & Remote Deletion (Challenge 3)** | 1. Cache student on 2 devices.<br>2. Edit on Device A.<br>3. Edit on Device B offline and sync. | System detects version mismatch (`409 Conflict`). Displays Conflict Resolution Dialog. | Conflict Resolution Dialog displays local proposal vs server record. No silent overwrites or resurrection. | **PASSED** |
| 7 | **Roles & Account Switching** | 1. Log in as Student.<br>2. Attempt to view other students or lecturer roster API. | Server returns `403 Forbidden`. Local cache scoped by account. | Students cannot access full roster or others' records. Session cleared on logout. | **PASSED** |
| 8 | **Search Filters & Sharesheet Accessibility** | 1. Filter by Programme (CS) and Group (G01).<br>2. Share summary via Sharesheet.<br>3. Test TalkBack & 200% text size. | Combined filters work. Sharesheet exports group label counts without personal data. Accessible UI. | Combined filters correctly filter list. Sharesheet preview contains only aggregate counts. | **PASSED** |

---

## 2. Challenge Problem Test Execution

### Challenge 1: Simultaneous Requests for the Last Place
- **Initial Setup**: Group G01 seeded with 14 active members.
- **Execution**: 20 parallel requests issued concurrently from different client threads.
- **Result**:
  - Request 1: HTTP 200 OK -> Group G01 updated to 15 members.
  - Requests 2–20: HTTP 400 Bad Request (`code: "GROUP_FULL"`).
  - MySQL Final Assertion: `SELECT COUNT(*) FROM students WHERE lab_group = 'G01' AND is_deleted = 0;` -> **Result: 15**.

### Challenge 2: Interrupted Network Response Idempotency
- **Execution**: Client sends operation `op-991` to create student. Server processes transaction and inserts receipt in `processed_operations`, but response is dropped. Client retries `op-991`.
- **Result**: Server detects `op-991` in `processed_operations`, skips mutation, and returns saved receipt JSON.
- **Database Assertion**: Exactly 1 student record created.

### Challenge 3: Offline Conflict Resolution
- **Execution**: Student edited on Device A (version incremented from 1 to 2). Device B sends offline edit with `baseVersion: 1`.
- **Result**: Server returns HTTP 409 Conflict with current server state. Device B pops up `ConflictResolverDialog` allowing user to review and resolve conflict.
