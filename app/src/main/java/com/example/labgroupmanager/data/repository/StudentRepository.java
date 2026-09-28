package com.example.labgroupmanager.data.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.labgroupmanager.data.local.AppDatabase;
import com.example.labgroupmanager.data.local.SessionManager;
import com.example.labgroupmanager.data.local.StudentDao;
import com.example.labgroupmanager.data.local.SyncOperationDao;
import com.example.labgroupmanager.data.model.ApiResponse;
import com.example.labgroupmanager.data.model.Student;
import com.example.labgroupmanager.data.model.SyncOperation;
import com.example.labgroupmanager.data.remote.ApiClient;
import com.example.labgroupmanager.data.remote.ApiService;
import com.example.labgroupmanager.worker.SyncWorker;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StudentRepository {

    private final StudentDao studentDao;
    private final SyncOperationDao syncOperationDao;
    private final ApiService apiService;
    private final SessionManager sessionManager;
    private final ExecutorService executor;
    private final Handler mainHandler;
    private final Context context;
    private final Gson gson;

    public interface Callback<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    public StudentRepository(Context context) {
        this.context = context.getApplicationContext();
        AppDatabase db = AppDatabase.getInstance(this.context);
        this.studentDao = db.studentDao();
        this.syncOperationDao = db.syncOperationDao();
        this.apiService = ApiClient.getService(this.context);
        this.sessionManager = new SessionManager(this.context);
        this.executor = Executors.newFixedThreadPool(4);
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.gson = new Gson();
    }

    public LiveData<List<Student>> getFilteredStudents(String group, String programme, String query) {
        return studentDao.getFilteredStudentsLiveData(group, programme, query);
    }

    public LiveData<Student> getStudentByAccountId(String accountId) {
        return studentDao.getStudentByAccountIdLiveData(accountId);
    }

    public LiveData<Student> getStudentById(String studentId) {
        return studentDao.getStudentByIdLiveData(studentId);
    }

    // Queue operation locally and schedule WorkManager
    private void queueOperation(String studentId, String type, Object payload, int baseVersion) {
        String operationId = UUID.randomUUID().toString();
        String accountId = sessionManager.getAccountId();
        if (accountId == null) accountId = "ANONYMOUS";

        String payloadJson = gson.toJson(payload);
        SyncOperation op = new SyncOperation(
                operationId, studentId, accountId, type, payloadJson, baseVersion, "PENDING", System.currentTimeMillis()
        );

        executor.execute(() -> {
            syncOperationDao.insertOperation(op);
            triggerWorkManagerSync();
        });
    }

    public void triggerWorkManagerSync() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest syncRequest = new OneTimeWorkRequest.Builder(SyncWorker.class)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(context).enqueue(syncRequest);
    }

    // Save Student Profile / Edit locally with early local validation
    public void saveStudentLocalAndSync(Student student, Callback<Student> callback) {
        executor.execute(() -> {
            // Early local capacity check if assigning to lab group
            if (!"Unassigned".equals(student.getLabGroup())) {
                int currentCount = studentDao.getGroupMemberCount(student.getLabGroup());
                // If it's a new student or transferred student
                Student currentInDb = studentDao.getStudentById(student.getStudentId());
                boolean isNewInGroup = currentInDb == null || !student.getLabGroup().equals(currentInDb.getLabGroup());
                if (isNewInGroup && currentCount >= 15) {
                    mainHandler.post(() -> callback.onError("Group " + student.getLabGroup() + " is full (15/15). Selection reverted to Unassigned."));
                    student.setLabGroup(currentInDb != null ? currentInDb.getLabGroup() : "Unassigned");
                }
            }

            student.setSyncStatus("PENDING");
            studentDao.insertOrUpdate(student);

            Map<String, String> payload = new HashMap<>();
            payload.put("studentNumber", student.getStudentNumber());
            payload.put("studentName", student.getStudentName());
            payload.put("programme", student.getProgramme());
            payload.put("labGroup", student.getLabGroup());

            queueOperation(student.getStudentId(), "UPDATE_STUDENT", payload, student.getVersion());

            mainHandler.post(() -> callback.onSuccess(student));
        });
    }

    // Lecturer Create Student
    public void createStudent(String number, String name, String programme, String group, Callback<Student> callback) {
        executor.execute(() -> {
            // Check duplicate number locally first
            Student existing = studentDao.getStudentByNumber(number);
            if (existing != null) {
                mainHandler.post(() -> callback.onError("Student number " + number + " already exists in local database."));
                return;
            }

            // Local 15 capacity check
            String finalGroup = group;
            if (!"Unassigned".equals(group)) {
                int count = studentDao.getGroupMemberCount(group);
                if (count >= 15) {
                    mainHandler.post(() -> callback.onError("Lab group " + group + " is full (15/15). Student created as Unassigned."));
                    finalGroup = "Unassigned";
                }
            }

            String studentId = UUID.randomUUID().toString();
            Student newStudent = new Student(studentId, number, name, programme, finalGroup, null, 1, false, "PENDING");
            studentDao.insertOrUpdate(newStudent);

            Map<String, String> payload = new HashMap<>();
            payload.put("studentNumber", number);
            payload.put("studentName", name);
            payload.put("programme", programme);
            payload.put("labGroup", finalGroup);

            queueOperation(studentId, "CREATE_STUDENT", payload, 1);

            mainHandler.post(() -> callback.onSuccess(newStudent));
        });
    }

    // Lecturer Soft Delete Student
    public void deleteStudent(String studentId, Callback<Void> callback) {
        executor.execute(() -> {
            studentDao.markSoftDeleted(studentId, "PENDING");
            queueOperation(studentId, "DELETE_STUDENT", new HashMap<>(), 0);
            mainHandler.post(() -> callback.onSuccess(null));
        });
    }

    // Student Group Transfer Request
    public void requestGroupChange(String studentId, String targetGroup, Callback<String> callback) {
        executor.execute(() -> {
            Student current = studentDao.getStudentById(studentId);
            if (current == null) {
                mainHandler.post(() -> callback.onError("Student profile not found."));
                return;
            }

            if (!"Unassigned".equals(targetGroup)) {
                int count = studentDao.getGroupMemberCount(targetGroup);
                if (count >= 15) {
                    mainHandler.post(() -> callback.onError("GROUP_FULL: Target group " + targetGroup + " is at full capacity (15/15). Transfer cancelled."));
                    return;
                }
            }

            current.setLabGroup(targetGroup);
            current.setSyncStatus("PENDING");
            studentDao.insertOrUpdate(current);

            Map<String, String> payload = new HashMap<>();
            payload.put("targetGroup", targetGroup);

            queueOperation(studentId, "GROUP_CHANGE", payload, current.getVersion());

            mainHandler.post(() -> callback.onSuccess(targetGroup));
        });
    }
}
