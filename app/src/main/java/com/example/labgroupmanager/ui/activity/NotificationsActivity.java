package com.example.labgroupmanager.ui.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labgroupmanager.R;
import com.example.labgroupmanager.data.model.NotificationItem;
import com.example.labgroupmanager.ui.adapter.NotificationAdapter;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        RecyclerView rvNotifications = findViewById(R.id.rvNotifications);
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));

        List<NotificationItem> list = new ArrayList<>();
        list.add(new NotificationItem("Registration Confirmed", "Your student account has been successfully verified in MUConnect.", "Today - 10:24 AM"));
        list.add(new NotificationItem("Lab Group Allocation", "You have been assigned to Lab Group G01 (12/15 active members).", "Yesterday - 04:32 PM"));
        list.add(new NotificationItem("Sync & Offline Status", "All pending Room database changes synchronized successfully with MySQL.", "Today - 08:15 AM"));
        list.add(new NotificationItem("Mulungushi University Announcement", "ICT361 group lab practical submissions and demonstrations due Friday.", "2 days ago"));

        NotificationAdapter adapter = new NotificationAdapter(list);
        rvNotifications.setAdapter(adapter);
    }
}
