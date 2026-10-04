package com.example.labgroupmanager.ui;

import android.app.Activity;
import android.content.Intent;

import com.example.labgroupmanager.R;
import com.example.labgroupmanager.data.local.SessionManager;
import com.example.labgroupmanager.ui.activity.GroupDetailsActivity;
import com.example.labgroupmanager.ui.activity.GroupManagementActivity;
import com.example.labgroupmanager.ui.activity.LecturerHomeActivity;
import com.example.labgroupmanager.ui.activity.LecturerRosterActivity;
import com.example.labgroupmanager.ui.activity.NotificationsActivity;
import com.example.labgroupmanager.ui.activity.SettingsActivity;
import com.example.labgroupmanager.ui.activity.StudentHomeActivity;
import com.example.labgroupmanager.ui.activity.StudentProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavHelper {

    public static void setupBottomNav(Activity activity, int selectedItemId) {
        BottomNavigationView bottomNav = activity.findViewById(R.id.bottomNavUniversal);
        if (bottomNav == null) bottomNav = activity.findViewById(R.id.bottomNavStudent);
        if (bottomNav == null) bottomNav = activity.findViewById(R.id.bottomNavLecturer);
        if (bottomNav == null) bottomNav = activity.findViewById(R.id.bottomNavProfile);
        if (bottomNav == null) bottomNav = activity.findViewById(R.id.bottomNavGroupDetails);

        if (bottomNav != null) {
            SessionManager sessionManager = new SessionManager(activity);
            boolean isLecturer = sessionManager.isLecturer();

            if (selectedItemId != 0) {
                bottomNav.setSelectedItemId(selectedItemId);
            }

            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_home || id == R.id.nav_lecturer_home) {
                    if (!(activity instanceof StudentHomeActivity) && !(activity instanceof LecturerHomeActivity)) {
                        Intent intent = isLecturer ?
                                new Intent(activity, LecturerHomeActivity.class) :
                                new Intent(activity, StudentHomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        activity.startActivity(intent);
                    }
                    return true;
                } else if (id == R.id.nav_group) {
                    if (!(activity instanceof GroupDetailsActivity)) {
                        Intent intent = new Intent(activity, GroupDetailsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        activity.startActivity(intent);
                    }
                    return true;
                } else if (id == R.id.nav_lecturer_groups) {
                    if (!(activity instanceof GroupManagementActivity)) {
                        Intent intent = new Intent(activity, GroupManagementActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        activity.startActivity(intent);
                    }
                    return true;
                } else if (id == R.id.nav_lecturer_students) {
                    if (!(activity instanceof LecturerRosterActivity)) {
                        Intent intent = new Intent(activity, LecturerRosterActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        activity.startActivity(intent);
                    }
                    return true;
                } else if (id == R.id.nav_notifications || id == R.id.nav_lecturer_notifs) {
                    if (!(activity instanceof NotificationsActivity)) {
                        Intent intent = new Intent(activity, NotificationsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        activity.startActivity(intent);
                    }
                    return true;
                } else if (id == R.id.nav_profile) {
                    if (!(activity instanceof StudentProfileActivity)) {
                        Intent intent = new Intent(activity, StudentProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        activity.startActivity(intent);
                    }
                    return true;
                } else if (id == R.id.nav_lecturer_profile) {
                    if (!(activity instanceof SettingsActivity)) {
                        Intent intent = new Intent(activity, SettingsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        activity.startActivity(intent);
                    }
                    return true;
                }
                return false;
            });
        }
    }
}
