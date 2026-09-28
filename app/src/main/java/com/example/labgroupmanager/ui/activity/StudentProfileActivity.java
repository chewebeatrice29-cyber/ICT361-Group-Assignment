package com.example.labgroupmanager.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.labgroupmanager.R;
import com.example.labgroupmanager.data.local.SessionManager;
import com.example.labgroupmanager.data.model.Student;
import com.example.labgroupmanager.ui.viewmodel.StudentViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class StudentProfileActivity extends AppCompatActivity {

    private TextView tvStudentGreeting, tvStudentNumberDisplay, tvProgrammeDisplay, tvGroupDisplay, tvSyncStatus, tvOfflineBanner;
    private TextInputEditText etEditName;
    private Spinner spinnerEditProgramme, spinnerTargetGroup;
    private Button btnSaveProfile, btnSubmitGroupRequest;

    private StudentViewModel studentViewModel;
    private SessionManager sessionManager;
    private Student currentStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        sessionManager = new SessionManager(this);

        tvStudentGreeting = findViewById(R.id.tvStudentGreeting);
        tvStudentNumberDisplay = findViewById(R.id.tvStudentNumberDisplay);
        tvProgrammeDisplay = findViewById(R.id.tvProgrammeDisplay);
        tvGroupDisplay = findViewById(R.id.tvGroupDisplay);
        tvSyncStatus = findViewById(R.id.tvSyncStatus);
        tvOfflineBanner = findViewById(R.id.tvOfflineBanner);

        etEditName = findViewById(R.id.etEditName);
        spinnerEditProgramme = findViewById(R.id.spinnerEditProgramme);
        spinnerTargetGroup = findViewById(R.id.spinnerTargetGroup);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnSubmitGroupRequest = findViewById(R.id.btnSubmitGroupRequest);

        ArrayAdapter<String> progAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"CS", "IT", "DS"});
        progAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEditProgramme.setAdapter(progAdapter);

        ArrayAdapter<String> groupAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"G01", "G02", "G03", "G04", "Unassigned"});
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTargetGroup.setAdapter(groupAdapter);

        studentViewModel = new ViewModelProvider(this).get(StudentViewModel.class);

        studentViewModel.getOwnStudentProfile().observe(this, student -> {
            if (student != null) {
                currentStudent = student;
                tvStudentGreeting.setText("Hello, " + student.getStudentName());
                tvStudentNumberDisplay.setText("Student #: " + student.getStudentNumber());
                tvProgrammeDisplay.setText("Programme: " + student.getProgramme());
                tvGroupDisplay.setText("My Lab Group: " + student.getLabGroup());
                tvSyncStatus.setText("Sync Status: " + student.getSyncStatus());

                if ("SAVED_LOCALLY".equalsIgnoreCase(student.getSyncStatus()) || "PENDING".equalsIgnoreCase(student.getSyncStatus())) {
                    tvOfflineBanner.setVisibility(View.VISIBLE);
                } else {
                    tvOfflineBanner.setVisibility(View.GONE);
                }

                if (etEditName.getText() == null || etEditName.getText().toString().isEmpty()) {
                    etEditName.setText(student.getStudentName());
                }
            }
        });

        studentViewModel.getToastMessage().observe(this, msg -> {
            if (msg != null) Toast.makeText(StudentProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
        });

        studentViewModel.getErrorMessage().observe(this, err -> {
            if (err != null) Toast.makeText(StudentProfileActivity.this, err, Toast.LENGTH_LONG).show();
        });

        btnSaveProfile.setOnClickListener(v -> {
            if (currentStudent != null) {
                String newName = etEditName.getText() != null ? etEditName.getText().toString().trim() : "";
                String newProg = spinnerEditProgramme.getSelectedItem().toString();
                currentStudent.setStudentName(newName);
                currentStudent.setProgramme(newProg);
                studentViewModel.saveStudentProfile(currentStudent);
            }
        });

        btnSubmitGroupRequest.setOnClickListener(v -> {
            if (currentStudent != null) {
                String target = spinnerTargetGroup.getSelectedItem().toString();
                studentViewModel.requestGroupChange(currentStudent.getStudentId(), target);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            sessionManager.clearSession();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
