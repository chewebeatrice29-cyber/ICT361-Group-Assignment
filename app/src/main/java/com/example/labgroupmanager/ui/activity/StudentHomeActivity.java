package com.example.labgroupmanager.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

                tvStudentHomeGreeting.setText("Hello, " + name);
                tvStudentSubDetail.setText(prog + (num.isEmpty() ? "" : "  •  " + num));
                tvHomeGroupTitle.setText("Group " + group);
                tvHomeGroupOccupancy.setText("Active Group Assignment");
            }
        });

        btnViewGroupDetails.setOnClickListener(v -> startActivity(new Intent(this, GroupDetailsActivity.class)));
        btnViewSchedule.setOnClickListener(v -> Toast.makeText(this, "Next Lab Session: Monday 10:00 AM at Computer Lab 2", Toast.LENGTH_LONG).show());

        ivNotificationBell.setOnClickListener(v -> startActivity(new Intent(this, NotificationsActivity.class)));
        ivSettingsIcon.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));

        // Profile options accessed under Settings / Options
        cardShortcutProfile.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        cardShortcutGroup.setOnClickListener(v -> startActivity(new Intent(this, GroupDetailsActivity.class)));
        cardShortcutMessages.setOnClickListener(v -> startActivity(new Intent(this, NotificationsActivity.class)));
        cardShortcutNotifs.setOnClickListener(v -> startActivity(new Intent(this, NotificationsActivity.class)));
        cardShortcutSchedule.setOnClickListener(v -> Toast.makeText(this, "Lab Schedule: Mon 10:00 AM Computer Lab 2", Toast.LENGTH_SHORT).show());
        cardShortcutCourses.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));

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
