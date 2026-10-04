package com.example.labgroupmanager.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "submissions")
public class SubmissionItem {

    @PrimaryKey
    @NonNull
    @SerializedName("id")
    private String id;

    @NonNull
    @SerializedName("assignmentId")
    private String assignmentId;

    @NonNull
    @SerializedName("studentId")
    private String studentId;

    @NonNull
    @SerializedName("studentNumber")
    private String studentNumber;

    @NonNull
    @SerializedName("studentName")
    private String studentName;

    @NonNull
    @SerializedName("submissionText")
    private String submissionText;

    @NonNull
    @SerializedName("submissionLink")
    private String submissionLink;

    private long submissionDate;

    @NonNull
    @SerializedName("status")
    private String status; // SUBMITTED, GRADED

    private int gradeScore;

    @SerializedName("feedback")
    private String feedback;

    public SubmissionItem(@NonNull String id, @NonNull String assignmentId, @NonNull String studentId,
                          @NonNull String studentNumber, @NonNull String studentName, @NonNull String submissionText,
                          @NonNull String submissionLink, long submissionDate, @NonNull String status,
                          int gradeScore, String feedback) {
        this.id = id;
        this.assignmentId = assignmentId;
        this.studentId = studentId;
        this.studentNumber = studentNumber;
        this.studentName = studentName;
        this.submissionText = submissionText;
        this.submissionLink = submissionLink;
        this.submissionDate = submissionDate;
        this.status = status;
        this.gradeScore = gradeScore;
        this.feedback = feedback != null ? feedback : "";
    }

    @NonNull public String getId() { return id; }
    @NonNull public String getAssignmentId() { return assignmentId; }
    @NonNull public String getStudentId() { return studentId; }
    @NonNull public String getStudentNumber() { return studentNumber; }
    @NonNull public String getStudentName() { return studentName; }
    @NonNull public String getSubmissionText() { return submissionText; }
    @NonNull public String getSubmissionLink() { return submissionLink; }
    public long getSubmissionDate() { return submissionDate; }
    @NonNull public String getStatus() { return status; }
    public void setStatus(@NonNull String status) { this.status = status; }
    public int getGradeScore() { return gradeScore; }
    public void setGradeScore(int gradeScore) { this.gradeScore = gradeScore; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
}
