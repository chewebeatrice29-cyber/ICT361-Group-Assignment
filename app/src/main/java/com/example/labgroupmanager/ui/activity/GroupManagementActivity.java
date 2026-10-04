package com.example.labgroupmanager.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.labgroupmanager.R;

public class GroupManagementActivity extends AppCompatActivity {

    private Button btnCreateNewGroup, btnSendGroupInvitation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_management);

        btnCreateNewGroup = findViewById(R.id.btnCreateNewGroup);
        btnSendGroupInvitation = findViewById(R.id.btnSendGroupInvitation);

        btnCreateNewGroup.setOnClickListener(v -> {
            Toast.makeText(this, "New Group G05 Created! Capacity set to 15 members.", Toast.LENGTH_LONG).show();
        });

        btnSendGroupInvitation.setOnClickListener(v -> {
            startActivity(new Intent(GroupManagementActivity.this, SendMessageActivity.class));
        });
    }
}
