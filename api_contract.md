# ICT361 REST API Contract Specification

**Base URL**: `http://localhost:3000/api/` (Android Emulator: `http://10.0.2.2:3000/api/`)  
**Authentication**: Bearer Token in `Authorization` header (`Authorization: Bearer <jwt_token>`).

---

## 1. Authentication Endpoints

### `POST /api/auth/register`
- **Role Permitted**: Public / Anonymous
- **Description**: Registers a new student account using a claim code or links to an existing profile created by a lecturer.
- **Request Body**:
  ```json
  {
    "claimCode": "CLAIM-20250001",
    "username": "mchanda",
    "password": "password123",
    "studentNumber": "202500001",
    "studentName": "Mulenga Chanda",
    "programme": "CS"
  }
  ```
- **Response (201 Created)**:
  ```json
  {
    "success": true,
    "message": "Registration successful.",
    "token": "eyJhbGciOiJIUzI1Ni...",
    "account": { "accountId": "uuid-1", "username": "mchanda", "role": "STUDENT" },
    "student": { "studentId": "s-1", "studentNumber": "202500001", "studentName": "Mulenga Chanda", "programme": "CS", "labGroup": "Unassigned", "version": 1 }
  }
  ```

### `POST /api/auth/login`
- **Role Permitted**: Public
- **Request Body**:
  ```json
  { "username": "lecturer", "password": "lecturer123" }
  ```
- **Response (200 OK)**:
  ```json
  {
    "success": true,
    "token": "eyJhbGciOiJIUzI1Ni...",
    "account": { "accountId": "uuid-lec", "username": "lecturer", "role": "LECTURER" },
    "student": null
  }
  ```

---

## 2. Student CRUD & Filter Endpoints

### `GET /api/students`
- **Role Permitted**: `LECTURER`
- **Query Parameters**: `search` (string), `group` (`G01`-`G04`, `Unassigned`, `All`), `programme` (`CS`, `IT`, `DS`, `All`), `page` (int), `limit` (int).
- **Response (200 OK)**:
  ```json
  {
    "success": true,
    "totalRecords": 12,
    "page": 1,
    "totalPages": 1,
    "students": [
      {
        "studentId": "s-1",
        "studentNumber": "202500001",
        "studentName": "Mulenga Chanda",
        "programme": "CS",
        "labGroup": "G01",
        "version": 1
      }
    ],
    "groupCounts": { "G01": 12, "G02": 8, "G03": 5, "G04": 2, "Unassigned": 3 }
  }
  ```

### `GET /api/students/summary`
- **Role Permitted**: Authenticated User
- **Response (200 OK)**: Returns active group counts for Sharesheet preview (excludes personal data).

### `POST /api/students`
- **Role Permitted**: `LECTURER`
- **Capacity Check**: InnoDB transaction locks target group and rejects with `400 Bad Request` if member count >= 15.

### `PUT /api/students/:id`
- **Role Permitted**: `LECTURER` (any student) or `STUDENT` (own profile only)
- **Concurrency Control**: Returns `409 Conflict` if `baseVersion` < current server version.

### `POST /api/students/:id/group`
- **Role Permitted**: Authenticated User (own profile or lecturer)
- **Capacity Enforcement**: Enforces 15 member limit. Retains previous group if target group is full.

### `DELETE /api/students/:id`
- **Role Permitted**: `LECTURER`
- **Behavior**: Soft deletes record (`is_deleted=1`), releases lab group seat (`Unassigned`), disables account, keeps student number reserved.

---

## 3. Sync Endpoints

### `POST /api/sync/batch`
- **Role Permitted**: Authenticated User
- **Idempotency**: Checked against `processed_operations` table by `operationId`. Interrupted retries return saved receipt without repeating mutations.

### `GET /api/sync/pull`
- **Role Permitted**: Authenticated User
- **Query Parameter**: `since` (ISO timestamp)
- **Response (200 OK)**: Returns updated active students and soft deletion tombstones.
