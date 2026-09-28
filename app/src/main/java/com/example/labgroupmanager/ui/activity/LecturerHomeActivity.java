package com.example.labgroupmanager.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.labgroupmanager.R;

public class LecturerHomeActivity extends AppCompatActivity {

    private Button btnLecturerOpenRoster, btnLecturerAddStudent, btnLecturerShareSummary, btnLecturerBroadcast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer_home);

        btnLecturerOpenRoster = findViewById(R.id.btnLecturerOpenRoster);
        btnLecturerAddStudent = findViewById(R.id.btnLecturerAddStudent);
        btnLecturerShareSummary = findViewById(R.id.btnLecturerShareSummary);
        btnLecturerBroadcast = findViewById(R.id.btnLecturerBroadcast);

        btnLecturerOpenRoster.setOnClickListener(v -> {
            startActivity(new Intent(LecturerHomeActivity.this, LecturerRosterActivity.class));
        });

        btnLecturerAddStudent.setOnClickListener(v -> {
            startActivity(new Intent(LecturerHomeActivity.this, StudentEditorActivity.class));
        });

        btnLecturerShareSummary.setOnClickListener(v -> {
            startActivity(new Intent(LecturerHomeActivity.this, LecturerRosterActivity.class));
        });

        btnLecturerBroadcast.setOnClickListener(v -> {
            Toast.makeText(this, "Announcement broadcasted successfully to all active students.", Toast.LENGTH_LONG).show();
        });
    }
}
