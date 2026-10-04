package com.example.labgroupmanager.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labgroupmanager.R;
import com.example.labgroupmanager.data.local.AppDatabase;
import com.example.labgroupmanager.data.model.TimetableSlot;
import com.example.labgroupmanager.ui.adapter.TimetableAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class StudentTimetableActivity extends AppCompatActivity {

    private Spinner spinnerTtStudentDay;
    private RecyclerView rvStudentTtSlots;
    private TimetableAdapter timetableAdapter;
    private final List<TimetableSlot> slotList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_timetable);

        spinnerTtStudentDay = findViewById(R.id.spinnerTtStudentDay);
        rvStudentTtSlots = findViewById(R.id.rvStudentTtSlots);

        rvStudentTtSlots.setLayoutManager(new LinearLayoutManager(this));
        timetableAdapter = new TimetableAdapter(slotList);
        rvStudentTtSlots.setAdapter(timetableAdapter);

        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{
                "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"
        });
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTtStudentDay.setAdapter(dayAdapter);

        spinnerTtStudentDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String day = spinnerTtStudentDay.getSelectedItem().toString();
                loadTimetableForDay(day);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        loadTimetableForDay("Monday");
    }

    private void loadTimetableForDay(String day) {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            List<TimetableSlot> dbSlots = db.timetableDao().getAllSlots();

            if (dbSlots.isEmpty()) {
                // Seed 5 slots for Monday
                db.timetableDao().insertSlot(new TimetableSlot("MON_1", "Monday", 1, "07:00 AM - 09:00 AM", "ICT361", "Mobile App Development Lab 1", "Computer Lab 2", "Dr. M. Banda"));
                db.timetableDao().insertSlot(new TimetableSlot("MON_2", "Monday", 2, "09:15 AM - 11:15 AM", "CS311", "Data Structures & Algorithms", "Lecture Hall 4", "Prof. C. Phiri"));
                db.timetableDao().insertSlot(new TimetableSlot("MON_3", "Monday", 3, "11:30 AM - 01:30 PM", "CS321", "Software Engineering", "Room 102", "Mr. J. Tembo"));
                db.timetableDao().insertSlot(new TimetableSlot("MON_4", "Monday", 4, "02:00 PM - 04:00 PM", "IT311", "Web Technologies & E-Commerce", "Lab 1", "Dr. M. Banda"));
                db.timetableDao().insertSlot(new TimetableSlot("MON_5", "Monday", 5, "04:15 PM - 06:15 PM", "BMG", "Principles of Management", "Main Hall", "Dr. S. Kasonde"));
                dbSlots = db.timetableDao().getAllSlots();
            }

            List<TimetableSlot> filtered = new ArrayList<>();
            for (TimetableSlot slot : dbSlots) {
                if (slot.getDay().equalsIgnoreCase(day)) {
                    filtered.add(slot);
                }
            }

            runOnUiThread(() -> {
                slotList.clear();
                slotList.addAll(filtered);
                timetableAdapter.notifyDataSetChanged();
            });
        });
    }
}
