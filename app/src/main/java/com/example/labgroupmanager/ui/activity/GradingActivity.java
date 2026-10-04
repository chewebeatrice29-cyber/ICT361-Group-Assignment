package com.example.labgroupmanager.ui.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.labgroupmanager.R;
import com.google.android.material.textfield.TextInputEditText;

public class GradingActivity extends AppCompatActivity {

    private TextInputEditText etGradeScore, etGradeFeedback;
    private Button btnSaveAndPublishGrade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grading);

        etGradeScore = findViewById(R.id.etGradeScore);
        etGradeFeedback = findViewById(R.id.etGradeFeedback);
        btnSaveAndPublishGrade = findViewById(R.id.btnSaveAndPublishGrade);

        btnSaveAndPublishGrade.setOnClickListener(v -> {
            String scoreStr = etGradeScore.getText() != null ? etGradeScore.getText().toString().trim() : "";
            String feedback = etGradeFeedback.getText() != null ? etGradeFeedback.getText().toString().trim() : "";

            if (scoreStr.isEmpty()) {
                Toast.makeText(this, "Please enter a grade score.", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Grade published: " + scoreStr + "/100 Marks. Feedback sent to student!", Toast.LENGTH_LONG).show();
            finish();
        });
    }
}
