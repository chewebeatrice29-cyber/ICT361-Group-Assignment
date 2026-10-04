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
import com.google.android.material.card.MaterialCardView;

public class StudentHomeActivity extends AppCompatActivity {

    private TextView tvStudentHomeGreeting, tvStudentSubDetail, tvHomeGroupTitle, tvHomeGroupOccupancy;
    private Button btnViewGroupDetails, btnViewSchedule;
    private ImageView ivNotificationBell, ivSettingsIcon;
    private MaterialCardView cardShortcutProfile, cardShortcutGroup, cardShortcutMessages, cardShortcutNotifs, cardShortcutSchedule, cardShortcutCourses;
    private BottomNavigationView bottomNavStudent;

    private StudentViewModel studentViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        tvStudentHomeGreeting = findViewById(R.id.tvStudentHomeGreeting);
        tvStudentSubDetail = findViewById(R.id.tvStudentSubDetail);
        tvHomeGroupTitle = findViewById(R.id.tvHomeGroupTitle);
        tvHomeGroupOccupancy = findViewById(R.id.tvHomeGroupOccupancy);

        btnViewGroupDetails = findViewById(R.id.btnViewGroupDetails);
        btnViewSchedule = findViewById(R.id.btnViewSchedule);

        ivNotificationBell = findViewById(R.id.ivNotificationBell);
        ivSettingsIcon = findViewById(R.id.ivSettingsIcon);

        cardShortcutProfile = findViewById(R.id.cardShortcutProfile);
        cardShortcutGroup = findViewById(R.id.cardShortcutGroup);
        cardShortcutMessages = findViewById(R.id.cardShortcutMessages);
        cardShortcutNotifs = findViewById(R.id.cardShortcutNotifs);
        cardShortcutSchedule = findViewById(R.id.cardShortcutSchedule);
        cardShortcutCourses = findViewById(R.id.cardShortcutCourses);

        bottomNavStudent = findViewById(R.id.bottomNavStudent);

        studentViewModel = new ViewModelProvider(this).get(StudentViewModel.class);

        studentViewModel.getOwnStudentProfile().observe(this, student -> {
            if (student != null) {
                String name = student.getStudentName() != null ? student.getStudentName() : "Student";
                String prog = student.getProgramme() != null ? student.getProgramme() : "N/A";
                String num = student.getStudentNumber() != null ? student.getStudentNumber() : "";
                String group = student.getLabGroup() != null ? student.getLabGroup() : "Unassigned";

                if (tvStudentHomeGreeting != null) tvStudentHomeGreeting.setText("Hello, " + name);
                if (tvStudentSubDetail != null) tvStudentSubDetail.setText(prog + (num.isEmpty() ? "" : "  •  " + num));
                if (tvHomeGroupTitle != null) tvHomeGroupTitle.setText("Group " + group);
                if (tvHomeGroupOccupancy != null) tvHomeGroupOccupancy.setText("Active Group Assignment");
            }
        });

        if (btnViewGroupDetails != null) btnViewGroupDetails.setOnClickListener(v -> startActivity(new Intent(this, GroupDetailsActivity.class)));
        if (btnViewSchedule != null) btnViewSchedule.setOnClickListener(v -> startActivity(new Intent(this, ScheduleActivity.class)));

        if (ivNotificationBell != null) ivNotificationBell.setOnClickListener(v -> startActivity(new Intent(this, NotificationsActivity.class)));
        if (ivSettingsIcon != null) ivSettingsIcon.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));

        if (cardShortcutProfile != null) cardShortcutProfile.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        if (cardShortcutGroup != null) cardShortcutGroup.setOnClickListener(v -> startActivity(new Intent(this, GroupDetailsActivity.class)));
        if (cardShortcutMessages != null) cardShortcutMessages.setOnClickListener(v -> startActivity(new Intent(this, ChatActivity.class)));
        if (cardShortcutNotifs != null) cardShortcutNotifs.setOnClickListener(v -> startActivity(new Intent(this, NotificationsActivity.class)));
        if (cardShortcutSchedule != null) cardShortcutSchedule.setOnClickListener(v -> startActivity(new Intent(this, ScheduleActivity.class)));
        if (cardShortcutCourses != null) cardShortcutCourses.setOnClickListener(v -> startActivity(new Intent(this, AvailableCoursesActivity.class)));

        if (bottomNavStudent != null) {
            bottomNavStudent.setSelectedItemId(R.id.nav_home);
            bottomNavStudent.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    return true;
                } else if (id == R.id.nav_group) {
                    startActivity(new Intent(this, GroupDetailsActivity.class));
                    return true;
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(this, SettingsActivity.class));
                    return true;
                } else if (id == R.id.nav_notifications) {
                    startActivity(new Intent(this, NotificationsActivity.class));
                    return true;
                }
                return false;
            });
        }
    }
}
