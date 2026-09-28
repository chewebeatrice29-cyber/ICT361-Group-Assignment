package com.example.labgroupmanager.data.model;

import com.google.gson.annotations.SerializedName;

public class ApiResponse<T> {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("code")
    private String code;

    @SerializedName("student")
    private Student student;

    @SerializedName("retainedGroup")
    private String retainedGroup;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getCode() { return code; }
    public Student getStudent() { return student; }
    public String getRetainedGroup() { return retainedGroup; }
}
