package com.example.labgroupmanager;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.labgroupmanager.ui.AppLifecycleTracker;

public class LabGroupManagerApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Follow phone system dark mode settings automatically
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        new AppLifecycleTracker(this);
    }
}
