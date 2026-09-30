package com.example.labgroupmanager.ui.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.labgroupmanager.R;
import com.example.labgroupmanager.data.local.AppDatabase;
import com.example.labgroupmanager.data.local.SessionManager;
import com.example.labgroupmanager.data.model.MessageItem;
import com.google.android.material.textfield.TextInputEditText;

import java.util.UUID;
import java.util.concurrent.Executors;

public class SendMessageActivity extends AppCompatActivity {

    private Spinner spinnerTargetRecipient;
    private TextInputEditText etMessageTitle, etMessageContent;
    private Button btnSendMessageSubmit;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        sessionManager = new SessionManager(this);

        spinnerTargetRecipient = findViewById(R.id.spinnerTargetRecipient);
        etMessageTitle = findViewById(R.id.etMessageTitle);
        etMessageContent = findViewById(R.id.etMessageContent);
        btnSendMessageSubmit = findViewById(R.id.btnSendMessageSubmit);

        ArrayAdapter<String> targetAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{
                "All Registered Students", "Group G01 Members", "Group G02 Members", "Group G03 Members", "Group G04 Members"
        });
        targetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTargetRecipient.setAdapter(targetAdapter);

        btnSendMessageSubmit.setOnClickListener(v -> {
            String title = etMessageTitle.getText() != null ? etMessageTitle.getText().toString().trim() : "";
            String content = etMessageContent.getText() != null ? etMessageContent.getText().toString().trim() : "";
            String recipient = spinnerTargetRecipient.getSelectedItem().toString();

            if (content.isEmpty()) {
                Toast.makeText(this, "Please enter message content.", Toast.LENGTH_SHORT).show();
                return;
            }

            String senderName = sessionManager.getUsername() != null ? sessionManager.getUsername() : "Dr. M. Banda";
            String fullMsg = (title.isEmpty() ? "" : "[" + title + "] ") + content;

            MessageItem msg = new MessageItem(
                    UUID.randomUUID().toString(),
                    sessionManager.getAccountId() != null ? sessionManager.getAccountId() : "lecturer-id",
                    senderName,
                    "LECTURER",
                    "ALL",
                    recipient,
                    fullMsg,
                    System.currentTimeMillis()
            );

            Executors.newSingleThreadExecutor().execute(() -> {
                AppDatabase.getInstance(getApplicationContext()).messageDao().insertMessage(msg);
                runOnUiThread(() -> {
                    Toast.makeText(SendMessageActivity.this, "Broadcast Message Sent to " + recipient + "!", Toast.LENGTH_LONG).show();
                    finish();
                });
            });
        });
    }
}
