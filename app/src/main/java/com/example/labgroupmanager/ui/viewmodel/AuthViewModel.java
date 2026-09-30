package com.example.labgroupmanager.ui.viewmodel;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.labgroupmanager.data.local.AppDatabase;
import com.example.labgroupmanager.data.local.SessionManager;
import com.example.labgroupmanager.data.local.StudentDao;
import com.example.labgroupmanager.data.local.SyncOperationDao;
import com.example.labgroupmanager.data.local.UserAccountDao;
import com.example.labgroupmanager.data.model.AuthResponse;
import com.example.labgroupmanager.data.model.Student;
import com.example.labgroupmanager.data.model.SyncOperation;
import com.example.labgroupmanager.data.model.UserAccount;
import com.example.labgroupmanager.data.remote.ApiClient;
import com.example.labgroupmanager.data.remote.ApiService;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthViewModel extends AndroidViewModel {

    private final ApiService apiService;
    private final SessionManager sessionManager;
    private final StudentDao studentDao;
    private final UserAccountDao userAccountDao;
    private final SyncOperationDao syncOperationDao;
    private final Gson gson;

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> authError = new MutableLiveData<>(null);
    private final MutableLiveData<AuthResponse> authSuccess = new MutableLiveData<>(null);

    public AuthViewModel(@NonNull Application application) {
        super(application);
        this.apiService = ApiClient.getService(application);
        this.sessionManager = new SessionManager(application);
        AppDatabase db = AppDatabase.getInstance(application);
        this.studentDao = db.studentDao();
        this.userAccountDao = db.userAccountDao();
        this.syncOperationDao = db.syncOperationDao();
        this.gson = new Gson();

        // Seed default offline local accounts into Room database if empty
        seedLocalPhoneAccounts();
    }

    private void seedLocalPhoneAccounts() {
        Executors.newSingleThreadExecutor().execute(() -> {
            if (userAccountDao.getAccountByUsername("gigz") == null) {
                userAccountDao.insertAccount(new UserAccount("acc-gigz", "gigz", "LECTURER", "12345678"));
            }
            if (userAccountDao.getAccountByUsername("lecturer") == null) {
                userAccountDao.insertAccount(new UserAccount("acc-lecturer", "lecturer", "LECTURER", "lecturer123"));
            }
            if (userAccountDao.getAccountByUsername("mchanda") == null) {
                String studentAccId = "acc-mchanda";
                userAccountDao.insertAccount(new UserAccount(studentAccId, "mchanda", "STUDENT", "password123"));
                if (studentDao.getStudentByNumber("202500001") == null) {
                    studentDao.insertOrUpdate(new Student("s-202500001", "202500001", "Mulenga Chanda", "CS", "G01", studentAccId, 1, false, "SYNCED"));
                }
            }

            // Seed Requested Student Account ("202109428" / "12345678") -> Student Home Page
            if (userAccountDao.getAccountByUsername("202109428") == null) {
                String studentAccId = "acc-202109428";
                userAccountDao.insertAccount(new UserAccount(studentAccId, "202109428", "STUDENT", "12345678"));
                if (studentDao.getStudentByNumber("202109428") == null) {
                    studentDao.insertOrUpdate(new Student("s-202109428", "202109428", "James Banda", "CS", "G01", studentAccId, 1, false, "SYNCED"));
                }
            }
        });
    }

    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getAuthError() { return authError; }
    public LiveData<AuthResponse> getAuthSuccess() { return authSuccess; }

    public void login(String username, String password) {
        final String cleanUser = username != null ? username.trim() : "";
        final String cleanPass = password != null ? password.trim() : "";

        if (cleanUser.isEmpty() || cleanPass.isEmpty()) {
            authError.setValue("Please enter both username and password.");
            return;
        }

        isLoading.setValue(true);
        authError.setValue(null);

        Map<String, String> body = new HashMap<>();
        body.put("username", cleanUser);
        body.put("password", cleanPass);

        apiService.login(body).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    isLoading.setValue(false);
                    AuthResponse res = response.body();
                    String studentId = res.getStudent() != null ? res.getStudent().getStudentId() : null;
                    sessionManager.saveSession(
                            res.getToken(),
                            res.getAccount().getAccountId(),
                            res.getAccount().getUsername(),
                            res.getAccount().getRole(),
                            studentId
                    );

                    // Cache student in Room if present
                    if (res.getStudent() != null) {
                        Student s = res.getStudent();
                        s.setSyncStatus("SYNCED");
                        Executors.newSingleThreadExecutor().execute(() -> studentDao.insertOrUpdate(s));
                    }

                    authSuccess.setValue(res);
                } else {
                    // Try offline login from phone memory
                    attemptOfflineRoomLogin(cleanUser, cleanPass);
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                // Server unavailable: Perform offline login from phone memory
                attemptOfflineRoomLogin(cleanUser, cleanPass);
            }
        });
    }

    private void attemptOfflineRoomLogin(String username, String password) {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Check by username
            UserAccount account = userAccountDao.getAccountByUsername(username);

            // Also check by student number if student entered student number
            if (account == null) {
                Student studentByNum = studentDao.getStudentByNumber(username);
                if (studentByNum != null && studentByNum.getAccountId() != null) {
                    account = userAccountDao.getAccountById(studentByNum.getAccountId());
                }
            }

            final UserAccount localAccount = account;

            new Handler(Looper.getMainLooper()).post(() -> {
                isLoading.setValue(false);
                if (localAccount != null && (localAccount.getPasswordHash() == null || localAccount.getPasswordHash().equals(password))) {
                    // Fetch linked student profile if STUDENT role
                    Executors.newSingleThreadExecutor().execute(() -> {
                        Student studentProfile = null;
                        if ("STUDENT".equals(localAccount.getRole())) {
                            studentProfile = studentDao.getStudentByAccountId(localAccount.getAccountId());
                        }

                        final Student linkedStudent = studentProfile;
                        String studentId = linkedStudent != null ? linkedStudent.getStudentId() : null;

                        sessionManager.saveSession(
                                "offline-login-token-" + localAccount.getAccountId(),
                                localAccount.getAccountId(),
                                localAccount.getUsername(),
                                localAccount.getRole(),
                                studentId
                        );

                        new Handler(Looper.getMainLooper()).post(() -> {
                            authError.setValue("Login Successful!");
                            AuthResponse syntheticRes = new AuthResponse();
                            authSuccess.setValue(syntheticRes);
                        });
                    });
                } else if (localAccount != null) {
                    authError.setValue("Incorrect password for local offline account.");
                } else {
                    authError.setValue("Invalid credentials. Account not found online or in phone memory.");
                }
            });
        });
    }

    public void registerStudent(String claimCode, String username, String password, String studentNumber, String studentName, String programme) {
        if (!validateRegistrationFields(studentNumber, studentName, programme)) return;

        isLoading.setValue(true);
        authError.setValue(null);

        Map<String, String> body = new HashMap<>();
        body.put("claimCode", claimCode != null ? claimCode.trim() : "");
        body.put("username", username.trim());
        body.put("password", password);
        body.put("studentNumber", studentNumber.trim());
        body.put("studentName", studentName.trim());
        body.put("programme", programme);

        apiService.registerStudent(body).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    AuthResponse res = response.body();
                    sessionManager.saveSession(
                            res.getToken(),
                            res.getAccount().getAccountId(),
                            res.getAccount().getUsername(),
                            res.getAccount().getRole(),
                            res.getStudent() != null ? res.getStudent().getStudentId() : null
                    );

                    if (res.getStudent() != null) {
                        Student s = res.getStudent();
                        s.setSyncStatus("SYNCED");
                        Executors.newSingleThreadExecutor().execute(() -> studentDao.insertOrUpdate(s));
                    }

                    authSuccess.setValue(res);
                } else {
                    authError.setValue("Registration failed: " + (response.body() != null ? response.body().getMessage() : "Invalid verification details."));
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                // Network unavailable: Create Account Locally in Phone Memory & Queue Sync!
                saveOfflineAccountAndProfile(username, password, studentNumber, studentName, programme);
            }
        });
    }

    private boolean validateRegistrationFields(String number, String name, String programme) {
        if (number == null || !number.trim().matches("^\\d{9}$")) {
            authError.setValue("Student number must be exactly 9 digits with no spaces.");
            return false;
        }
        if (name == null || name.trim().length() < 2 || name.trim().length() > 100) {
            authError.setValue("Student name must be between 2 and 100 characters.");
            return false;
        }
        if (programme == null || (!programme.equals("CS") && !programme.equals("IT") && !programme.equals("DS"))) {
            authError.setValue("Please select a valid programme (CS, IT, DS).");
            return false;
        }
        return true;
    }

    private void saveOfflineAccountAndProfile(String username, String password, String number, String name, String programme) {
        Executors.newSingleThreadExecutor().execute(() -> {
            String accountId = UUID.randomUUID().toString();
            String studentId = UUID.randomUUID().toString();

            // 1. Store User Account locally in phone memory
            UserAccount account = new UserAccount(accountId, username.trim(), "STUDENT", password);
            userAccountDao.insertAccount(account);

            // 2. Store Student Profile locally in phone memory
            Student student = new Student(studentId, number.trim(), name.trim(), programme, "Unassigned", accountId, 1, false, "SAVED_LOCALLY");
            studentDao.insertOrUpdate(student);

            // 3. Queue Sync Operation for WorkManager
            Map<String, String> payload = new HashMap<>();
            payload.put("studentNumber", number.trim());
            payload.put("studentName", name.trim());
            payload.put("programme", programme);
            payload.put("labGroup", "Unassigned");

            SyncOperation op = new SyncOperation(
                    UUID.randomUUID().toString(),
                    studentId,
                    accountId,
                    "CREATE_STUDENT",
                    gson.toJson(payload),
                    1,
                    "PENDING",
                    System.currentTimeMillis()
            );
            syncOperationDao.insertOperation(op);

            // 4. Save Session state
            sessionManager.saveSession("offline-token-" + accountId, accountId, username.trim(), "STUDENT", studentId);

            // 5. Trigger UI success callback on main thread
            new Handler(Looper.getMainLooper()).post(() -> {
                isLoading.setValue(false);
                authError.setValue("Offline Account Created! Stored in phone memory and queued for server sync.");
                // Construct synthetic AuthResponse for UI navigation
                AuthResponse syntheticRes = new AuthResponse();
                authSuccess.setValue(syntheticRes);
            });
        });
    }

    public void logout() {
        sessionManager.clearSession();
        Executors.newSingleThreadExecutor().execute(studentDao::clearAll);
    }
}
