package com.example.companionapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class CreateAccountActivity extends AppCompatActivity {

    private CardView cardStudent, cardLecturer;
    private TextView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // Initialize UI Views
        cardStudent = findViewById(R.id.cardStudent);
        cardLecturer = findViewById(R.id.cardLecturer);
        btnBack = findViewById(R.id.btnBack);

        // Open Student Registration
        cardStudent.setOnClickListener(v -> {
            Intent intent = new Intent(CreateAccountActivity.this, StudentRegistrationActivity.class);
            startActivity(intent);
        });

        // Open Lecturer Registration
        cardLecturer.setOnClickListener(v -> {
            Intent intent = new Intent(CreateAccountActivity.this, LecturerRegistrationActivity.class);
            startActivity(intent);
        });

        // Go Back to previous activity
        btnBack.setOnClickListener(v -> finish());
    }
}