package com.example.labgroupmanager.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "course_modules")
public class ModuleItem {

    @PrimaryKey
    @NonNull
    @SerializedName("id")
    private String id;

    @NonNull
    @SerializedName("courseCode")
    private String courseCode;

    @NonNull
    @SerializedName("moduleTitle")
    private String moduleTitle;

    @NonNull
    @SerializedName("description")
    private String description;

    @NonNull
    @SerializedName("fileUrl")
    private String fileUrl;

    private long uploadDate;

    public ModuleItem(@NonNull String id, @NonNull String courseCode, @NonNull String moduleTitle,
                      @NonNull String description, @NonNull String fileUrl, long uploadDate) {
        this.id = id;
        this.courseCode = courseCode;
        this.moduleTitle = moduleTitle;
        this.description = description;
        this.fileUrl = fileUrl;
        this.uploadDate = uploadDate;
    }

    @NonNull public String getId() { return id; }
    @NonNull public String getCourseCode() { return courseCode; }
    @NonNull public String getModuleTitle() { return moduleTitle; }
    @NonNull public String getDescription() { return description; }
    @NonNull public String getFileUrl() { return fileUrl; }
    public long getUploadDate() { return uploadDate; }
}
