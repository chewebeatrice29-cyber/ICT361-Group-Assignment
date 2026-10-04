package com.example.labgroupmanager.ui.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.labgroupmanager.R;
import com.example.labgroupmanager.ui.viewmodel.StudentViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class GroupTransferActivity extends AppCompatActivity {

    private TextView tvTransferStudentName, tvTransferProgramme, tvTransferCurrentGroup;
    private Spinner spinnerNewGroup;
    private TextInputEditText etTransferReason;
    private Button btnTransferCancel, btnTransferSubmit;

    private StudentViewModel studentViewModel;
    private String studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_transfer);

        tvTransferStudentName = findViewById(R.id.tvTransferStudentName);
        tvTransferProgramme = findViewById(R.id.tvTransferProgramme);
        tvTransferCurrentGroup = findViewById(R.id.tvTransferCurrentGroup);
        spinnerNewGroup = findViewById(R.id.spinnerNewGroup);
        etTransferReason = findViewById(R.id.etTransferReason);
        btnTransferCancel = findViewById(R.id.btnTransferCancel);
        btnTransferSubmit = findViewById(R.id.btnTransferSubmit);

        ArrayAdapter<String> groupAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{
                "G01", "G02", "G03", "G04", "Unassigned"
        });
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNewGroup.setAdapter(groupAdapter);

        studentViewModel = new ViewModelProvider(this).get(StudentViewModel.class);

        studentId = getIntent().getStringExtra("STUDENT_ID");
        if (studentId != null) {
            studentViewModel.getStudentById(studentId).observe(this, student -> {
                if (student != null) {
                    tvTransferStudentName.setText(student.getStudentNumber() + " - " + student.getStudentName());
                    tvTransferProgramme.setText(student.getProgramme());
                    tvTransferCurrentGroup.setText("Current Group: " + student.getLabGroup());
                }
            });
        }

        studentViewModel.getToastMessage().observe(this, msg -> {
            if (msg != null) {
                Toast.makeText(GroupTransferActivity.this, msg, Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        studentViewModel.getErrorMessage().observe(this, err -> {
            if (err != null) {
                Toast.makeText(GroupTransferActivity.this, err, Toast.LENGTH_LONG).show();
            }
        });

        btnTransferCancel.setOnClickListener(v -> finish());

        btnTransferSubmit.setOnClickListener(v -> {
            String newGroup = spinnerNewGroup.getSelectedItem().toString();
            if (studentId != null) {
                studentViewModel.requestGroupChange(studentId, newGroup);
            } else {
                Toast.makeText(this, "Transfer submitted to " + newGroup + "!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
