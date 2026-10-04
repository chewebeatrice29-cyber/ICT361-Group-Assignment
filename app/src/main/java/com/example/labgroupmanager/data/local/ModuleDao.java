package com.example.labgroupmanager.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.labgroupmanager.data.model.ModuleItem;

import java.util.List;

@Dao
public interface ModuleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModule(ModuleItem module);

    @Query("SELECT * FROM course_modules ORDER BY uploadDate DESC")
    LiveData<List<ModuleItem>> getAllModulesLiveData();

    @Query("SELECT * FROM course_modules ORDER BY uploadDate DESC")
    List<ModuleItem> getAllModules();
}
