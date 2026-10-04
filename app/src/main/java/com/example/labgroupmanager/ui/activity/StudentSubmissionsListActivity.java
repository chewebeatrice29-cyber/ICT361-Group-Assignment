package com.example.labgroupmanager.ui.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labgroupmanager.R;
import com.example.labgroupmanager.data.local.AppDatabase;
import com.example.labgroupmanager.data.local.SessionManager;
import com.example.labgroupmanager.data.model.SubmissionItem;
import com.example.labgroupmanager.ui.BottomNavHelper;
import com.example.labgroupmanager.ui.adapter.SubmissionAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

public class StudentSubmissionsListActivity extends AppCompatActivity {

    private RecyclerView rvStudentSubmissions;
    private SubmissionAdapter submissionAdapter;
    private final List<SubmissionItem> submissionList = new ArrayList<>();
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_submissions_list);

        sessionManager = new SessionManager(this);

        rvStudentSubmissions = findViewById(R.id.rvStudentSubmissions);
        rvStudentSubmissions.setLayoutManager(new LinearLayoutManager(this));
        submissionAdapter = new SubmissionAdapter(submissionList);
        rvStudentSubmissions.setAdapter(submissionAdapter);

        BottomNavHelper.setupBottomNav(this, 0);
        loadSubmissions();
    }

    private void loadSubmissions() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            String studentId = sessionManager.getStudentId() != null ? sessionManager.getStudentId() : "s-202109428";
            List<SubmissionItem> dbItems = db.submissionDao().getAllSubmissions();

            if (dbItems.isEmpty()) {
                db.submissionDao().insertSubmission(new SubmissionItem(
                        UUID.randomUUID().toString(),
                        "ICT361 Lab Practical 3",
                        studentId,
                        "202109428",
                        "James Banda",
                        "Completed Room DB entities, DAOs, and background sync worker.",
                        "https://github.com/jamesbanda/ICT361_Lab3.git",
                        System.currentTimeMillis(),
                        "GRADED",
                        92,
                        "Excellent implementation of DAOs and asynchronous sync workers!"
                ));
                db.submissionDao().insertSubmission(new SubmissionItem(
                        UUID.randomUUID().toString(),
                        "ICT361 Assignment 2",
                        studentId,
                        "202109428",
                        "James Banda",
                        "Material Design 3 bottom navigation and theme integration.",
                        "https://github.com/jamesbanda/ICT361_Assignment2.git",
                        System.currentTimeMillis() - 86400000,
                        "SUBMITTED",
                        0,
                        "Pending lecturer review."
                ));
                dbItems = db.submissionDao().getAllSubmissions();
            }

            final List<SubmissionItem> items = dbItems;
            runOnUiThread(() -> {
                submissionList.clear();
                submissionList.addAll(items);
                submissionAdapter.notifyDataSetChanged();
            });
        });
    }
}
