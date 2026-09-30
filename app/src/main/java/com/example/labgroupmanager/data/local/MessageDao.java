package com.example.labgroupmanager.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.labgroupmanager.data.model.MessageItem;

import java.util.List;

@Dao
public interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMessage(MessageItem message);

    @Query("SELECT * FROM messages WHERE senderId = :userId OR recipientId = :userId OR recipientId = 'ALL' ORDER BY timestamp ASC")
    LiveData<List<MessageItem>> getMessagesForUserLiveData(String userId);

    @Query("SELECT * FROM messages WHERE senderId = :userId OR recipientId = :userId OR recipientId = 'ALL' ORDER BY timestamp ASC")
    List<MessageItem> getMessagesForUser(String userId);
}
