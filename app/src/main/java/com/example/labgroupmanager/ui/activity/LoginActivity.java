package com.example.labgroupmanager.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.labgroupmanager.R;
import com.example.labgroupmanager.data.local.SessionManager;
import com.example.labgroupmanager.ui.viewmodel.AuthViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private TextView tvForgotPassword;
    private Button btnLogin;
    private Button btnCreateAccount;
    private ProgressBar progressBar;

    private AuthViewModel authViewModel;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);

        // Check active session on launch
        if (sessionManager.isLoggedIn()) {
            navigateToHome();
            return;
        }

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        progressBar = findViewById(R.id.progressBarLogin);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        authViewModel.getIsLoading().observe(this, loading -> {
            progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
            btnLogin.setEnabled(!loading);
        });

        authViewModel.getAuthError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });

        authViewModel.getAuthSuccess().observe(this, response -> {
            if (response != null) {
                Toast.makeText(LoginActivity.this, "Welcome " + response.getAccount().getUsername() + "!", Toast.LENGTH_SHORT).show();
                navigateToHome();
            }
        });

        btnLogin.setOnClickListener(v -> attemptLoginOrBypass());

        etPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO) {
                attemptLoginOrBypass();
                return true;
            }
            return false;
        });

        tvForgotPassword.setOnClickListener(v -> {
            Toast.makeText(this, "Password Reset: Contact administrator or check registered email.", Toast.LENGTH_LONG).show();
        });

        btnCreateAccount.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RoleSelectionActivity.class));
        });
    }

    private void attemptLoginOrBypass() {
        String inputId = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        // If no credentials entered, allow guest/admin bypass
        if (inputId.isEmpty() && password.isEmpty()) {
            Toast.makeText(this, "Login Access Granted (Guest Mode)", Toast.LENGTH_SHORT).show();
            sessionManager.saveSession("bypass-token", "admin-account-id", "gigz", "LECTURER", null);
            navigateToHome();
            return;
        }

        authViewModel.login(inputId, password);
    }

    private void navigateToHome() {
        Intent intent;
        if (sessionManager.isLecturer()) {
            intent = new Intent(this, LecturerHomeActivity.class);
        } else {
            intent = new Intent(this, StudentHomeActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
