package com.example.labgroupmanager.ui.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labgroupmanager.R;
import com.example.labgroupmanager.ui.adapter.StudentAdapter;
import com.example.labgroupmanager.ui.viewmodel.StudentViewModel;

public class GroupDetailsActivity extends AppCompatActivity {

    private TextView tvGroupDetailsTitle, tvGroupDetailsOccupancy;
    private RecyclerView rvGroupMembers;

    private StudentViewModel studentViewModel;
    private StudentAdapter studentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);

        tvGroupDetailsTitle = findViewById(R.id.tvGroupDetailsTitle);
        tvGroupDetailsOccupancy = findViewById(R.id.tvGroupDetailsOccupancy);
        rvGroupMembers = findViewById(R.id.rvGroupMembers);

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
    }
}
