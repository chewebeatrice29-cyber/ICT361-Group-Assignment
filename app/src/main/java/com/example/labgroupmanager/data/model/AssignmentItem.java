package com.example.labgroupmanager.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "assignments")
public class AssignmentItem {

    @PrimaryKey
    @NonNull
    @SerializedName("id")
    private String id;

    @NonNull
    @SerializedName("courseCode")
    private String courseCode;

    @NonNull
    @SerializedName("title")
    private String title;

    @NonNull
    @SerializedName("instructions")
    private String instructions;

    @NonNull
    @SerializedName("dueDate")
    private String dueDate;

    private int maxMarks;
    private long timestamp;

    public AssignmentItem(@NonNull String id, @NonNull String courseCode, @NonNull String title,
                          @NonNull String instructions, @NonNull String dueDate, int maxMarks, long timestamp) {
        this.id = id;
        this.courseCode = courseCode;
        this.title = title;
        this.instructions = instructions;
        this.dueDate = dueDate;
        this.maxMarks = maxMarks;
        this.timestamp = timestamp;
    }

    @NonNull public String getId() { return id; }
    @NonNull public String getCourseCode() { return courseCode; }
    @NonNull public String getTitle() { return title; }
    @NonNull public String getInstructions() { return instructions; }
    @NonNull public String getDueDate() { return dueDate; }
    public int getMaxMarks() { return maxMarks; }
    public long getTimestamp() { return timestamp; }
}
