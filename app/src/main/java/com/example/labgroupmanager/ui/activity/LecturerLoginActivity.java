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

public class LecturerLoginActivity extends AppCompatActivity {

    private TextInputEditText etLecturerLoginUser, etLecturerLoginPassword;
    private Button btnLecturerLoginSubmit;
    private ProgressBar progressBarLecturerLogin;

    private AuthViewModel authViewModel;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer_login);

        sessionManager = new SessionManager(this);

        etLecturerLoginUser = findViewById(R.id.etLecturerLoginUser);
        etLecturerLoginPassword = findViewById(R.id.etLecturerLoginPassword);
        btnLecturerLoginSubmit = findViewById(R.id.btnLecturerLoginSubmit);
        progressBarLecturerLogin = findViewById(R.id.progressBarLecturerLogin);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        authViewModel.getIsLoading().observe(this, loading -> {
            progressBarLecturerLogin.setVisibility(loading ? View.VISIBLE : View.GONE);
            btnLecturerLoginSubmit.setEnabled(!loading);
        });

        authViewModel.getAuthError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(LecturerLoginActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });

        authViewModel.getAuthSuccess().observe(this, response -> {
            if (response != null) {
                Toast.makeText(LecturerLoginActivity.this, "Welcome Lecturer " + response.getAccount().getUsername() + "!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LecturerLoginActivity.this, LecturerHomeActivity.class));
                finish();
            }
        });

        View.OnClickListener loginListener = v -> attemptLecturerLoginOrBypass();

        btnLecturerLoginSubmit.setOnClickListener(loginListener);

        etLecturerLoginPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO) {
                attemptLecturerLoginOrBypass();
                return true;
            }
            return false;
        });
    }

    private void attemptLecturerLoginOrBypass() {
        String username = etLecturerLoginUser.getText() != null ? etLecturerLoginUser.getText().toString().trim() : "";
        String password = etLecturerLoginPassword.getText() != null ? etLecturerLoginPassword.getText().toString().trim() : "";

        // If fields are blank, allow instant lecturer bypass access ("gigz")!
        if (username.isEmpty() && password.isEmpty()) {
            Toast.makeText(this, "Lecturer Admin Access Granted", Toast.LENGTH_SHORT).show();
            sessionManager.saveSession("lecturer-bypass-token", "lecturer-account-id", "gigz", "LECTURER", null);
            startActivity(new Intent(LecturerLoginActivity.this, LecturerHomeActivity.class));
            finish();
            return;
        }

        authViewModel.login(username, password);
    }
}
