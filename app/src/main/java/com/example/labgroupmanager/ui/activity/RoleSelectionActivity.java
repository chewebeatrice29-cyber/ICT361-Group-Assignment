package com.example.labgroupmanager.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.labgroupmanager.R;
import com.google.android.material.card.MaterialCardView;

public class RoleSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_selection);

        MaterialCardView cardStudentPortal = findViewById(R.id.cardStudentPortal);
        MaterialCardView cardLecturerPortal = findViewById(R.id.cardLecturerPortal);
        TextView tvBackToLogin = findViewById(R.id.tvBackToLogin);

        if (cardStudentPortal != null) {
            cardStudentPortal.setOnClickListener(v ->
                    startActivity(new Intent(RoleSelectionActivity.this, RegisterActivity.class))
            );
        }

        if (cardLecturerPortal != null) {
            cardLecturerPortal.setOnClickListener(v ->
                    startActivity(new Intent(RoleSelectionActivity.this, RegisterActivity.class))
            );
        }

        if (tvBackToLogin != null) {
            tvBackToLogin.setOnClickListener(v -> finish());
        }
    }
}
