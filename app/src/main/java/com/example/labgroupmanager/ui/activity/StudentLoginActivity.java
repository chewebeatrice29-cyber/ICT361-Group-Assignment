package com.example.labgroupmanager.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.labgroupmanager.R;
import com.example.labgroupmanager.data.local.SessionManager;
import com.example.labgroupmanager.ui.viewmodel.AuthViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class StudentLoginActivity extends AppCompatActivity {

    private TextInputEditText etStudentLoginNumber, etStudentLoginPassword;
    private Button btnStudentLoginSubmit, btnStudentGoToRegister;
    private ProgressBar progressBarStudentLogin;

    private AuthViewModel authViewModel;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);

        sessionManager = new SessionManager(this);

        etStudentLoginNumber = findViewById(R.id.etStudentLoginNumber);
        etStudentLoginPassword = findViewById(R.id.etStudentLoginPassword);
        btnStudentLoginSubmit = findViewById(R.id.btnStudentLoginSubmit);
        btnStudentGoToRegister = findViewById(R.id.btnStudentGoToRegister);
        progressBarStudentLogin = findViewById(R.id.progressBarStudentLogin);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        authViewModel.getIsLoading().observe(this, loading -> {
            progressBarStudentLogin.setVisibility(loading ? View.VISIBLE : View.GONE);
            btnStudentLoginSubmit.setEnabled(!loading);
        });

        authViewModel.getAuthError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(StudentLoginActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });

        authViewModel.getAuthSuccess().observe(this, response -> {
            if (response != null) {
                String username = (response.getAccount() != null && response.getAccount().getUsername() != null)
                        ? response.getAccount().getUsername()
                        : sessionManager.getUsername();
                if (username == null || username.trim().isEmpty()) {
                    username = "Student";
                }
                Toast.makeText(StudentLoginActivity.this, "Welcome " + username + "!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(StudentLoginActivity.this, StudentHomeActivity.class));
                finish();
            }
        });

        View.OnClickListener loginListener = v -> attemptStudentLoginOrBypass();

        btnStudentLoginSubmit.setOnClickListener(loginListener);

        etStudentLoginPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO) {
                attemptStudentLoginOrBypass();
                return true;
            }
            return false;
        });

        btnStudentGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(StudentLoginActivity.this, RegisterActivity.class));
        });
    }

    private void attemptStudentLoginOrBypass() {
        String numberOrUser = etStudentLoginNumber.getText() != null ? etStudentLoginNumber.getText().toString().trim() : "";
        String password = etStudentLoginPassword.getText() != null ? etStudentLoginPassword.getText().toString().trim() : "";

        // If fields are blank, allow instant student guest access!
        if (numberOrUser.isEmpty() && password.isEmpty()) {
            Toast.makeText(this, "Student Guest Access Granted", Toast.LENGTH_SHORT).show();
            sessionManager.saveSession("student-bypass-token", "student-account-id", "student_guest", "STUDENT", "s-guest-1");
            startActivity(new Intent(StudentLoginActivity.this, StudentHomeActivity.class));
            finish();
            return;
        }

        authViewModel.login(numberOrUser, password);
    }
}
