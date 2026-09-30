package com.example.labgroupmanager.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;

import com.example.labgroupmanager.R;
import com.example.labgroupmanager.data.local.SessionManager;
import com.example.labgroupmanager.data.model.Student;
import com.example.labgroupmanager.ui.viewmodel.StudentViewModel;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private TextInputEditText etSettingsName;
    private CheckBox cbCourseICT361, cbCourseBMG, cbCourseCyber;
    private Button btnSaveProfileSettings, btnSettingsLogout;
    private Spinner spinnerLanguage;
    private SwitchMaterial switchDarkMode, switchLargeFont;
    private TextView tvHelpCenterLink;

    private StudentViewModel studentViewModel;
    private SessionManager sessionManager;
    private Student currentStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sessionManager = new SessionManager(this);

        etSettingsName = findViewById(R.id.etSettingsName);
        cbCourseICT361 = findViewById(R.id.cbCourseICT361);
        cbCourseBMG = findViewById(R.id.cbCourseBMG);
        cbCourseCyber = findViewById(R.id.cbCourseCyber);
        btnSaveProfileSettings = findViewById(R.id.btnSaveProfileSettings);

        spinnerLanguage = findViewById(R.id.spinnerLanguage);
        switchDarkMode = findViewById(R.id.switchDarkMode);
        switchLargeFont = findViewById(R.id.switchLargeFont);
        tvHelpCenterLink = findViewById(R.id.tvHelpCenterLink);

        btnSettingsLogout = findViewById(R.id.btnSettingsLogout);

        ArrayAdapter<String> langAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"English (Default)", "Bemba", "Nyanja"});
        langAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(langAdapter);

        studentViewModel = new ViewModelProvider(this).get(StudentViewModel.class);

        studentViewModel.getOwnStudentProfile().observe(this, student -> {
            if (student != null) {
                currentStudent = student;
                if (etSettingsName.getText() == null || etSettingsName.getText().toString().isEmpty()) {
                    etSettingsName.setText(student.getStudentName());
                }

                String courses = student.getCourses();
                cbCourseICT361.setChecked(courses.contains("ICT361"));
                cbCourseBMG.setChecked(courses.contains("BMG"));
                cbCourseCyber.setChecked(courses.contains("Cyber Security"));
            }
        });

        btnSaveProfileSettings.setOnClickListener(v -> {
            if (currentStudent != null) {
                String newName = etSettingsName.getText() != null ? etSettingsName.getText().toString().trim() : "";
                List<String> selectedCourses = new ArrayList<>();
                if (cbCourseICT361.isChecked()) selectedCourses.add("ICT361");
                if (cbCourseBMG.isChecked()) selectedCourses.add("BMG");
                if (cbCourseCyber.isChecked()) selectedCourses.add("Cyber Security");

                currentStudent.setStudentName(newName);
                currentStudent.setCourses(String.join(", ", selectedCourses));

                studentViewModel.saveStudentProfile(currentStudent);
                Toast.makeText(this, "Profile options and courses updated!", Toast.LENGTH_SHORT).show();
            }
        });

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        switchLargeFont.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(this, "Accessibility: High contrast mode updated.", Toast.LENGTH_SHORT).show();
        });

        tvHelpCenterLink.setOnClickListener(v -> {
            Toast.makeText(this, "Help Center: Contact support at support@mu.ac.zm", Toast.LENGTH_LONG).show();
        });

        // Logout at the VERY LAST item on the list
        btnSettingsLogout.setOnClickListener(v -> {
            sessionManager.clearSession();
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
