package com.mulungushi.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a student record from the server.
 */
public class Student {

    @SerializedName("user_id")
    private String userId;

    @SerializedName("student_number")
    private String studentNumber;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("email")
    private String email;

    @SerializedName("programme_id")
    private String programmeId;

    @SerializedName("group_id")
    private String groupId;

    @SerializedName("group_label")
    private String groupLabel;

    @SerializedName("role")
    private String role;

    @SerializedName("is_deleted")
    private boolean isDeleted;

    @SerializedName("version")
    private int version;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getProgrammeId() { return programmeId; }
    public void setProgrammeId(String programmeId) { this.programmeId = programmeId; }

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    public String getGroupLabel() { return groupLabel; }
    public void setGroupLabel(String groupLabel) { this.groupLabel = groupLabel; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }

    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
}