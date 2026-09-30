package com.example.labgroupmanager.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.labgroupmanager.R;
import com.example.labgroupmanager.ui.viewmodel.StudentViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class StudentHomeActivity extends AppCompatActivity {

    private TextView tvStudentHomeGreeting, tvHomeGroupTitle, tvHomeGroupOccupancy;
    private Button btnViewGroupDetails, btnShortcutProfile, btnShortcutGroup, btnShortcutNotifications;
    private ImageView ivNotificationBell;
    private BottomNavigationView bottomNavStudent;

    private StudentViewModel studentViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        tvStudentHomeGreeting = findViewById(R.id.tvStudentHomeGreeting);
        tvHomeGroupTitle = findViewById(R.id.tvHomeGroupTitle);
        tvHomeGroupOccupancy = findViewById(R.id.tvHomeGroupOccupancy);

        btnViewGroupDetails = findViewById(R.id.btnViewGroupDetails);
        btnShortcutProfile = findViewById(R.id.btnShortcutProfile);
        btnShortcutGroup = findViewById(R.id.btnShortcutGroup);
        btnShortcutNotifications = findViewById(R.id.btnShortcutNotifications);
        ivNotificationBell = findViewById(R.id.ivNotificationBell);
        bottomNavStudent = findViewById(R.id.bottomNavStudent);

        studentViewModel = new ViewModelProvider(this).get(StudentViewModel.class);

        studentViewModel.getOwnStudentProfile().observe(this, student -> {
            if (student != null) {
                tvStudentHomeGreeting.setText("Hello, " + student.getStudentName());
                tvHomeGroupTitle.setText("Group " + student.getLabGroup());
                tvHomeGroupOccupancy.setText("Active Group Assignment");
            }
        });

        btnViewGroupDetails.setOnClickListener(v -> startActivity(new Intent(this, StudentProfileActivity.class)));
        btnShortcutProfile.setOnClickListener(v -> startActivity(new Intent(this, StudentProfileActivity.class)));
        btnShortcutGroup.setOnClickListener(v -> startActivity(new Intent(this, StudentProfileActivity.class)));
        btnShortcutNotifications.setOnClickListener(v -> startActivity(new Intent(this, NotificationsActivity.class)));
        ivNotificationBell.setOnClickListener(v -> startActivity(new Intent(this, NotificationsActivity.class)));

        bottomNavStudent.setSelectedItemId(R.id.nav_home);
        bottomNavStudent.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_profile || id == R.id.nav_group) {
                startActivity(new Intent(this, StudentProfileActivity.class));
                return true;
            } else if (id == R.id.nav_notifications) {
                startActivity(new Intent(this, NotificationsActivity.class));
                return true;
            }
            return false;
        });
    }
}
