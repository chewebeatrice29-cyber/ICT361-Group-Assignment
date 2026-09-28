package com.example.labgroupmanager.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.labgroupmanager.data.local.AppDatabase;
import com.example.labgroupmanager.data.local.SessionManager;
import com.example.labgroupmanager.data.local.StudentDao;
import com.example.labgroupmanager.data.model.AuthResponse;
import com.example.labgroupmanager.data.model.Student;
import com.example.labgroupmanager.data.remote.ApiClient;
import com.example.labgroupmanager.data.remote.ApiService;

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

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> authError = new MutableLiveData<>(null);
    private final MutableLiveData<AuthResponse> authSuccess = new MutableLiveData<>(null);

    public AuthViewModel(@NonNull Application application) {
        super(application);
        this.apiService = ApiClient.getService(application);
        this.sessionManager = new SessionManager(application);
        this.studentDao = AppDatabase.getInstance(application).studentDao();
    }

    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getAuthError() { return authError; }
    public LiveData<AuthResponse> getAuthSuccess() { return authSuccess; }

    public void login(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            authError.setValue("Please enter both username and password.");
            return;
        }

        isLoading.setValue(true);
        authError.setValue(null);

        Map<String, String> body = new HashMap<>();
        body.put("username", username.trim());
        body.put("password", password);

        apiService.login(body).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
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
                    authError.setValue("Invalid credentials. Please check your username and password.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                authError.setValue("Network error: Unable to connect to server. Check connection.");
            }
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
                // Save Registration Draft locally when offline!
                saveOfflineRegistrationDraft(username, studentNumber, studentName, programme);
                isLoading.setValue(false);
                authError.setValue("Offline: Registration draft saved locally! Account verification will complete once reconnected.");
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

    private void saveOfflineRegistrationDraft(String username, String number, String name, String programme) {
        Executors.newSingleThreadExecutor().execute(() -> {
            String tempId = UUID.randomUUID().toString();
            Student draft = new Student(tempId, number.trim(), name.trim(), programme, "Unassigned", null, 1, false, "SAVED_LOCALLY");
            studentDao.insertOrUpdate(draft);
        });
    }

    public void logout() {
        sessionManager.clearSession();
        Executors.newSingleThreadExecutor().execute(studentDao::clearAll);
    }
}
