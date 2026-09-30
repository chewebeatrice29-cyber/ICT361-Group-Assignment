package com.example.labgroupmanager.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.labgroupmanager.data.model.ScheduleItem;

import java.util.List;

@Dao
public interface ScheduleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertItem(ScheduleItem item);

    @Query("SELECT * FROM schedule_items ORDER BY isClassTimetable DESC, time ASC")
    LiveData<List<ScheduleItem>> getAllScheduleItemsLiveData();

    @Query("SELECT * FROM schedule_items ORDER BY isClassTimetable DESC, time ASC")
    List<ScheduleItem> getAllScheduleItems();

    @Query("DELETE FROM schedule_items WHERE id = :id")
    void deleteItem(String id);
}
