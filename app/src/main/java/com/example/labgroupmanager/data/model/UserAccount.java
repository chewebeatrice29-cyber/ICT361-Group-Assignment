package com.example.labgroupmanager.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "user_accounts")
public class UserAccount {

    @PrimaryKey
    @NonNull
    @SerializedName("accountId")
    private String accountId;

    @NonNull
    @SerializedName("username")
    private String username;

    @NonNull
    @SerializedName("role")
    private String role; // STUDENT or LECTURER

    private String passwordHash;

    public UserAccount(@NonNull String accountId, @NonNull String username, @NonNull String role, String passwordHash) {
        this.accountId = accountId;
        this.username = username;
        this.role = role;
        this.passwordHash = passwordHash;
    }

    @NonNull
    public String getAccountId() { return accountId; }
    public void setAccountId(@NonNull String accountId) { this.accountId = accountId; }

    @NonNull
    public String getUsername() { return username; }
    public void setUsername(@NonNull String username) { this.username = username; }

    @NonNull
    public String getRole() { return role; }
    public void setRole(@NonNull String role) { this.role = role; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}
