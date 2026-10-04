package com.example.labgroupmanager.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labgroupmanager.R;
import com.example.labgroupmanager.data.model.NotificationItem;
import com.example.labgroupmanager.ui.adapter.NotificationAdapter;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private RecyclerView rvNotifications;
    private TextView tvClearAllNotifs;
    private Button btnFilterNotifAll, btnFilterNotifMessages, btnFilterNotifSystem;
    private NotificationAdapter adapter;
    private final List<NotificationItem> allNotifs = new ArrayList<>();
    private final List<NotificationItem> displayedNotifs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        rvNotifications = findViewById(R.id.rvNotifications);
        tvClearAllNotifs = findViewById(R.id.tvClearAllNotifs);
        btnFilterNotifAll = findViewById(R.id.btnFilterNotifAll);
        btnFilterNotifMessages = findViewById(R.id.btnFilterNotifMessages);
        btnFilterNotifSystem = findViewById(R.id.btnFilterNotifSystem);

        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAdapter(displayedNotifs);
        rvNotifications.setAdapter(adapter);

        seedNotifications();

        btnFilterNotifAll.setOnClickListener(v -> filterNotifications("ALL"));
        btnFilterNotifMessages.setOnClickListener(v -> filterNotifications("MESSAGES"));
        btnFilterNotifSystem.setOnClickListener(v -> filterNotifications("SYSTEM"));

        tvClearAllNotifs.setOnClickListener(v -> {
            displayedNotifs.clear();
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "All notifications marked as read.", Toast.LENGTH_SHORT).show();
        });
    }

    private void seedNotifications() {
        allNotifs.clear();
        allNotifs.add(new NotificationItem("Course Registration Approved", "Dr. M. Banda approved your requested courses: ICT361, CS311, CS321.", "Today - 10:24 AM"));
        allNotifs.add(new NotificationItem("New Chat Message", "Dr. M. Banda: Please ensure your ICT361 lab submission is ready.", "Today - 09:45 AM"));
        allNotifs.add(new NotificationItem("Lab Group Allocation", "You have been assigned to Lab Group G01 (12/15 active members).", "Yesterday - 04:32 PM"));
        allNotifs.add(new NotificationItem("Sync & Offline Status", "All pending Room database changes synchronized successfully with MySQL.", "Today - 08:15 AM"));

        filterNotifications("ALL");
    }

    private void filterNotifications(String type) {
        displayedNotifs.clear();
        if ("ALL".equalsIgnoreCase(type)) {
            displayedNotifs.addAll(allNotifs);
        } else if ("MESSAGES".equalsIgnoreCase(type)) {
            for (NotificationItem item : allNotifs) {
                if (item.getTitle().contains("Message") || item.getTitle().contains("Chat")) {
                    displayedNotifs.add(item);
                }
            }
        } else {
            for (NotificationItem item : allNotifs) {
                if (item.getTitle().contains("Sync") || item.getTitle().contains("Approved") || item.getTitle().contains("Group")) {
                    displayedNotifs.add(item);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}
