package com.example.labgroupmanager.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "messages")
public class MessageItem {

    @PrimaryKey
    @NonNull
    @SerializedName("messageId")
    private String messageId;

    @NonNull
    @SerializedName("senderId")
    private String senderId;

    @NonNull
    @SerializedName("senderName")
    private String senderName;

    @NonNull
    @SerializedName("senderRole")
    private String senderRole; // STUDENT or LECTURER

    @NonNull
    @SerializedName("recipientId")
    private String recipientId; // Target user ID or "ALL"

    @NonNull
    @SerializedName("recipientName")
    private String recipientName;

    @NonNull
    @SerializedName("content")
    private String content;

    private long timestamp;

    public MessageItem(@NonNull String messageId, @NonNull String senderId, @NonNull String senderName,
                       @NonNull String senderRole, @NonNull String recipientId, @NonNull String recipientName,
                       @NonNull String content, long timestamp) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderRole = senderRole;
        this.recipientId = recipientId;
        this.recipientName = recipientName;
        this.content = content;
        this.timestamp = timestamp;
    }

    @NonNull public String getMessageId() { return messageId; }
    @NonNull public String getSenderId() { return senderId; }
    @NonNull public String getSenderName() { return senderName; }
    @NonNull public String getSenderRole() { return senderRole; }
    @NonNull public String getRecipientId() { return recipientId; }
    @NonNull public String getRecipientName() { return recipientName; }
    @NonNull public String getContent() { return content; }
    public long getTimestamp() { return timestamp; }
}
