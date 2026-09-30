package com.example.labgroupmanager.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.labgroupmanager.R;
import com.example.labgroupmanager.data.local.SessionManager;
import com.example.labgroupmanager.data.model.GroupSummary;
import com.example.labgroupmanager.ui.viewmodel.StudentViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

import java.util.Map;

public class LecturerHomeActivity extends AppCompatActivity {

    private TextView tvLecturerHomeGreeting, tvLecturerTotalCount, tvLecturerViewAllStudents;
    private ImageView ivLecturerNotifBell, ivLecturerSettings;
    private MaterialCardView cardLecturerShortcutStudents, cardLecturerShortcutGroups, cardLecturerShortcutSearch, cardLecturerShortcutFilters;
    private Button btnLecturerAddStudent, btnLecturerShareSummary, btnLecturerBroadcast;
    private BottomNavigationView bottomNavLecturer;

    private StudentViewModel studentViewModel;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer_home);

        sessionManager = new SessionManager(this);

        tvLecturerHomeGreeting = findViewById(R.id.tvLecturerHomeGreeting);
        tvLecturerTotalCount = findViewById(R.id.tvLecturerTotalCount);
        tvLecturerViewAllStudents = findViewById(R.id.tvLecturerViewAllStudents);

        ivLecturerNotifBell = findViewById(R.id.ivLecturerNotifBell);
        ivLecturerSettings = findViewById(R.id.ivLecturerSettings);

        cardLecturerShortcutStudents = findViewById(R.id.cardLecturerShortcutStudents);
        cardLecturerShortcutGroups = findViewById(R.id.cardLecturerShortcutGroups);
        cardLecturerShortcutSearch = findViewById(R.id.cardLecturerShortcutSearch);
        cardLecturerShortcutFilters = findViewById(R.id.cardLecturerShortcutFilters);

        btnLecturerAddStudent = findViewById(R.id.btnLecturerAddStudent);
        btnLecturerShareSummary = findViewById(R.id.btnLecturerShareSummary);
        btnLecturerBroadcast = findViewById(R.id.btnLecturerBroadcast);

        bottomNavLecturer = findViewById(R.id.bottomNavLecturer);

        studentViewModel = new ViewModelProvider(this).get(StudentViewModel.class);

        // Update greeting and count
        String username = sessionManager.getUsername();
        if (username != null && !username.isEmpty()) {
            tvLecturerHomeGreeting.setText("Good Morning, " + username);
        }

        studentViewModel.getFilteredStudents().observe(this, students -> {
            if (students != null) {
                tvLecturerTotalCount.setText(String.valueOf(students.size()));
            }
        });

        studentViewModel.getGroupSummaryLiveData().observe(this, summary -> {
            if (summary != null) {
                shareGroupSummary(summary);
            }
        });

        // Click listeners
        View.OnClickListener openRosterListener = v -> startActivity(new Intent(this, LecturerRosterActivity.class));

        if (cardLecturerShortcutStudents != null) cardLecturerShortcutStudents.setOnClickListener(openRosterListener);
        if (cardLecturerShortcutGroups != null) cardLecturerShortcutGroups.setOnClickListener(openRosterListener);
        if (cardLecturerShortcutSearch != null) cardLecturerShortcutSearch.setOnClickListener(openRosterListener);
        if (cardLecturerShortcutFilters != null) cardLecturerShortcutFilters.setOnClickListener(openRosterListener);
        if (tvLecturerViewAllStudents != null) tvLecturerViewAllStudents.setOnClickListener(openRosterListener);

        if (ivLecturerNotifBell != null) ivLecturerNotifBell.setOnClickListener(v -> startActivity(new Intent(this, NotificationsActivity.class)));
        if (ivLecturerSettings != null) ivLecturerSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));

        if (btnLecturerAddStudent != null) {
            btnLecturerAddStudent.setOnClickListener(v -> startActivity(new Intent(this, StudentEditorActivity.class)));
        }

        if (btnLecturerShareSummary != null) {
            btnLecturerShareSummary.setOnClickListener(v -> studentViewModel.fetchGroupSummaryForSharesheet());
        }

        if (btnLecturerBroadcast != null) {
            btnLecturerBroadcast.setOnClickListener(v -> startActivity(new Intent(this, SendMessageActivity.class)));
        }

        if (bottomNavLecturer != null) {
            bottomNavLecturer.setSelectedItemId(R.id.nav_lecturer_home);
            bottomNavLecturer.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_lecturer_home) {
                    return true;
                } else if (id == R.id.nav_lecturer_students || id == R.id.nav_lecturer_groups) {
                    startActivity(new Intent(this, LecturerRosterActivity.class));
                    return true;
                } else if (id == R.id.nav_lecturer_notifs) {
                    startActivity(new Intent(this, NotificationsActivity.class));
                    return true;
                } else if (id == R.id.nav_lecturer_profile) {
                    startActivity(new Intent(this, SettingsActivity.class));
                    return true;
                }
                return false;
            });
        }
    }

    private void shareGroupSummary(GroupSummary summary) {
        StringBuilder sb = new StringBuilder();
        sb.append("ICT361 Lab Group Summary Report\n");
        sb.append("===================================\n");
        if (summary.getGroupCounts() != null) {
            for (Map.Entry<String, Integer> entry : summary.getGroupCounts().entrySet()) {
                sb.append(entry.getKey()).append(": ").append(entry.getValue());
                if (!"Unassigned".equals(entry.getKey())) {
                    sb.append("/15 members");
                }
                sb.append("\n");
            }
        }
        sb.append("===================================\n");
        sb.append("Total Active Students: ").append(summary.getTotalActiveStudents());

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "ICT361 Lab Group Summary");
        shareIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());

        startActivity(Intent.createChooser(shareIntent, "Share Group Summary"));
    }
}
