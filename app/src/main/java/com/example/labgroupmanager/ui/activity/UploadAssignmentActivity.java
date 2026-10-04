package com.example.labgroupmanager.ui.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.labgroupmanager.R;
import com.example.labgroupmanager.data.local.AppDatabase;
import com.example.labgroupmanager.data.model.AssignmentItem;
import com.google.android.material.textfield.TextInputEditText;

import java.util.UUID;
import java.util.concurrent.Executors;

public class UploadAssignmentActivity extends AppCompatActivity {

    private Spinner spinnerAssignCourse;
    private TextInputEditText etAssignPostTitle, etAssignInstructions, etAssignPostDueDate, etAssignMaxMarks;
    private Button btnSubmitPostAssignment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_assignment);

        spinnerAssignCourse = findViewById(R.id.spinnerAssignCourse);
        etAssignPostTitle = findViewById(R.id.etAssignPostTitle);
        etAssignInstructions = findViewById(R.id.etAssignInstructions);
        etAssignPostDueDate = findViewById(R.id.etAssignPostDueDate);
        etAssignMaxMarks = findViewById(R.id.etAssignMaxMarks);
        btnSubmitPostAssignment = findViewById(R.id.btnSubmitPostAssignment);

        ArrayAdapter<String> courseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{
                "ICT361", "CS311", "CS321", "CS331", "IT311", "IT321", "DS311", "DS321", "BMG"
        });
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAssignCourse.setAdapter(courseAdapter);

        btnSubmitPostAssignment.setOnClickListener(v -> {
            String course = spinnerAssignCourse.getSelectedItem().toString();
            String title = etAssignPostTitle.getText() != null ? etAssignPostTitle.getText().toString().trim() : "";
            String instructions = etAssignInstructions.getText() != null ? etAssignInstructions.getText().toString().trim() : "";
            String dueDate = etAssignPostDueDate.getText() != null ? etAssignPostDueDate.getText().toString().trim() : "";
            String maxMarksStr = etAssignMaxMarks.getText() != null ? etAssignMaxMarks.getText().toString().trim() : "100";

            if (title.isEmpty() || dueDate.isEmpty()) {
                Toast.makeText(this, "Please enter assignment title and deadline date.", Toast.LENGTH_SHORT).show();
                return;
            }

            int maxMarks = 100;
            try { maxMarks = Integer.parseInt(maxMarksStr); } catch (Exception ignored) {}

            AssignmentItem item = new AssignmentItem(
                    UUID.randomUUID().toString(),
                    course,
                    title,
                    instructions.isEmpty() ? "Complete assignment exercises before the deadline." : instructions,
                    dueDate,
                    maxMarks,
                    System.currentTimeMillis()
            );

            Executors.newSingleThreadExecutor().execute(() -> {
                AppDatabase.getInstance(getApplicationContext()).assignmentDao().insertAssignment(item);
                runOnUiThread(() -> {
                    Toast.makeText(UploadAssignmentActivity.this, "Assignment published! Deadline set to " + dueDate + ".", Toast.LENGTH_LONG).show();
                    finish();
                });
            });
        });
    }
}
