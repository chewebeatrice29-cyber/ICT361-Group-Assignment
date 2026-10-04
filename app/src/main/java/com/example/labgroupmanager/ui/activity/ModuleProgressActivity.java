package com.example.labgroupmanager.ui.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.labgroupmanager.R;
import com.google.android.material.textfield.TextInputEditText;

public class ModuleProgressActivity extends AppCompatActivity {

    private Spinner spinnerModuleCourse;
    private TextInputEditText etModuleTitle, etModulePercent;
    private Button btnUpdateModuleSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module_progress);

        spinnerModuleCourse = findViewById(R.id.spinnerModuleCourse);
        etModuleTitle = findViewById(R.id.etModuleTitle);
        etModulePercent = findViewById(R.id.etModulePercent);
        btnUpdateModuleSubmit = findViewById(R.id.btnUpdateModuleSubmit);

        ArrayAdapter<String> courseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{
                "ICT361 Mobile App Development", "BMG Principles of Management", "Cyber Security Principles"
        });
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerModuleCourse.setAdapter(courseAdapter);

        btnUpdateModuleSubmit.setOnClickListener(v -> {
            String course = spinnerModuleCourse.getSelectedItem().toString();
            String title = etModuleTitle.getText() != null ? etModuleTitle.getText().toString().trim() : "";
            String percent = etModulePercent.getText() != null ? etModulePercent.getText().toString().trim() : "";

            if (title.isEmpty() || percent.isEmpty()) {
                Toast.makeText(this, "Please enter both module title and progress percentage.", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Module Progress Updated: " + title + " (" + percent + "%) for " + course, Toast.LENGTH_LONG).show();
            finish();
        });
    }
}
