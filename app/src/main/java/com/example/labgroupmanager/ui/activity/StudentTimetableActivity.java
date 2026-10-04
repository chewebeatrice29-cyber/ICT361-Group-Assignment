package com.example.labgroupmanager.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labgroupmanager.R;
import com.example.labgroupmanager.data.local.AppDatabase;
import com.example.labgroupmanager.data.model.TimetableSlot;
import com.example.labgroupmanager.ui.BottomNavHelper;
import com.example.labgroupmanager.ui.adapter.TimetableAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

public class StudentTimetableActivity extends AppCompatActivity {

    private Spinner spinnerEduroleProgram, spinnerTtDayFilter;
    private Button btnShowEduroleTimetable;
    private RecyclerView rvStudentTtSlots;
    private TimetableAdapter timetableAdapter;
    private final List<TimetableSlot> slotList = new ArrayList<>();
    private final List<TimetableSlot> allSlots = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_timetable);

        spinnerEduroleProgram = findViewById(R.id.spinnerEduroleProgram);
        spinnerTtDayFilter = findViewById(R.id.spinnerTtDayFilter);
        btnShowEduroleTimetable = findViewById(R.id.btnShowEduroleTimetable);
        rvStudentTtSlots = findViewById(R.id.rvStudentTtSlots);

        rvStudentTtSlots.setLayoutManager(new LinearLayoutManager(this));
        timetableAdapter = new TimetableAdapter(slotList);
        rvStudentTtSlots.setAdapter(timetableAdapter);

        ArrayAdapter<String> progAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{
                "Bachelor of Science in Computer Science (BScCS1)",
                "Bachelor of Science in Information Technology (BScIT1)",
                "Bachelor of Science in Data Science (BScDS1)"
        });
        progAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEduroleProgram.setAdapter(progAdapter);

        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{
                "All Days", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"
        });
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTtDayFilter.setAdapter(dayAdapter);

        spinnerTtDayFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterSlotsByDay(dayAdapter.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnShowEduroleTimetable.setOnClickListener(v -> {
            String prog = spinnerEduroleProgram.getSelectedItem().toString();
            Toast.makeText(this, "Refreshed timetable for " + prog, Toast.LENGTH_SHORT).show();
            loadTimetableData();
        });

        BottomNavHelper.setupBottomNav(this, 0);
        loadTimetableData();
    }

    private void loadTimetableData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            List<TimetableSlot> dbSlots = db.timetableDao().getAllSlots();

            if (dbSlots.isEmpty()) {
                db.timetableDao().insertSlot(new TimetableSlot("MON_1", "Monday", 1, "11:00 - 13:00", "ICT101", "Halubanza B. (Dr.)", "G 40", "Computer Lab 2"));
                db.timetableDao().insertSlot(new TimetableSlot("MON_2", "Monday", 2, "13:00 - 15:00", "ICT111", "Nyirenda E. (Mr.)", "NEW DINNING HALL", "Lecture Hall 4"));
                db.timetableDao().insertSlot(new TimetableSlot("MON_3", "Monday", 3, "17:00 - 19:00", "PHY101", "Chilukusha D.C. (Mr.)", "New Lecture Theatre", "Science Block"));
                db.timetableDao().insertSlot(new TimetableSlot("WED_1", "Wednesday", 1, "11:00 - 13:00", "ICT111", "Nyirenda E. (Mr.)", "NEW DINNING HALL", "Lecture Hall 4"));
                db.timetableDao().insertSlot(new TimetableSlot("WED_2", "Wednesday", 2, "13:00 - 15:00", "ICT131", "Kaluba Z. (Mr.)", "G 46", "Computer Lab 1"));
                db.timetableDao().insertSlot(new TimetableSlot("WED_3", "Wednesday", 3, "15:00 - 17:00", "MSM111", "Chimbola O. (Dr.)", "Chalabesa hall", "Maths Block"));
                db.timetableDao().insertSlot(new TimetableSlot("THU_1", "Thursday", 1, "09:00 - 11:00", "PHY101", "Chilukusha D.C. (Mr.)", "MULTI PURPOSE HALL", "Science Block"));
                db.timetableDao().insertSlot(new TimetableSlot("THU_2", "Thursday", 2, "13:00 - 15:00", "MSM111", "Chimbola O. (Dr.)", "Chalabesa hall", "Maths Block"));
                db.timetableDao().insertSlot(new TimetableSlot("FRI_1", "Friday", 1, "09:00 - 11:00", "ICT131", "Kaluba Z. (Mr.)", "NEW DINNING HALL", "Computer Lab 1"));
                db.timetableDao().insertSlot(new TimetableSlot("FRI_2", "Friday", 2, "13:00 - 15:00", "ICT101", "Halubanza B. (Dr.)", "Old Dinning Hall B", "Computer Lab 2"));
                dbSlots = db.timetableDao().getAllSlots();
            }

            final List<TimetableSlot> slots = dbSlots;
            runOnUiThread(() -> {
                allSlots.clear();
                allSlots.addAll(slots);
                String selectedDay = spinnerTtDayFilter != null && spinnerTtDayFilter.getSelectedItem() != null ?
                        spinnerTtDayFilter.getSelectedItem().toString() : "All Days";
                filterSlotsByDay(selectedDay);
            });
        });
    }

    private void filterSlotsByDay(String day) {
        slotList.clear();
        if ("All Days".equalsIgnoreCase(day)) {
            slotList.addAll(allSlots);
        } else {
            for (TimetableSlot slot : allSlots) {
                if (slot.getDay().equalsIgnoreCase(day)) {
                    slotList.add(slot);
                }
            }
        }
        timetableAdapter.notifyDataSetChanged();
    }
}
