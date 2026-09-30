package com.example.labgroupmanager.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "students")
public class Student {

    @PrimaryKey
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
    @SerializedName("programme")
    private String programme; // CS, IT, DS

    @NonNull
    @SerializedName("labGroup")
    private String labGroup; // G01, G02, G03, G04, Unassigned

    @SerializedName("courses")
    private String courses; // Enrolled courses e.g. "ICT361, BMG, Cyber Security"

    @SerializedName("accountId")
    private String accountId;

    @SerializedName("version")
    private int version;

    @SerializedName("isDeleted")
    private boolean isDeleted;

    @NonNull
    private String syncStatus; // SAVED_LOCALLY, PENDING, SYNCING, SYNCED, CONFLICT

    public Student(@NonNull String studentId, @NonNull String studentNumber, @NonNull String studentName,
                   @NonNull String programme, @NonNull String labGroup, String courses, String accountId,
                   int version, boolean isDeleted, @NonNull String syncStatus) {
        this.studentId = studentId;
        this.studentNumber = studentNumber;
        this.studentName = studentName;
        this.programme = programme;
        this.labGroup = labGroup;
        this.courses = courses != null ? courses : "ICT361, BMG, Cyber Security";
        this.accountId = accountId;
        this.version = version;
        this.isDeleted = isDeleted;
        this.syncStatus = syncStatus;
    }

    @Ignore
    public Student(@NonNull String studentId, @NonNull String studentNumber, @NonNull String studentName,
                   @NonNull String programme, @NonNull String labGroup, String accountId,
                   int version, boolean isDeleted, @NonNull String syncStatus) {
        this(studentId, studentNumber, studentName, programme, labGroup, "ICT361, BMG, Cyber Security", accountId, version, isDeleted, syncStatus);
    }

    @NonNull
    public String getStudentId() { return studentId; }
    public void setStudentId(@NonNull String studentId) { this.studentId = studentId; }

    @NonNull
    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(@NonNull String studentNumber) { this.studentNumber = studentNumber; }

    @NonNull
    public String getStudentName() { return studentName; }
    public void setStudentName(@NonNull String studentName) { this.studentName = studentName; }

    @NonNull
    public String getProgramme() { return programme; }
    public void setProgramme(@NonNull String programme) { this.programme = programme; }

    @NonNull
    public String getLabGroup() { return labGroup; }
    public void setLabGroup(@NonNull String labGroup) { this.labGroup = labGroup; }

    public String getCourses() { return courses != null ? courses : "ICT361, BMG, Cyber Security"; }
    public void setCourses(String courses) { this.courses = courses; }

    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }

    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }

    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }

    @NonNull
    public String getSyncStatus() { return syncStatus; }
    public void setSyncStatus(@NonNull String syncStatus) { this.syncStatus = syncStatus; }
}
