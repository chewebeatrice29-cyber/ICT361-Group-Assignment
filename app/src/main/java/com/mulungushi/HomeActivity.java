package com.mulungushi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    // ⭐ Test toggles
    private static final boolean TEST_EMPTY_STATE = false;
    private static final boolean TEST_EMPTY_ACTIVITY = false;
    private static final boolean SHOW_SKELETON = false;   // ← turn skeleton on/off

    private int unreadNotificationCount = 3;

    private static final int COLOR_ACTIVE = 0xFF1976D2;
    private static final int COLOR_INACTIVE = 0xFF999999;

    private LinearLayout navHome, navGroups, navChat, navNotifications, navProfile;
    private ImageView navHomeIcon, navGroupsIcon, navChatIcon, navNotificationsIcon, navProfileIcon;
    private TextView navHomeText, navGroupsText, navChatText, navNotificationsText, navProfileText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Fade-in
        View rootView = findViewById(R.id.scrollView);
        Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        fadeIn.setDuration(300);
        rootView.startAnimation(fadeIn);

        // HEADER
        ImageView bellIcon = findViewById(R.id.bellIcon);
        bellIcon.setOnClickListener(v ->
                Toast.makeText(this, "Opening Notifications...", Toast.LENGTH_SHORT).show());

        ImageView syncIcon = findViewById(R.id.syncIcon);
        syncIcon.setOnClickListener(v -> performSync(syncIcon));

        updateNotificationBadge();

        // SEARCH
        LinearLayout searchBar = findViewById(R.id.searchBar);
        searchBar.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // GROUPS CARD — Skeleton → Real/Empty
        LinearLayout groupsSkeletonCard = findViewById(R.id.groupsSkeletonCard);
        LinearLayout groupsFilledCard = findViewById(R.id.groupsFilledCard);
        LinearLayout groupsEmptyCard = findViewById(R.id.groupsEmptyCard);

        if (SHOW_SKELETON) {
            // Start with skeleton
            groupsSkeletonCard.setVisibility(View.VISIBLE);
            groupsFilledCard.setVisibility(View.GONE);
            groupsEmptyCard.setVisibility(View.GONE);

            // After 800ms, swap to real content
            groupsSkeletonCard.postDelayed(() -> {
                // Fade out skeleton, fade in content
                Animation fadeOut = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
                fadeOut.setDuration(200);
                groupsSkeletonCard.startAnimation(fadeOut);
                groupsSkeletonCard.setVisibility(View.GONE);

                if (TEST_EMPTY_STATE) {
                    groupsEmptyCard.setVisibility(View.VISIBLE);
                    Animation fadeInContent = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
                    fadeInContent.setDuration(250);
                    groupsEmptyCard.startAnimation(fadeInContent);
                } else {
                    groupsFilledCard.setVisibility(View.VISIBLE);
                    Animation fadeInContent = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
                    fadeInContent.setDuration(250);
                    groupsFilledCard.startAnimation(fadeInContent);
                }
            }, 800);
        } else {
            // No skeleton — show directly
            groupsSkeletonCard.setVisibility(View.GONE);
            if (TEST_EMPTY_STATE) {
                groupsFilledCard.setVisibility(View.GONE);
                groupsEmptyCard.setVisibility(View.VISIBLE);
            } else {
                groupsFilledCard.setVisibility(View.VISIBLE);
                groupsEmptyCard.setVisibility(View.GONE);
            }
        }

        TextView viewAllGroups = findViewById(R.id.viewAllGroups);
        viewAllGroups.setOnClickListener(v ->
                Toast.makeText(this, "Opening My Lab Groups...", Toast.LENGTH_SHORT).show());

        TextView browseGroupsButton = findViewById(R.id.browseGroupsButton);
        browseGroupsButton.setOnClickListener(v ->
                Toast.makeText(this, "Opening Browse Groups...", Toast.LENGTH_SHORT).show());

        // QUICK ACTIONS
        LinearLayout quickProfile = findViewById(R.id.quickProfile);
        LinearLayout quickGroups = findViewById(R.id.quickGroups);
        LinearLayout quickChat = findViewById(R.id.quickChat);
        LinearLayout quickMore = findViewById(R.id.quickMore);

        quickProfile.setOnClickListener(v ->
                Toast.makeText(this, "Opening My Profile...", Toast.LENGTH_SHORT).show());

        quickGroups.setOnClickListener(v ->
                Toast.makeText(this, "Opening My Groups...", Toast.LENGTH_SHORT).show());

        quickChat.setOnClickListener(v ->
                Toast.makeText(this, "Opening Chat...", Toast.LENGTH_SHORT).show());

        quickMore.setOnClickListener(v ->
                Toast.makeText(this, "Opening More Options...", Toast.LENGTH_SHORT).show());

        // RECENT ACTIVITY — Empty vs Filled
        LinearLayout activityFilledCard = findViewById(R.id.activityFilledCard);
        LinearLayout activityEmptyCard = findViewById(R.id.activityEmptyCard);

        if (TEST_EMPTY_ACTIVITY) {
            activityFilledCard.setVisibility(View.GONE);
            activityEmptyCard.setVisibility(View.VISIBLE);
        } else {
            activityFilledCard.setVisibility(View.VISIBLE);
            activityEmptyCard.setVisibility(View.GONE);
        }

        TextView viewAllActivity = findViewById(R.id.viewAllActivity);
        viewAllActivity.setOnClickListener(v ->
                Toast.makeText(this, "Opening Activity Feed...", Toast.LENGTH_SHORT).show());

        // BOTTOM NAV
        navHome = findViewById(R.id.navHome);
        navGroups = findViewById(R.id.navGroups);
        navChat = findViewById(R.id.navChat);
        navNotifications = findViewById(R.id.navNotifications);
        navProfile = findViewById(R.id.navProfile);

        navHomeIcon = findViewById(R.id.navHomeIcon);
        navGroupsIcon = findViewById(R.id.navGroupsIcon);
        navChatIcon = findViewById(R.id.navChatIcon);
        navNotificationsIcon = findViewById(R.id.navNotificationsIcon);
        navProfileIcon = findViewById(R.id.navProfileIcon);

        navHomeText = findViewById(R.id.navHomeText);
        navGroupsText = findViewById(R.id.navGroupsText);
        navChatText = findViewById(R.id.navChatText);
        navNotificationsText = findViewById(R.id.navNotificationsText);
        navProfileText = findViewById(R.id.navProfileText);

        navHome.setOnClickListener(v -> setActiveTab("home"));

        navGroups.setOnClickListener(v -> {
            setActiveTab("groups");
            Toast.makeText(this, "Opening Groups...", Toast.LENGTH_SHORT).show();
        });

        navChat.setOnClickListener(v -> {
            setActiveTab("chat");
            Toast.makeText(this, "Opening Chat...", Toast.LENGTH_SHORT).show();
        });

        navNotifications.setOnClickListener(v -> {
            setActiveTab("notifications");
            Toast.makeText(this, "Opening Notifications...", Toast.LENGTH_SHORT).show();
        });

        navProfile.setOnClickListener(v -> {
            setActiveTab("profile");
            Toast.makeText(this, "Opening Profile...", Toast.LENGTH_SHORT).show();
        });

        // AUTO-SYNC
        syncIcon.postDelayed(() -> performSilentSync(syncIcon), 500);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void setActiveTab(String active) {
        setTabState(navHomeIcon, navHomeText, false);
        setTabState(navGroupsIcon, navGroupsText, false);
        setTabState(navChatIcon, navChatText, false);
        setTabState(navNotificationsIcon, navNotificationsText, false);
        setTabState(navProfileIcon, navProfileText, false);

        switch (active) {
            case "home": setTabState(navHomeIcon, navHomeText, true); break;
            case "groups": setTabState(navGroupsIcon, navGroupsText, true); break;
            case "chat": setTabState(navChatIcon, navChatText, true); break;
            case "notifications": setTabState(navNotificationsIcon, navNotificationsText, true); break;
            case "profile": setTabState(navProfileIcon, navProfileText, true); break;
        }
    }

    private void setTabState(ImageView icon, TextView text, boolean isActive) {
        int color = isActive ? COLOR_ACTIVE : COLOR_INACTIVE;
        icon.setColorFilter(color);
        text.setTextColor(color);
        text.setTypeface(null, isActive
                ? android.graphics.Typeface.BOLD
                : android.graphics.Typeface.NORMAL);
    }

    private void updateNotificationBadge() {
        TextView badge = findViewById(R.id.notificationBadge);

        if (unreadNotificationCount <= 0) {
            badge.setVisibility(View.GONE);
        } else {
            badge.setVisibility(View.VISIBLE);
            if (unreadNotificationCount > 99) {
                badge.setText("99+");
            } else {
                badge.setText(String.valueOf(unreadNotificationCount));
            }
        }
    }

    private void performSilentSync(ImageView syncIcon) {
        RotateAnimation rotate = new RotateAnimation(
                0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        rotate.setDuration(1000);
        syncIcon.startAnimation(rotate);
    }

    private void performSync(ImageView syncIcon) {
        RotateAnimation rotate = new RotateAnimation(
                0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        rotate.setDuration(1000);
        syncIcon.startAnimation(rotate);

        syncIcon.postDelayed(() ->
                Toast.makeText(this, "Synced just now ✅", Toast.LENGTH_SHORT).show(), 1000);
    }
}