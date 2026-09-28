package com.example.labgroupmanager.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.labgroupmanager.R;
import com.example.labgroupmanager.data.local.SessionManager;
import com.google.android.material.card.MaterialCardView;

public class RoleSelectionActivity extends AppCompatActivity {

    private MaterialCardView cardStudentPortal, cardLecturerPortal;
    private CheckBox cbRememberChoice;
    private Button btnQuickBypass;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_selection);

        sessionManager = new SessionManager(this);

        cardStudentPortal = findViewById(R.id.cardStudentPortal);
        cardLecturerPortal = findViewById(R.id.cardLecturerPortal);
        cbRememberChoice = findViewById(R.id.cbRememberChoice);
        btnQuickBypass = findViewById(R.id.btnQuickBypass);

        cardStudentPortal.setOnClickListener(v -> {
            if (cbRememberChoice.isChecked()) {
                sessionManager.setRememberedPortal("STUDENT");
            }
            startActivity(new Intent(RoleSelectionActivity.this, StudentLoginActivity.class));
        });

        cardLecturerPortal.setOnClickListener(v -> {
            if (cbRememberChoice.isChecked()) {
                sessionManager.setRememberedPortal("LECTURER");
            }
            startActivity(new Intent(RoleSelectionActivity.this, LecturerLoginActivity.class));
        });

        btnQuickBypass.setOnClickListener(v -> {
            Toast.makeText(this, "Admin Access Granted", Toast.LENGTH_SHORT).show();
            sessionManager.saveSession("bypass-token", "admin-id", "gigz", "LECTURER", null);
            startActivity(new Intent(RoleSelectionActivity.this, LecturerHomeActivity.class));
            finish();
        });
    }
}
