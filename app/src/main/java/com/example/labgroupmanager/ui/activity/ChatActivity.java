package com.example.labgroupmanager.ui.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labgroupmanager.R;
import com.example.labgroupmanager.data.local.AppDatabase;
import com.example.labgroupmanager.data.local.SessionManager;
import com.example.labgroupmanager.data.model.MessageItem;
import com.example.labgroupmanager.ui.adapter.ChatAdapter;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {

    private TextView tvChatHeaderName;
    private RecyclerView rvChatMessages;
    private TextInputEditText etChatMessageInput;
    private Button btnSendChatMessage;

    private SessionManager sessionManager;
    private ChatAdapter chatAdapter;
    private final List<MessageItem> messageList = new ArrayList<>();
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sessionManager = new SessionManager(this);
        currentUserId = sessionManager.getAccountId() != null ? sessionManager.getAccountId() : "user-id";

        tvChatHeaderName = findViewById(R.id.tvChatHeaderName);
        rvChatMessages = findViewById(R.id.rvChatMessages);
        etChatMessageInput = findViewById(R.id.etChatMessageInput);
        btnSendChatMessage = findViewById(R.id.btnSendChatMessage);

        rvChatMessages.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter(messageList, currentUserId);
        rvChatMessages.setAdapter(chatAdapter);

        if (sessionManager.isLecturer()) {
            tvChatHeaderName.setText("Lecturer Broadcast & Chat");
        } else {
            tvChatHeaderName.setText("MUConnect Chat Support");
        }

        btnSendChatMessage.setOnClickListener(v -> sendMessage());

        loadMessagesAndSeedIfEmpty();
    }

    private void loadMessagesAndSeedIfEmpty() {
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        db.messageDao().getMessagesForUserLiveData(currentUserId).observe(this, messages -> {
            if (messages == null || messages.isEmpty()) {
                seedInitialMessages();
            } else {
                messageList.clear();
                messageList.addAll(messages);
                chatAdapter.notifyDataSetChanged();
                rvChatMessages.scrollToPosition(messageList.size() - 1);
            }
        });
    }

    private void seedInitialMessages() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            db.messageDao().insertMessage(new MessageItem(
                    UUID.randomUUID().toString(),
                    "lecturer-acc-id",
                    "Dr. M. Banda",
                    "LECTURER",
                    currentUserId,
                    "Student",
                    "Welcome to MUConnect! Feel free to ask questions regarding ICT361 lab groups or schedules.",
                    System.currentTimeMillis() - 3600000
            ));
        });
    }

    private void sendMessage() {
        String text = etChatMessageInput.getText() != null ? etChatMessageInput.getText().toString().trim() : "";
        if (text.isEmpty()) return;

        String senderName = sessionManager.getUsername() != null ? sessionManager.getUsername() : "User";
        String senderRole = sessionManager.isLecturer() ? "LECTURER" : "STUDENT";

        MessageItem newMsg = new MessageItem(
                UUID.randomUUID().toString(),
                currentUserId,
                senderName,
                senderRole,
                "ALL",
                "Recipient",
                text,
                System.currentTimeMillis()
        );

        etChatMessageInput.setText("");

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase.getInstance(getApplicationContext()).messageDao().insertMessage(newMsg);
            runOnUiThread(() -> Toast.makeText(ChatActivity.this, "Message sent!", Toast.LENGTH_SHORT).show());
        });
    }
}
