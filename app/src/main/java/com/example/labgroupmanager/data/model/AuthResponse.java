package com.example.labgroupmanager.data.model;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("token")
    private String token;

    @SerializedName("account")
    private UserAccount account;

    @SerializedName("student")
    private Student student;

    public AuthResponse() {}

    public AuthResponse(boolean success, String message, String token, UserAccount account, Student student) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.account = account;
        this.student = student;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getToken() { return token; }
    public UserAccount getAccount() { return account; }
    public Student getStudent() { return student; }

    public void setSuccess(boolean success) { this.success = success; }
    public void setMessage(String message) { this.message = message; }
    public void setToken(String token) { this.token = token; }
    public void setAccount(UserAccount account) { this.account = account; }
    public void setStudent(Student student) { this.student = student; }
}
