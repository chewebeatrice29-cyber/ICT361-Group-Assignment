package com.example.labgroupmanager.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SyncBatchRequest {
    @SerializedName("operations")
    private List<SyncOperationPayload> operations;

    public SyncBatchRequest(List<SyncOperationPayload> operations) {
        this.operations = operations;
    }

    public List<SyncOperationPayload> getOperations() { return operations; }

    public static class SyncOperationPayload {
        @SerializedName("operationId")
        private String operationId;

        @SerializedName("studentId")
        private String studentId;

        @SerializedName("type")
        private String type;

        @SerializedName("payload")
        private Object payload;

        @SerializedName("baseVersion")
        private int baseVersion;

        public SyncOperationPayload(String operationId, String studentId, String type, Object payload, int baseVersion) {
            this.operationId = operationId;
            this.studentId = studentId;
            this.type = type;
            this.payload = payload;
            this.baseVersion = baseVersion;
        }

        public String getOperationId() { return operationId; }
        public String getStudentId() { return studentId; }
        public String getType() { return type; }
        public Object getPayload() { return payload; }
        public int getBaseVersion() { return baseVersion; }
    }
}
