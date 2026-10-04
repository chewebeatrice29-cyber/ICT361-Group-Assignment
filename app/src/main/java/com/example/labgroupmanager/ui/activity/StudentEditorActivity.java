package com.example.labgroupmanager.ui.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.labgroupmanager.R;
import com.example.labgroupmanager.data.model.Student;
import com.example.labgroupmanager.ui.viewmodel.StudentViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class StudentEditorActivity extends AppCompatActivity {

    private TextView tvEditorTitle;
    private TextInputEditText etEditorStudentNumber, etEditorStudentName;
    private Spinner spinnerEditorProgramme, spinnerEditorGroup;
    private CheckBox cbCourseCS, cbCourseSE, cbCourseIS, cbCourseDB, cbCourseWT, cbCourseMC;
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

        cbCourseCS = findViewById(R.id.cbCourseCS);
        cbCourseSE = findViewById(R.id.cbCourseSE);
        cbCourseIS = findViewById(R.id.cbCourseIS);
        cbCourseDB = findViewById(R.id.cbCourseDB);
        cbCourseWT = findViewById(R.id.cbCourseWT);
        cbCourseMC = findViewById(R.id.cbCourseMC);

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

                    String courses = student.getCourses();
                    cbCourseCS.setChecked(courses.contains("CS") || courses.contains("Computer Science"));
                    cbCourseSE.setChecked(courses.contains("SE") || courses.contains("Software"));
                    cbCourseIS.setChecked(courses.contains("IS") || courses.contains("Information"));
                    cbCourseDB.setChecked(courses.contains("DB") || courses.contains("Database"));
                    cbCourseWT.setChecked(courses.contains("WT") || courses.contains("Web"));
                    cbCourseMC.setChecked(courses.contains("MC") || courses.contains("Mobile"));
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

        List<String> selectedCourses = new ArrayList<>();
        if (cbCourseCS.isChecked()) selectedCourses.add("CS");
        if (cbCourseSE.isChecked()) selectedCourses.add("SE");
        if (cbCourseIS.isChecked()) selectedCourses.add("IS");
        if (cbCourseDB.isChecked()) selectedCourses.add("DB");
        if (cbCourseWT.isChecked()) selectedCourses.add("WT");
        if (cbCourseMC.isChecked()) selectedCourses.add("MC");
        String courseString = String.join(", ", selectedCourses);

        if (editingStudent == null) {
            // Create New
            studentViewModel.createStudentByLecturer(number, name, programme, group);
        } else {
            // Update Existing
            editingStudent.setStudentNumber(number);
            editingStudent.setStudentName(name);
            editingStudent.setProgramme(programme);
            editingStudent.setLabGroup(group);
            editingStudent.setCourses(courseString);
            studentViewModel.saveStudentProfile(editingStudent);
        }
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Student")
                .setMessage("Are you sure you want to delete " + (editingStudent != null ? editingStudent.getStudentName() : "this student") + "? This action cannot be undone. The record will be hidden (soft delete).")
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
