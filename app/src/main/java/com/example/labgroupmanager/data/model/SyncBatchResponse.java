package com.example.labgroupmanager.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SyncBatchResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("processedCount")
    private int processedCount;

    @SerializedName("results")
    private List<OperationResult> results;

    public boolean isSuccess() { return success; }
    public int getProcessedCount() { return processedCount; }
    public List<OperationResult> getResults() { return results; }

    public static class OperationResult {
        @SerializedName("operationId")
        private String operationId;

        @SerializedName("status")
        private String status; // SYNCED, FAILED, CONFLICT

        @SerializedName("message")
        private String message;

        public String getOperationId() { return operationId; }
        public String getStatus() { return status; }
        public String getMessage() { return message; }
    }
}
