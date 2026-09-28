# AI and External Source Log

In accordance with ICT361 assessment guidelines, this log records the assistance provided during the development of this project.

---

## Log Entries

### Entry 1: Architecture & Data Layer
- **Tool / Source**: Android Studio AI Assistant / Gemini Code Assistant
- **Question / Request**: "How do I implement offline sync queue with Room and WorkManager for Android in Java?"
- **Code Affected**: `SyncOperation.java`, `SyncWorker.java`, `StudentRepository.java`
- **Changes Made**: Created `sync_operations` entity storing unique `operationId`, payload JSON, base version, and status. Implemented `SyncWorker` using `Worker` with network connectivity constraints.
- **Verification**: Verified using unit tests and observing Room database entries during airplane mode testing.

### Entry 2: MySQL Transaction Lock for Capacity Bounds
- **Tool / Source**: MySQL 8.0 Documentation & AI Assistant
- **Question / Request**: "How to enforce a maximum capacity limit of 15 items under concurrent requests in Node.js Express and MySQL?"
- **Code Affected**: `server/routes/studentRoutes.js`
- **Changes Made**: Wrapped count query and insertion inside a MySQL transaction (`START TRANSACTION`) with `FOR UPDATE` row-level locking.
- **Verification**: Ran 20 parallel request iterations against group G01 initialized with 14 active members. Exactly one request succeeded and the 21st received `GROUP_FULL`.

### Entry 3: Idempotent Sync Operation Replay
- **Tool / Source**: REST API Idempotency Patterns
- **Question / Request**: "How to handle interrupted network responses so retried operations do not duplicate records?"
- **Code Affected**: `server/routes/syncRoutes.js`, `processed_operations` table
- **Changes Made**: Created `processed_operations` table storing `operation_id` receipts. On retry, the server returns the saved receipt JSON without executing duplicate mutations.
- **Verification**: Simulated interrupted responses and verified single record creation.
