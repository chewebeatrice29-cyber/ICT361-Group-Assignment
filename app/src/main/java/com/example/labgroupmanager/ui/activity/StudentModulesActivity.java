package com.example.labgroupmanager.ui.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labgroupmanager.R;
import com.example.labgroupmanager.data.local.AppDatabase;
import com.example.labgroupmanager.data.model.ModuleItem;
import com.example.labgroupmanager.ui.BottomNavHelper;
import com.example.labgroupmanager.ui.adapter.ModuleAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

public class StudentModulesActivity extends AppCompatActivity {

    private RecyclerView rvStudentModules;
    private ModuleAdapter moduleAdapter;
    private final List<ModuleItem> moduleList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_modules);

        rvStudentModules = findViewById(R.id.rvStudentModules);
        rvStudentModules.setLayoutManager(new LinearLayoutManager(this));
        moduleAdapter = new ModuleAdapter(moduleList);
        rvStudentModules.setAdapter(moduleAdapter);

        BottomNavHelper.setupBottomNav(this, 0);
        loadModules();
    }

    private void loadModules() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            List<ModuleItem> dbItems = db.moduleDao().getAllModules();

            if (dbItems.isEmpty()) {
                db.moduleDao().insertModule(new ModuleItem(
                        UUID.randomUUID().toString(), "ICT361", "Module 1: Android UI & XML Views",
                        "Comprehensive guide on ViewGroups, Material Components, and layout attributes.",
                        "http://www.mu.ac.zm/downloads/ict361_module1.pdf", System.currentTimeMillis()
                ));
                db.moduleDao().insertModule(new ModuleItem(
                        UUID.randomUUID().toString(), "ICT361", "Module 2: Room DB & Local Persistence",
                        "Covers Entities, DAOs, LiveData, and Room Database migrations.",
                        "http://www.mu.ac.zm/downloads/ict361_module2.pdf", System.currentTimeMillis() - 3600000
                ));
                db.moduleDao().insertModule(new ModuleItem(
                        UUID.randomUUID().toString(), "ICT361", "Module 3: Retrofit REST API & WorkManager",
                        "Covers HTTP client calls, background sync workers, and offline queuing.",
                        "http://www.mu.ac.zm/downloads/ict361_module3.pdf", System.currentTimeMillis() - 7200000
                ));
                dbItems = db.moduleDao().getAllModules();
            }

            final List<ModuleItem> items = dbItems;
            runOnUiThread(() -> {
                moduleList.clear();
                moduleList.addAll(items);
                moduleAdapter.notifyDataSetChanged();
            });
        });
    }
}
