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

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private Button btnLogin;
    private Button btnGoToRegister;
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
        btnLogin = findViewById(R.id.btnLogin);
        btnGoToRegister = findViewById(R.id.btnGoToRegister);
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

        View.OnClickListener loginClickListener = v -> attemptLoginOrBypass();

        btnLogin.setOnClickListener(loginClickListener);

        etPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_NEXT) {
                attemptLoginOrBypass();
                return true;
            }
            return false;
        });

        btnGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void attemptLoginOrBypass() {
        String username = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        // If no credentials have been entered, allow guest / admin bypass access immediately!
        if (username.isEmpty() && password.isEmpty()) {
            Toast.makeText(this, "Lecturer Guest Access Granted (Bypassed)", Toast.LENGTH_SHORT).show();
            // Save admin session as guest/admin ("gigz" / LECTURER)
            sessionManager.saveSession("guest-bypass-token", "admin-account-id", "gigz", "LECTURER", null);
            navigateToHome();
            return;
        }

        authViewModel.login(username, password);
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
