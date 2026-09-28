package com.example.labgroupmanager.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

public class StudentListResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("totalRecords")
    private int totalRecords;

    @SerializedName("page")
    private int page;

    @SerializedName("totalPages")
    private int totalPages;

    @SerializedName("students")
    private List<Student> students;

    @SerializedName("groupCounts")
    private Map<String, Integer> groupCounts;

    public boolean isSuccess() { return success; }
    public int getTotalRecords() { return totalRecords; }
    public int getPage() { return page; }
    public int getTotalPages() { return totalPages; }
    public List<Student> getStudents() { return students; }
    public Map<String, Integer> getGroupCounts() { return groupCounts; }
}
