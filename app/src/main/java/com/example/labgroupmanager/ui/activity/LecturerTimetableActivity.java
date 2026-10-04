package com.example.labgroupmanager.ui.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.labgroupmanager.R;
import com.example.labgroupmanager.data.local.AppDatabase;
import com.example.labgroupmanager.data.local.SessionManager;
import com.example.labgroupmanager.data.model.TimetableSlot;
import com.google.android.material.textfield.TextInputEditText;

import java.util.concurrent.Executors;

public class LecturerTimetableActivity extends AppCompatActivity {

    private Spinner spinnerTtDay, spinnerTtSlot;
    private TextInputEditText etTtCourseCode, etTtCourseName, etTtVenue;
    private Button btnSaveTtSlot;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer_timetable);

        sessionManager = new SessionManager(this);

        spinnerTtDay = findViewById(R.id.spinnerTtDay);
        spinnerTtSlot = findViewById(R.id.spinnerTtSlot);
        etTtCourseCode = findViewById(R.id.etTtCourseCode);
        etTtCourseName = findViewById(R.id.etTtCourseName);
        etTtVenue = findViewById(R.id.etTtVenue);
        btnSaveTtSlot = findViewById(R.id.btnSaveTtSlot);

        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{
                "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"
        });
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTtDay.setAdapter(dayAdapter);

        ArrayAdapter<String> slotAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{
                "Slot 1: 07:00 AM - 09:00 AM",
                "Slot 2: 09:15 AM - 11:15 AM",
                "Slot 3: 11:30 AM - 01:30 PM",
                "Slot 4: 02:00 PM - 04:00 PM",
                "Slot 5: 04:15 PM - 06:15 PM"
        });
        slotAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTtSlot.setAdapter(slotAdapter);

        btnSaveTtSlot.setOnClickListener(v -> {
            String day = spinnerTtDay.getSelectedItem().toString();
            int slotIdx = spinnerTtSlot.getSelectedItemPosition() + 1;
            String slotText = spinnerTtSlot.getSelectedItem().toString();
            String code = etTtCourseCode.getText() != null ? etTtCourseCode.getText().toString().trim() : "";
            String name = etTtCourseName.getText() != null ? etTtCourseName.getText().toString().trim() : "";
            String venue = etTtVenue.getText() != null ? etTtVenue.getText().toString().trim() : "";

            if (code.isEmpty() || name.isEmpty()) {
                Toast.makeText(this, "Please enter course code and name.", Toast.LENGTH_SHORT).show();
                return;
            }

            String slotId = day.substring(0, 3).toUpperCase() + "_SLOT" + slotIdx;
            String lecturerName = sessionManager.getUsername() != null ? sessionManager.getUsername() : "Dr. M. Banda";

            TimetableSlot slot = new TimetableSlot(
                    slotId,
                    day,
                    slotIdx,
                    slotText,
                    code,
                    name,
                    venue.isEmpty() ? "Computer Lab 2" : venue,
                    lecturerName
            );

            Executors.newSingleThreadExecutor().execute(() -> {
                AppDatabase.getInstance(getApplicationContext()).timetableDao().insertSlot(slot);
                runOnUiThread(() -> {
                    Toast.makeText(LecturerTimetableActivity.this, "Timetable slot saved for " + day + " (" + slotText + ")!", Toast.LENGTH_LONG).show();
                    finish();
                });
            });
        });
    }
}
