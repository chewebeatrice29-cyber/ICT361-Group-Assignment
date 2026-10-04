package com.example.labgroupmanager.ui.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.labgroupmanager.R;
import com.google.android.material.textfield.TextInputEditText;

public class AssignmentManagerActivity extends AppCompatActivity {

    private TextInputEditText etAssignTitle, etAssignDueDate;
    private Spinner spinnerAssignStatus;
    private Button btnSubmitAssignment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_manager);

        etAssignTitle = findViewById(R.id.etAssignTitle);
        etAssignDueDate = findViewById(R.id.etAssignDueDate);
        spinnerAssignStatus = findViewById(R.id.spinnerAssignStatus);
        btnSubmitAssignment = findViewById(R.id.btnSubmitAssignment);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{
                "✔ Submitted & Graded", "⏱ Pending Submission", "✔ Submitted (Awaiting Grade)"
        });
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAssignStatus.setAdapter(statusAdapter);

        btnSubmitAssignment.setOnClickListener(v -> {
            String title = etAssignTitle.getText() != null ? etAssignTitle.getText().toString().trim() : "";
            String dueDate = etAssignDueDate.getText() != null ? etAssignDueDate.getText().toString().trim() : "";
            String status = spinnerAssignStatus.getSelectedItem().toString();

            if (title.isEmpty()) {
                Toast.makeText(this, "Please enter an assignment title.", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Assignment Saved: " + title + " (" + status + ") Due: " + dueDate, Toast.LENGTH_LONG).show();
            finish();
        });
    }
}
