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

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getToken() { return token; }
    public UserAccount getAccount() { return account; }
    public Student getStudent() { return student; }
}
