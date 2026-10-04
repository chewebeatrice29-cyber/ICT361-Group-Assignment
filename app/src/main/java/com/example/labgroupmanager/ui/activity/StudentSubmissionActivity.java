package com.example.labgroupmanager.ui.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.labgroupmanager.R;
import com.example.labgroupmanager.data.local.AppDatabase;
import com.example.labgroupmanager.data.local.SessionManager;
import com.example.labgroupmanager.data.model.SubmissionItem;
import com.google.android.material.textfield.TextInputEditText;

import java.util.UUID;
import java.util.concurrent.Executors;

public class StudentSubmissionActivity extends AppCompatActivity {

    private TextInputEditText etSubResponseText, etSubCodeLink;
    private Button btnSubmitWork;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_submission);

        sessionManager = new SessionManager(this);

        etSubResponseText = findViewById(R.id.etSubResponseText);
        etSubCodeLink = findViewById(R.id.etSubCodeLink);
        btnSubmitWork = findViewById(R.id.btnSubmitWork);

        btnSubmitWork.setOnClickListener(v -> {
            String text = etSubResponseText.getText() != null ? etSubResponseText.getText().toString().trim() : "";
            String link = etSubCodeLink.getText() != null ? etSubCodeLink.getText().toString().trim() : "";

            if (text.isEmpty() && link.isEmpty()) {
                Toast.makeText(this, "Please enter response notes or link before submitting.", Toast.LENGTH_SHORT).show();
                return;
            }

            String studentId = sessionManager.getStudentId() != null ? sessionManager.getStudentId() : "s-202109428";
            String studentNum = sessionManager.getUsername() != null ? sessionManager.getUsername() : "202109428";

            SubmissionItem submission = new SubmissionItem(
                    UUID.randomUUID().toString(),
                    "ASSIGN_ICT361_LAB3",
                    studentId,
                    studentNum,
                    "James Banda",
                    text.isEmpty() ? "ICT361 Lab Practical 3 Implementation Completed." : text,
                    link.isEmpty() ? "https://github.com/jamesbanda/ICT361_Lab3.git" : link,
                    System.currentTimeMillis(),
                    "SUBMITTED",
                    0,
                    "Pending Lecturer Grading"
            );

            Executors.newSingleThreadExecutor().execute(() -> {
                AppDatabase.getInstance(getApplicationContext()).submissionDao().insertSubmission(submission);
                runOnUiThread(() -> {
                    Toast.makeText(StudentSubmissionActivity.this, "Work submitted successfully! Pending lecturer grading.", Toast.LENGTH_LONG).show();
                    finish();
                });
            });
        });
    }
}
