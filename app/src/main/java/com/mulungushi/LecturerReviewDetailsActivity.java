package com.example.companionapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class LecturerReviewDetailsActivity extends AppCompatActivity {

    private TextView tvFullName, tvStaffNumber, tvEmail, tvDepartment, tvPassword;
    private ImageView ivToggleReviewPassword;
    private Button btnConfirmAndSubmit;
    private TextView btnBackToEdit;

    private String actualPassword = "";
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer_review_details);

        tvFullName = findViewById(R.id.tvReviewFullName);
        tvStaffNumber = findViewById(R.id.tvReviewStaffNumber);
        tvEmail = findViewById(R.id.tvReviewEmail);
        tvDepartment = findViewById(R.id.tvReviewDepartment);
        tvPassword = findViewById(R.id.tvReviewPassword);
        ivToggleReviewPassword = findViewById(R.id.ivToggleReviewPassword);
        btnConfirmAndSubmit = findViewById(R.id.btnConfirmAndSubmit);
        btnBackToEdit = findViewById(R.id.btnBackToEdit);

        // Retrieve Intent Data
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("FULL_NAME")) {
                tvFullName.setText(intent.getStringExtra("FULL_NAME"));
            }
            if (intent.hasExtra("STAFF_NUMBER")) {
                tvStaffNumber.setText(intent.getStringExtra("STAFF_NUMBER"));
            }
            if (intent.hasExtra("EMAIL")) {
                tvEmail.setText(intent.getStringExtra("EMAIL"));
            }
            if (intent.hasExtra("DEPARTMENT")) {
                tvDepartment.setText(intent.getStringExtra("DEPARTMENT"));
            }
            if (intent.hasExtra("PASSWORD")) {
                actualPassword = intent.getStringExtra("PASSWORD");
            }
        }

        if (actualPassword == null) {
            actualPassword = "";
        }

        // Render Initial Password Display
        updatePasswordDisplay();

        // Eye Icon Toggle Click Listener
        ivToggleReviewPassword.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                ivToggleReviewPassword.setImageResource(R.drawable.ic_eye_off);
            } else {
                ivToggleReviewPassword.setImageResource(R.drawable.ic_eye_open);
            }
            updatePasswordDisplay();
        });

        btnBackToEdit.setOnClickListener(v -> finish());

        btnConfirmAndSubmit.setOnClickListener(v -> {
            Intent dashboardIntent = new Intent(LecturerReviewDetailsActivity.this, LecturerDashboardActivity.class);
            dashboardIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(dashboardIntent);
        });
    }

    private void updatePasswordDisplay() {
        if (actualPassword.isEmpty()) {
            tvPassword.setText("••••••••");
            return;
        }

        if (isPasswordVisible) {
            tvPassword.setText(actualPassword);
        } else {
            StringBuilder bullets = new StringBuilder();
            for (int i = 0; i < actualPassword.length(); i++) {
                bullets.append("•");
            }
            tvPassword.setText(bullets.toString());
        }
    }
}