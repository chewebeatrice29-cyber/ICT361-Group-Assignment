package com.example.labgroupmanager.ui.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.labgroupmanager.R;
import com.example.labgroupmanager.worker.NotificationWorker;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.concurrent.TimeUnit;

public class ReminderSettingsActivity extends AppCompatActivity {

    private SwitchMaterial switchEnablePopups;
    private Spinner spinnerAdvanceTime, spinnerFrequency;
    private Button btnSaveReminderSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_settings);

        switchEnablePopups = findViewById(R.id.switchEnablePopups);
        spinnerAdvanceTime = findViewById(R.id.spinnerAdvanceTime);
        spinnerFrequency = findViewById(R.id.spinnerFrequency);
        btnSaveReminderSettings = findViewById(R.id.btnSaveReminderSettings);

        ArrayAdapter<String> advanceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{
                "15 Minutes Before", "1 Hour Before", "1 Day Before (Default)", "2 Days Before"
        });
        advanceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAdvanceTime.setAdapter(advanceAdapter);

        ArrayAdapter<String> freqAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{
                "Every 15 Minutes", "Every 1 Hour", "Every 6 Hours", "Once Daily (Default)"
        });
        freqAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrequency.setAdapter(freqAdapter);

        btnSaveReminderSettings.setOnClickListener(v -> {
            boolean enabled = switchEnablePopups.isChecked();
            String advance = spinnerAdvanceTime.getSelectedItem().toString();
            String freq = spinnerFrequency.getSelectedItem().toString();

            if (!enabled) {
                WorkManager.getInstance(getApplicationContext()).cancelUniqueWork("POPUP_REMINDERS_WORK");
                Toast.makeText(this, "Pop-up reminders disabled.", Toast.LENGTH_SHORT).show();
            } else {
                long intervalMinutes = 15;
                if (freq.contains("1 Hour")) intervalMinutes = 60;
                else if (freq.contains("6 Hours")) intervalMinutes = 360;
                else if (freq.contains("Daily")) intervalMinutes = 1440;

                PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                        NotificationWorker.class,
                        intervalMinutes,
                        TimeUnit.MINUTES
                ).build();

                WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork(
                        "POPUP_REMINDERS_WORK",
                        ExistingPeriodicWorkPolicy.UPDATE,
                        request
                );

                Toast.makeText(this, "Pop-up settings saved! Warnings set for " + advance + " (" + freq + ").", Toast.LENGTH_LONG).show();
            }

            finish();
        });
    }
}
