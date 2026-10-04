package com.example.labgroupmanager.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.labgroupmanager.data.model.SubmissionItem;

import java.util.List;

@Dao
public interface SubmissionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSubmission(SubmissionItem submission);

    @Query("SELECT * FROM submissions WHERE studentId = :studentId ORDER BY submissionDate DESC")
    LiveData<List<SubmissionItem>> getSubmissionsForStudentLiveData(String studentId);

    @Query("SELECT * FROM submissions ORDER BY submissionDate DESC")
    LiveData<List<SubmissionItem>> getAllSubmissionsLiveData();

    @Query("SELECT * FROM submissions ORDER BY submissionDate DESC")
    List<SubmissionItem> getAllSubmissions();
}
