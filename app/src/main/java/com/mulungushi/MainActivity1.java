package com.example.companionapp;

import com.example.companionapp.profileactivity.StudentHomeActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class MainActivity extends AppCompatActivity {

    private EditText etIdentity, etPassword;
    private AppCompatButton btnLogin, btnCreateAccount;
    private TextView tvForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_companion_main);

        // Initialize Views
        etIdentity = findViewById(R.id.etIdentity);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);

        // Forgot Password Click Listener -> Navigates to ForgotPasswordActivity
        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        // Create Account Click Listener -> Navigates to CreateAccountActivity
        if (btnCreateAccount != null) {
            btnCreateAccount.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, CreateAccountActivity.class);
                startActivity(intent);
            });
        }

        // Login Button Click Listener -> Navigates to LecturerDashboardActivity
        btnLogin.setOnClickListener(v -> {
            String identity = etIdentity.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (identity.isEmpty()) {
                etIdentity.setError("Identity is required");
                return;
            }

            if (password.isEmpty()) {
                etPassword.setError("Password is required");
                return;
            }

            // Navigate to Dashboard
            Intent intent;
            if (identity.toLowerCase().contains("lecturer") || identity.toLowerCase().startsWith("lec")) {
                intent = new Intent(MainActivity.this, LecturerDashboardActivity.class);
            } else {
                intent = new Intent(MainActivity.this, StudentHomeActivity.class);
            }
            startActivity(intent);
            finish();
        });
    }
}