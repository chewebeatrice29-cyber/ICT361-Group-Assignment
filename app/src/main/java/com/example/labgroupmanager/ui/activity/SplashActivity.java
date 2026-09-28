package com.example.labgroupmanager.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.labgroupmanager.R;
import com.example.labgroupmanager.data.local.SessionManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SessionManager sessionManager = new SessionManager(this);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            String remembered = sessionManager.getRememberedPortal();
            if ("STUDENT".equals(remembered)) {
                startActivity(new Intent(SplashActivity.this, StudentLoginActivity.class));
            } else if ("LECTURER".equals(remembered)) {
                startActivity(new Intent(SplashActivity.this, LecturerLoginActivity.class));
            } else {
                // Always reset to Choice screen on startup unless remembered
                startActivity(new Intent(SplashActivity.this, RoleSelectionActivity.class));
            }
            finish();
        }, 1500);
    }
}
