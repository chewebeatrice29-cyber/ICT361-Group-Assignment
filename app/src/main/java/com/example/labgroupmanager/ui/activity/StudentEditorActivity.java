package com.example.labgroupmanager.ui.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.labgroupmanager.R;
import com.example.labgroupmanager.data.model.Student;
import com.example.labgroupmanager.ui.viewmodel.StudentViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class StudentEditorActivity extends AppCompatActivity {

    private TextView tvEditorTitle;
    private TextInputEditText etEditorStudentNumber, etEditorStudentName;
    private Spinner spinnerEditorProgramme, spinnerEditorGroup;
    private Button btnEditorSave, btnEditorDelete;

    private StudentViewModel studentViewModel;
    private String studentId;
    private Student editingStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_editor);

        tvEditorTitle = findViewById(R.id.tvEditorTitle);
        etEditorStudentNumber = findViewById(R.id.etEditorStudentNumber);
        etEditorStudentName = findViewById(R.id.etEditorStudentName);
        spinnerEditorProgramme = findViewById(R.id.spinnerEditorProgramme);
        spinnerEditorGroup = findViewById(R.id.spinnerEditorGroup);
        btnEditorSave = findViewById(R.id.btnEditorSave);
        btnEditorDelete = findViewById(R.id.btnEditorDelete);

        ArrayAdapter<String> progAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"CS", "IT", "DS"});
        progAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEditorProgramme.setAdapter(progAdapter);

        ArrayAdapter<String> groupAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"G01", "G02", "G03", "G04", "Unassigned"});
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEditorGroup.setAdapter(groupAdapter);

        studentViewModel = new ViewModelProvider(this).get(StudentViewModel.class);

        studentId = getIntent().getStringExtra("STUDENT_ID");

        if (studentId != null) {
            tvEditorTitle.setText(R.string.title_edit_student);
            btnEditorDelete.setVisibility(View.VISIBLE);

            studentViewModel.getStudentById(studentId).observe(this, student -> {
                if (student != null) {
                    editingStudent = student;
                    etEditorStudentNumber.setText(student.getStudentNumber());
                    etEditorStudentName.setText(student.getStudentName());
                    selectSpinnerValue(spinnerEditorProgramme, student.getProgramme());
                    selectSpinnerValue(spinnerEditorGroup, student.getLabGroup());
                }
            });
        }

        studentViewModel.getToastMessage().observe(this, msg -> {
            if (msg != null) {
                Toast.makeText(StudentEditorActivity.this, msg, Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        studentViewModel.getErrorMessage().observe(this, err -> {
            if (err != null) {
                Toast.makeText(StudentEditorActivity.this, err, Toast.LENGTH_LONG).show();
            }
        });

        btnEditorSave.setOnClickListener(v -> saveStudent());

        btnEditorDelete.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    private void saveStudent() {
        String number = etEditorStudentNumber.getText() != null ? etEditorStudentNumber.getText().toString().trim() : "";
        String name = etEditorStudentName.getText() != null ? etEditorStudentName.getText().toString().trim() : "";
        String programme = spinnerEditorProgramme.getSelectedItem().toString();
        String group = spinnerEditorGroup.getSelectedItem().toString();

        if (!number.matches("^\\d{9}$")) {
            Toast.makeText(this, "Student number must be exactly 9 digits.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.length() < 2 || name.length() > 100) {
            Toast.makeText(this, "Student name must be between 2 and 100 characters.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (editingStudent == null) {
            // Create New
            studentViewModel.createStudentByLecturer(number, name, programme, group);
        } else {
            // Update Existing
            editingStudent.setStudentNumber(number);
            editingStudent.setStudentName(name);
            editingStudent.setProgramme(programme);
            editingStudent.setLabGroup(group);
            studentViewModel.saveStudentProfile(editingStudent);
        }
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete this student record? This will soft-delete the student and release their lab group seat while preserving the student number.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (studentId != null) {
                        studentViewModel.deleteStudent(studentId);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void selectSpinnerValue(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }
}
