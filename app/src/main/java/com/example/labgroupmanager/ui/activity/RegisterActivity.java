package com.example.labgroupmanager.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.labgroupmanager.R;
import com.example.labgroupmanager.ui.viewmodel.AuthViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etClaimCode, etStudentNumber, etStudentName, etRegUsername, etRegPassword;
    private Spinner spinnerProgramme;
    private Button btnSubmitRegister;

    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        etStudentNumber = findViewById(R.id.etStudentNumber);
        etStudentName = findViewById(R.id.etStudentName);
        etRegUsername = findViewById(R.id.etRegUsername);
        etRegPassword = findViewById(R.id.etRegPassword);
        spinnerProgramme = findViewById(R.id.spinnerProgramme);
        btnSubmitRegister = findViewById(R.id.btnSubmitRegister);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"CS", "IT", "DS"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProgramme.setAdapter(adapter);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        authViewModel.getAuthError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });

        authViewModel.getAuthSuccess().observe(this, response -> {
            if (response != null) {
                Toast.makeText(RegisterActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, StudentProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        btnSubmitRegister.setOnClickListener(v -> {
            String claim = etClaimCode.getText() != null ? etClaimCode.getText().toString() : "";
            String number = etStudentNumber.getText() != null ? etStudentNumber.getText().toString() : "";
            String name = etStudentName.getText() != null ? etStudentName.getText().toString() : "";
            String username = etRegUsername.getText() != null ? etRegUsername.getText().toString() : "";
            String password = etRegPassword.getText() != null ? etRegPassword.getText().toString() : "";
            String programme = spinnerProgramme.getSelectedItem().toString();

            authViewModel.registerStudent(claim, username, password, number, name, programme);
        });
    }
}
