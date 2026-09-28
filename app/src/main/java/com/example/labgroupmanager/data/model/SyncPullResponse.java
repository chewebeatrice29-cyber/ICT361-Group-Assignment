package com.example.labgroupmanager.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SyncPullResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("timestamp")
    private String timestamp;

    @SerializedName("students")
    private List<Student> students;

    @SerializedName("deletedStudentIds")
    private List<String> deletedStudentIds;

    public boolean isSuccess() { return success; }
    public String getTimestamp() { return timestamp; }
    public List<Student> getStudents() { return students; }
    public List<String> getDeletedStudentIds() { return deletedStudentIds; }
}
