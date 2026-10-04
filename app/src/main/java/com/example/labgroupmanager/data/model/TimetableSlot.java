package com.example.labgroupmanager.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "timetable_slots")
public class TimetableSlot {

    @PrimaryKey
    @NonNull
    @SerializedName("id")
    private String id; // e.g. MON_1, TUE_2

    @NonNull
    @SerializedName("day")
    private String day; // Monday, Tuesday, Wednesday, Thursday, Friday

    private int slotNumber; // 1 to 5

    @NonNull
    @SerializedName("timeRange")
    private String timeRange; // e.g. "07:00 AM - 09:00 AM"

    @NonNull
    @SerializedName("courseCode")
    private String courseCode;

    @NonNull
    @SerializedName("courseName")
    private String courseName;

    @NonNull
    @SerializedName("venue")
    private String venue;

    @NonNull
    @SerializedName("lecturerName")
    private String lecturerName;

    public TimetableSlot(@NonNull String id, @NonNull String day, int slotNumber, @NonNull String timeRange,
                         @NonNull String courseCode, @NonNull String courseName, @NonNull String venue, @NonNull String lecturerName) {
        this.id = id;
        this.day = day;
        this.slotNumber = slotNumber;
        this.timeRange = timeRange;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.venue = venue;
        this.lecturerName = lecturerName;
    }

    @NonNull public String getId() { return id; }
    @NonNull public String getDay() { return day; }
    public int getSlotNumber() { return slotNumber; }
    @NonNull public String getTimeRange() { return timeRange; }
    @NonNull public String getCourseCode() { return courseCode; }
    @NonNull public String getCourseName() { return courseName; }
    @NonNull public String getVenue() { return venue; }
    @NonNull public String getLecturerName() { return lecturerName; }
}
