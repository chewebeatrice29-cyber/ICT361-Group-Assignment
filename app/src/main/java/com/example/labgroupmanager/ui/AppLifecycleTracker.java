package com.example.labgroupmanager.ui;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.labgroupmanager.data.local.SessionManager;
import com.example.labgroupmanager.ui.activity.LoginActivity;
import com.example.labgroupmanager.ui.activity.RoleSelectionActivity;
import com.example.labgroupmanager.ui.activity.SplashActivity;

public class AppLifecycleTracker implements Application.ActivityLifecycleCallbacks {

    private int numStarted = 0;
    private final SessionManager sessionManager;

    public AppLifecycleTracker(Application application) {
        this.sessionManager = new SessionManager(application);
        application.registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        numStarted++;
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        // Skip check if on Splash, Login, or Role Selection screen
        if (activity instanceof SplashActivity || activity instanceof LoginActivity || activity instanceof RoleSelectionActivity) {
            return;
        }

        // Check 5-minute background session expiry
        if (sessionManager.isLoggedIn() && sessionManager.isSessionExpiredAfterBackground()) {
            sessionManager.clearSession();
            Toast.makeText(activity, "Session expired due to background inactivity (> 5 min). Please sign in again.", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(activity, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(intent);
            activity.finish();
        }
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {}

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        numStarted--;
        if (numStarted == 0) {
            // App entered background or was closed
            sessionManager.recordBackgroundTime();
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {}
    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {}
}
