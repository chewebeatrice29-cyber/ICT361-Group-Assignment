package com.example.labgroupmanager.ui.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labgroupmanager.R;
import com.example.labgroupmanager.data.local.AppDatabase;
import com.example.labgroupmanager.data.model.ScheduleItem;
import com.example.labgroupmanager.ui.adapter.ScheduleAdapter;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

public class ScheduleActivity extends AppCompatActivity {

    private RecyclerView rvScheduleItems;
    private Button btnAddScheduleActivity;
    private ScheduleAdapter scheduleAdapter;
    private final List<ScheduleItem> itemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        rvScheduleItems = findViewById(R.id.rvScheduleItems);
        btnAddScheduleActivity = findViewById(R.id.btnAddScheduleActivity);

        rvScheduleItems.setLayoutManager(new LinearLayoutManager(this));
        scheduleAdapter = new ScheduleAdapter(itemList, item -> deleteItem(item));
        rvScheduleItems.setAdapter(scheduleAdapter);

        btnAddScheduleActivity.setOnClickListener(v -> showAddActivityDialog());

        loadScheduleData();
    }

    private void loadScheduleData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            List<ScheduleItem> dbItems = db.scheduleDao().getAllScheduleItems();

            if (dbItems.isEmpty()) {
                // Seed default class timetable & initial activities
                db.scheduleDao().insertItem(new ScheduleItem(UUID.randomUUID().toString(), "ICT361 Mobile App Dev Lab", "Monday, 10:00 AM", "Computer Lab 2", true));
                db.scheduleDao().insertItem(new ScheduleItem(UUID.randomUUID().toString(), "BMG Principles of Management", "Thursday, 08:00 AM", "Lecture Hall 4", true));
                db.scheduleDao().insertItem(new ScheduleItem(UUID.randomUUID().toString(), "Cyber Security Quiz Prep", "Wednesday, 04:00 PM", "Library Study Room 1", false));
                dbItems = db.scheduleDao().getAllScheduleItems();
            }

            final List<ScheduleItem> items = dbItems;
            runOnUiThread(() -> {
                itemList.clear();
                itemList.addAll(items);
                scheduleAdapter.notifyDataSetChanged();
            });
        });
    }

    private void deleteItem(ScheduleItem item) {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase.getInstance(getApplicationContext()).scheduleDao().deleteItem(item.getId());
            runOnUiThread(() -> {
                Toast.makeText(ScheduleActivity.this, "Activity removed from schedule", Toast.LENGTH_SHORT).show();
                loadScheduleData();
            });
        });
    }

    private void showAddActivityDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Activity to Personal Schedule");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_schedule_activity, null);
        TextInputEditText etTitle = dialogView.findViewById(R.id.etSchedDialogTitle);
        TextInputEditText etTime = dialogView.findViewById(R.id.etSchedDialogTime);
        TextInputEditText etLocation = dialogView.findViewById(R.id.etSchedDialogLocation);

        builder.setView(dialogView);
        builder.setPositiveButton("Add to Schedule", (dialog, which) -> {
            String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
            String time = etTime.getText() != null ? etTime.getText().toString().trim() : "";
            String location = etLocation.getText() != null ? etLocation.getText().toString().trim() : "";

            if (title.isEmpty()) {
                Toast.makeText(this, "Please enter an activity title.", Toast.LENGTH_SHORT).show();
                return;
            }

            ScheduleItem newItem = new ScheduleItem(
                    UUID.randomUUID().toString(),
                    title,
                    time.isEmpty() ? "Flexible Time" : time,
                    location.isEmpty() ? "Personal Workspace" : location,
                    false
            );

            Executors.newSingleThreadExecutor().execute(() -> {
                AppDatabase.getInstance(getApplicationContext()).scheduleDao().insertItem(newItem);
                runOnUiThread(() -> {
                    Toast.makeText(ScheduleActivity.this, "Activity added to schedule!", Toast.LENGTH_SHORT).show();
                    loadScheduleData();
                });
            });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
