package com.example.labgroupmanager.worker;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.labgroupmanager.R;
import com.example.labgroupmanager.data.local.AppDatabase;
import com.example.labgroupmanager.data.model.AssignmentItem;
import com.example.labgroupmanager.data.model.ScheduleItem;
import com.example.labgroupmanager.ui.activity.StudentHomeActivity;

import java.util.List;

public class NotificationWorker extends Worker {

    public static final String CHANNEL_ID = "channel_due_reminders";
    public static final String CHANNEL_NAME = "Due Activity & Assignment Pop-up Reminders";

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        createNotificationChannel();

        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        List<ScheduleItem> scheduleItems = db.scheduleDao().getAllScheduleItems();
        List<AssignmentItem> assignments = db.assignmentDao().getAllAssignments();

        if (!scheduleItems.isEmpty()) {
            ScheduleItem item = scheduleItems.get(0);
            showPopUpNotification(
                    "⏰ Upcoming Scheduled Activity Due!",
                    "Reminder: " + item.getTitle() + " (" + item.getTime() + ") at " + item.getLocation() + "."
            );
        }

        if (!assignments.isEmpty()) {
            AssignmentItem assignment = assignments.get(0);
            showPopUpNotification(
                    "🚨 Assignment Deadline Approaching!",
                    "Due Soon: " + assignment.getTitle() + " (" + assignment.getCourseCode() + ") Deadline: " + assignment.getDueDate() + "."
            );
        }

        return Result.success();
    }

    @SuppressLint("MissingPermission")
    private void showPopUpNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(getApplicationContext(), StudentHomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                (int) System.currentTimeMillis(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        if (notificationManager != null) {
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("High priority pop-up alerts for scheduled class activities and assignment deadlines.");
            channel.enableVibration(true);
            channel.enableLights(true);

            NotificationManager manager = getApplicationContext().getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
