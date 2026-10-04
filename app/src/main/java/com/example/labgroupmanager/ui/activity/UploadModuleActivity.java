package com.example.labgroupmanager.ui.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.labgroupmanager.R;
import com.example.labgroupmanager.data.local.AppDatabase;
import com.example.labgroupmanager.data.model.ModuleItem;
import com.google.android.material.textfield.TextInputEditText;

import java.util.UUID;
import java.util.concurrent.Executors;

public class UploadModuleActivity extends AppCompatActivity {

    private Spinner spinnerUploadCourseCode;
    private TextInputEditText etUploadModuleTitle, etUploadModuleDesc, etUploadFileUrl;
    private Button btnSubmitUploadModule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_module);

        spinnerUploadCourseCode = findViewById(R.id.spinnerUploadCourseCode);
        etUploadModuleTitle = findViewById(R.id.etUploadModuleTitle);
        etUploadModuleDesc = findViewById(R.id.etUploadModuleDesc);
        etUploadFileUrl = findViewById(R.id.etUploadFileUrl);
        btnSubmitUploadModule = findViewById(R.id.btnSubmitUploadModule);

        ArrayAdapter<String> courseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{
                "ICT361", "CS311", "CS321", "CS331", "IT311", "IT321", "DS311", "DS321", "BMG"
        });
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUploadCourseCode.setAdapter(courseAdapter);

        btnSubmitUploadModule.setOnClickListener(v -> {
            String code = spinnerUploadCourseCode.getSelectedItem().toString();
            String title = etUploadModuleTitle.getText() != null ? etUploadModuleTitle.getText().toString().trim() : "";
            String desc = etUploadModuleDesc.getText() != null ? etUploadModuleDesc.getText().toString().trim() : "";
            String url = etUploadFileUrl.getText() != null ? etUploadFileUrl.getText().toString().trim() : "";

            if (title.isEmpty()) {
                Toast.makeText(this, "Please enter module title.", Toast.LENGTH_SHORT).show();
                return;
            }

            ModuleItem item = new ModuleItem(
                    UUID.randomUUID().toString(),
                    code,
                    title,
                    desc.isEmpty() ? "Course module notes and lecture materials." : desc,
                    url.isEmpty() ? "http://mu.ac.zm/downloads/module_notes.pdf" : url,
                    System.currentTimeMillis()
            );

            Executors.newSingleThreadExecutor().execute(() -> {
                AppDatabase.getInstance(getApplicationContext()).moduleDao().insertModule(item);
                runOnUiThread(() -> {
                    Toast.makeText(UploadModuleActivity.this, "Module Published Successfully!", Toast.LENGTH_LONG).show();
                    finish();
                });
            });
        });
    }
}
