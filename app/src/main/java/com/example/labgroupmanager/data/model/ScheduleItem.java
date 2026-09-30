package com.example.labgroupmanager.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "schedule_items")
public class ScheduleItem {

    @PrimaryKey
    @NonNull
    private String id;

    @NonNull
    private String title;

    @NonNull
    private String time;

    @NonNull
    private String location;

    private boolean isClassTimetable;

    public ScheduleItem(@NonNull String id, @NonNull String title, @NonNull String time, @NonNull String location, boolean isClassTimetable) {
        this.id = id;
        this.title = title;
        this.time = time;
        this.location = location;
        this.isClassTimetable = isClassTimetable;
    }

    @NonNull
    public String getId() { return id; }

    @NonNull
    public String getTitle() { return title; }

    @NonNull
    public String getTime() { return time; }

    @NonNull
    public String getLocation() { return location; }

    public boolean isClassTimetable() { return isClassTimetable; }
}
