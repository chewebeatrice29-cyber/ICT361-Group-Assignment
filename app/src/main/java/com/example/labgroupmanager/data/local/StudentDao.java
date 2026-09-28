package com.example.labgroupmanager.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.labgroupmanager.data.model.Student;

import java.util.List;

@Dao
public interface StudentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(Student student);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdateAll(List<Student> students);

    @Query("SELECT * FROM students WHERE studentId = :studentId AND isDeleted = 0")
    LiveData<Student> getStudentByIdLiveData(String studentId);

    @Query("SELECT * FROM students WHERE studentId = :studentId AND isDeleted = 0")
    Student getStudentById(String studentId);

    @Query("SELECT * FROM students WHERE accountId = :accountId AND isDeleted = 0 LIMIT 1")
    LiveData<Student> getStudentByAccountIdLiveData(String accountId);

    @Query("SELECT * FROM students WHERE accountId = :accountId AND isDeleted = 0 LIMIT 1")
    Student getStudentByAccountId(String accountId);

    @Query("SELECT * FROM students WHERE studentNumber = :studentNumber AND isDeleted = 0 LIMIT 1")
    Student getStudentByNumber(String studentNumber);

    // Active roster filter query
    @Query("SELECT * FROM students WHERE isDeleted = 0 AND " +
           "(:group = 'All' OR labGroup = :group) AND " +
           "(:programme = 'All' OR programme = :programme) AND " +
           "(:query = '' OR studentName LIKE '%' || :query || '%' OR studentNumber LIKE '%' || :query || '%') " +
           "ORDER BY studentName ASC")
    LiveData<List<Student>> getFilteredStudentsLiveData(String group, String programme, String query);

    @Query("SELECT COUNT(*) FROM students WHERE labGroup = :group AND isDeleted = 0")
    int getGroupMemberCount(String group);

    @Query("SELECT COUNT(*) FROM students WHERE labGroup = :group AND isDeleted = 0")
    LiveData<Integer> getGroupMemberCountLiveData(String group);

    @Query("UPDATE students SET isDeleted = 1, labGroup = 'Unassigned', syncStatus = :syncStatus WHERE studentId = :studentId")
    void markSoftDeleted(String studentId, String syncStatus);

    @Query("DELETE FROM students WHERE studentId = :studentId")
    void deletePermanently(String studentId);

    @Query("DELETE FROM students")
    void clearAll();
}
