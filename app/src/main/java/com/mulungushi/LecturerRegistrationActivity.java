package com.example.companionapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LecturerRegistrationActivity extends AppCompatActivity {

    private EditText etFullName, etStaffNumber, etEmail, etDepartment, etPassword, etConfirmPassword;
    private ImageView ivTogglePassword, ivToggleConfirmPassword;
    private Button btnReviewDetails;
    private TextView btnBack;

    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer_registration);

        etFullName = findViewById(R.id.etFullName);
        etStaffNumber = findViewById(R.id.etStaffNumber);
        etEmail = findViewById(R.id.etEmail);
        etDepartment = findViewById(R.id.etDepartment);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        ivTogglePassword = findViewById(R.id.ivTogglePassword);
        ivToggleConfirmPassword = findViewById(R.id.ivToggleConfirmPassword);

        btnReviewDetails = findViewById(R.id.btnReviewDetails);
        btnBack = findViewById(R.id.btnBack);

        // Password Toggle
        ivTogglePassword.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;

            if (isPasswordVisible) {
                // Show password text -> change icon to open eye / visible state immediately on 1st click
                etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                ivTogglePassword.setImageResource(R.drawable.ic_eye_open);
            } else {
                // Hide password text -> change icon to slashed eye / hidden state
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivTogglePassword.setImageResource(R.drawable.ic_eye_off);
            }

            etPassword.setSelection(etPassword.getText().length());
        });

        // Confirm Password Toggle
        ivToggleConfirmPassword.setOnClickListener(v -> {
            isConfirmPasswordVisible = !isConfirmPasswordVisible;

            if (isConfirmPasswordVisible) {
                // Show confirm password text -> change icon immediately on 1st click
                etConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                ivToggleConfirmPassword.setImageResource(R.drawable.ic_eye_open);
            } else {
                // Hide confirm password text -> change icon to slashed eye
                etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivToggleConfirmPassword.setImageResource(R.drawable.ic_eye_off);
            }

            etConfirmPassword.setSelection(etConfirmPassword.getText().length());
        });

        btnReviewDetails.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            String staffNumber = etStaffNumber.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String department = etDepartment.getText().toString().trim();
            String password = etPassword.getText().toString();
            String confirmPassword = etConfirmPassword.getText().toString();

            if (fullName.isEmpty() || staffNumber.isEmpty() || email.isEmpty() || department.isEmpty() || password.isEmpty()) {
                Toast.makeText(LecturerRegistrationActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(LecturerRegistrationActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(LecturerRegistrationActivity.this, LecturerReviewDetailsActivity.class);
            intent.putExtra("FULL_NAME", fullName);
            intent.putExtra("STAFF_NUMBER", staffNumber);
            intent.putExtra("EMAIL", email);
            intent.putExtra("DEPARTMENT", department);
            intent.putExtra("PASSWORD", password);
            startActivity(intent);
        });

        btnBack.setOnClickListener(v -> finish());
    }
}