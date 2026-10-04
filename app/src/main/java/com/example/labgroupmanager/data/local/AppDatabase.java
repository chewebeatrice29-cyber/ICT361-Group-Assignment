package com.example.labgroupmanager.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.labgroupmanager.data.model.AssignmentItem;
import com.example.labgroupmanager.data.model.MessageItem;
import com.example.labgroupmanager.data.model.ModuleItem;
import com.example.labgroupmanager.data.model.ScheduleItem;
import com.example.labgroupmanager.data.model.Student;
import com.example.labgroupmanager.data.model.SubmissionItem;
import com.example.labgroupmanager.data.model.SyncOperation;
import com.example.labgroupmanager.data.model.TimetableSlot;
import com.example.labgroupmanager.data.model.UserAccount;

@Database(entities = {Student.class, SyncOperation.class, UserAccount.class, ScheduleItem.class, MessageItem.class, ModuleItem.class, AssignmentItem.class, SubmissionItem.class, TimetableSlot.class}, version = 6, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract StudentDao studentDao();
    public abstract SyncOperationDao syncOperationDao();
    public abstract UserAccountDao userAccountDao();
    public abstract ScheduleDao scheduleDao();
    public abstract MessageDao messageDao();
    public abstract ModuleDao moduleDao();
    public abstract AssignmentDao assignmentDao();
    public abstract SubmissionDao submissionDao();
    public abstract TimetableDao timetableDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "lab_group_manager_db"
                    )
                    .fallbackToDestructiveMigration()
                    .build();
                }
            }
        }
        return INSTANCE;
    }
}
