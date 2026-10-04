package com.example.labgroupmanager.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.labgroupmanager.data.model.AssignmentItem;

import java.util.List;

@Dao
public interface AssignmentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAssignment(AssignmentItem assignment);

    @Query("SELECT * FROM assignments ORDER BY timestamp DESC")
    LiveData<List<AssignmentItem>> getAllAssignmentsLiveData();

    @Query("SELECT * FROM assignments ORDER BY timestamp DESC")
    List<AssignmentItem> getAllAssignments();
}
