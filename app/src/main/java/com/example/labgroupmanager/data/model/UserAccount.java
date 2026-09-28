package com.example.labgroupmanager.data.model;

import com.google.gson.annotations.SerializedName;

public class UserAccount {
    @SerializedName("accountId")
    private String accountId;

    @SerializedName("username")
    private String username;

    @SerializedName("role")
    private String role; // STUDENT or LECTURER

    public UserAccount(String accountId, String username, String role) {
        this.accountId = accountId;
        this.username = username;
        this.role = role;
    }

    public String getAccountId() { return accountId; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
}
