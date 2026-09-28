package com.example.labgroupmanager.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sync_operations")
public class SyncOperation {

    @PrimaryKey
    @NonNull
    private String operationId;

    @NonNull
    private String studentId;

    @NonNull
    private String accountId;

    @NonNull
    private String type; // CREATE_STUDENT, UPDATE_STUDENT, GROUP_CHANGE, DELETE_STUDENT

    @NonNull
    private String payloadJson; // JSON representation of data

    private int baseVersion;

    @NonNull
    private String status; // PENDING, SYNCING, SYNCED, FAILED, CONFLICT

    private long timestamp;

    public SyncOperation(@NonNull String operationId, @NonNull String studentId, @NonNull String accountId,
                         @NonNull String type, @NonNull String payloadJson, int baseVersion,
                         @NonNull String status, long timestamp) {
        this.operationId = operationId;
        this.studentId = studentId;
        this.accountId = accountId;
        this.type = type;
        this.payloadJson = payloadJson;
        this.baseVersion = baseVersion;
        this.status = status;
        this.timestamp = timestamp;
    }

    @NonNull
    public String getOperationId() { return operationId; }

    @NonNull
    public String getStudentId() { return studentId; }

    @NonNull
    public String getAccountId() { return accountId; }

    @NonNull
    public String getType() { return type; }

    @NonNull
    public String getPayloadJson() { return payloadJson; }

    public int getBaseVersion() { return baseVersion; }

    @NonNull
    public String getStatus() { return status; }
    public void setStatus(@NonNull String status) { this.status = status; }

    public long getTimestamp() { return timestamp; }
}
