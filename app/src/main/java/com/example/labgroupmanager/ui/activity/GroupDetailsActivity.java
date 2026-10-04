package com.example.labgroupmanager.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labgroupmanager.R;
import com.example.labgroupmanager.ui.adapter.StudentAdapter;
import com.example.labgroupmanager.ui.viewmodel.StudentViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class GroupDetailsActivity extends AppCompatActivity {

    private TextView tvGroupDetailsTitle, tvGroupDetailsOccupancy;
    private RecyclerView rvGroupMembers;
    private BottomNavigationView bottomNavGroupDetails;

    private StudentViewModel studentViewModel;
    private StudentAdapter studentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);

        tvGroupDetailsTitle = findViewById(R.id.tvGroupDetailsTitle);
        tvGroupDetailsOccupancy = findViewById(R.id.tvGroupDetailsOccupancy);
        rvGroupMembers = findViewById(R.id.rvGroupMembers);
        bottomNavGroupDetails = findViewById(R.id.bottomNavGroupDetails);

        rvGroupMembers.setLayoutManager(new LinearLayoutManager(this));
        studentAdapter = new StudentAdapter(student -> {
            // Member click
        });
        rvGroupMembers.setAdapter(studentAdapter);

        studentViewModel = new ViewModelProvider(this).get(StudentViewModel.class);

        studentViewModel.getOwnStudentProfile().observe(this, student -> {
            if (student != null) {
                String group = student.getLabGroup();
                tvGroupDetailsTitle.setText("Group " + group);
                studentViewModel.updateFilter(group, "All", "");
            }
        });

        studentViewModel.getFilteredStudents().observe(this, students -> {
            if (students != null) {
                studentAdapter.submitList(students);
                tvGroupDetailsOccupancy.setText(students.size() + " / 15 active members");
            }
        });

        if (bottomNavGroupDetails != null) {
            bottomNavGroupDetails.setSelectedItemId(R.id.nav_group);
            bottomNavGroupDetails.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    Intent intent = new Intent(this, StudentHomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(this, SettingsActivity.class));
                    return true;
                } else if (id == R.id.nav_notifications) {
                    startActivity(new Intent(this, NotificationsActivity.class));
                    return true;
                }
                return true;
            });
        }
    }
}
