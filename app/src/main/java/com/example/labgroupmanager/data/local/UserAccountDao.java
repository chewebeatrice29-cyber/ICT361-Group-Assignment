package com.example.labgroupmanager.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.labgroupmanager.data.model.UserAccount;

@Dao
public interface UserAccountDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAccount(UserAccount account);

    @Query("SELECT * FROM user_accounts WHERE username = :username LIMIT 1")
    UserAccount getAccountByUsername(String username);

    @Query("SELECT * FROM user_accounts WHERE accountId = :accountId LIMIT 1")
    UserAccount getAccountById(String accountId);
}
