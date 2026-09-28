# Project Group Contribution Log & Sub-Team Task Matrix
**Course**: ICT361 Mobile Application Development  
**Project**: Android Student Registration and Lab Group Management System  
**Group Size**: 15 Members (Divided into 5 Sub-Teams of 3)

---

## Sub-Team 1: UI/UX & Accessibility Team
1. **Mulenga Chanda (SIN: 202500001)**: Lead UI Designer. Created XML layouts for `activity_login.xml`, `activity_register.xml`, and Material Design themes.
2. **Bwalya Banda (SIN: 202500002)**: Implemented `activity_student_profile.xml` and group change UI controls.
3. **Kabwe Mwape (SIN: 202500003)**: Handled TalkBack accessibility strings, large text scaling testing (200%), content descriptions, and form error feedback.

## Sub-Team 2: Android Architecture & State Management Team
4. **Natasha Phiri (SIN: 202500004)**: Built `AuthViewModel` and `StudentViewModel`, ensuring form state survives configuration changes (rotation).
5. **Chisomo Zulu (SIN: 202500005)**: Implemented `StudentRepository` pattern coordinating local and remote data sources.
6. **Thandiwe Musonda (SIN: 202500006)**: Developed `StudentAdapter` with DiffUtil for efficient RecyclerView rendering.

## Sub-Team 3: Local Storage & Sync Queue Team
7. **Kondwani Tembo (SIN: 202500007)**: Designed Room entities (`Student`, `SyncOperation`) and `StudentDao` queries.
8. **Mapalo Katongo (SIN: 202500008)**: Implemented `SessionManager` for secure JWT storage and account scoping.
9. **Lombe Chilufya (SIN: 202500009)**: Built `SyncWorker` (WorkManager) for persistent background synchronization and retry backoff.

## Sub-Team 4: Backend & Database Engineering Team
10. **Subilo Kasonde (SIN: 202500010)**: Created MySQL schema (`schema.sql`), foreign key constraints, and seed scripts (`seed.js`).
11. **Wezi Mwanza (SIN: 202500011)**: Implemented Node.js Express routes (`authRoutes.js`, `studentRoutes.js`) with bcrypt hashing and JWT verification.
12. **Taonga Phiri (SIN: 202500012)**: Designed InnoDB `FOR UPDATE` transaction locks enforcing the strict 15-student group capacity rule and idempotency receipts.

## Sub-Team 5: Testing, Security & Integration Team
13. **Mainza Hachambo (SIN: 202500013)**: Conducted unit testing, input validation checks (9-digit student numbers), and fake repository tests.
14. **Nchimunya Moonga (SIN: 202500014)**: Verified Challenge 1 (simultaneous group requests), Challenge 2 (interrupted sync retries), and Challenge 3 (conflict resolution).
15. **Chileshe Mwewa (SIN: 202500015)**: Integrated Android Sharesheet summary export, verified role-based access control, and prepared documentation deliverables.
