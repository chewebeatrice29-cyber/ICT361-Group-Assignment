package com.example.labgroupmanager.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.labgroupmanager.data.model.TimetableSlot;

import java.util.List;

@Dao
public interface TimetableDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSlot(TimetableSlot slot);

    @Query("SELECT * FROM timetable_slots WHERE day = :day ORDER BY slotNumber ASC")
    LiveData<List<TimetableSlot>> getSlotsForDayLiveData(String day);

    @Query("SELECT * FROM timetable_slots ORDER BY slotNumber ASC")
    LiveData<List<TimetableSlot>> getAllSlotsLiveData();

    @Query("SELECT * FROM timetable_slots ORDER BY slotNumber ASC")
    List<TimetableSlot> getAllSlots();
}
