package com.example.labgroupmanager.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.labgroupmanager.data.model.SyncOperation;

import java.util.List;

@Dao
public interface SyncOperationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOperation(SyncOperation operation);

    @Query("SELECT * FROM sync_operations WHERE accountId = :accountId AND status = 'PENDING' ORDER BY timestamp ASC")
    List<SyncOperation> getPendingOperationsForAccount(String accountId);

    @Query("SELECT * FROM sync_operations WHERE accountId = :accountId ORDER BY timestamp ASC")
    LiveData<List<SyncOperation>> getAllOperationsLiveData(String accountId);

    @Query("UPDATE sync_operations SET status = :status WHERE operationId = :operationId")
    void updateOperationStatus(String operationId, String status);

    @Query("DELETE FROM sync_operations WHERE operationId = :operationId")
    void deleteOperation(String operationId);

    @Query("DELETE FROM sync_operations WHERE accountId = :accountId")
    void clearOperationsForAccount(String accountId);
}
