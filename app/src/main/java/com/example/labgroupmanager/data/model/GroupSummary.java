package com.example.labgroupmanager.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class GroupSummary {
    @SerializedName("success")
    private boolean success;

    @SerializedName("groupCounts")
    private Map<String, Integer> groupCounts;

    @SerializedName("totalActiveStudents")
    private int totalActiveStudents;

    @SerializedName("maxCapacityPerGroup")
    private int maxCapacityPerGroup;

    public boolean isSuccess() { return success; }
    public Map<String, Integer> getGroupCounts() { return groupCounts; }
    public int getTotalActiveStudents() { return totalActiveStudents; }
    public int getMaxCapacityPerGroup() { return maxCapacityPerGroup; }
}
