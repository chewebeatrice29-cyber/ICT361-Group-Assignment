package com.example.labgroupmanager.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.labgroupmanager.data.local.AppDatabase;
import com.example.labgroupmanager.data.local.SessionManager;
import com.example.labgroupmanager.data.local.StudentDao;
import com.example.labgroupmanager.data.local.SyncOperationDao;
import com.example.labgroupmanager.data.model.Student;
import com.example.labgroupmanager.data.model.SyncBatchRequest;
import com.example.labgroupmanager.data.model.SyncBatchResponse;
import com.example.labgroupmanager.data.model.SyncOperation;
import com.example.labgroupmanager.data.model.SyncPullResponse;
import com.example.labgroupmanager.data.remote.ApiClient;
import com.example.labgroupmanager.data.remote.ApiService;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class SyncWorker extends Worker {

    private static final String TAG = "SyncWorker";

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        SessionManager sessionManager = new SessionManager(context);
        String accountId = sessionManager.getAccountId();

        if (accountId == null || !sessionManager.isLoggedIn()) {
            Log.d(TAG, "No active logged in account. Sync worker exiting.");
            return Result.success();
        }

        AppDatabase db = AppDatabase.getInstance(context);
        SyncOperationDao syncOperationDao = db.syncOperationDao();
        StudentDao studentDao = db.studentDao();
        ApiService apiService = ApiClient.getService(context);
        Gson gson = new Gson();

        List<SyncOperation> pendingOperations = syncOperationDao.getPendingOperationsForAccount(accountId);

        // 1. Process Pending Operations Queue
        if (!pendingOperations.isEmpty()) {
            List<SyncBatchRequest.SyncOperationPayload> payloads = new ArrayList<>();
            for (SyncOperation op : pendingOperations) {
                Object payloadObj = gson.fromJson(op.getPayloadJson(), Object.class);
                payloads.add(new SyncBatchRequest.SyncOperationPayload(
                        op.getOperationId(), op.getStudentId(), op.getType(), payloadObj, op.getBaseVersion()
                ));
            }

            try {
                Response<SyncBatchResponse> batchRes = apiService.syncBatchOperations(new SyncBatchRequest(payloads)).execute();
                if (batchRes.isSuccessful() && batchRes.body() != null && batchRes.body().isSuccess()) {
                    List<SyncBatchResponse.OperationResult> results = batchRes.body().getResults();
                    for (SyncBatchResponse.OperationResult res : results) {
                        if ("SYNCED".equalsIgnoreCase(res.getStatus())) {
                            syncOperationDao.deleteOperation(res.getOperationId());
                        } else if ("CONFLICT".equalsIgnoreCase(res.getStatus())) {
                            syncOperationDao.updateOperationStatus(res.getOperationId(), "CONFLICT");
                        } else {
                            syncOperationDao.updateOperationStatus(res.getOperationId(), "FAILED");
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Sync Push Error: " + e.getMessage());
                return Result.retry();
            }
        }

        // 2. Pull Remote Server Updates and Tombstones
        try {
            String lastSync = sessionManager.getLastSyncTime();
            Response<SyncPullResponse> pullRes = apiService.pullSyncUpdates(lastSync).execute();

            if (pullRes.isSuccessful() && pullRes.body() != null && pullRes.body().isSuccess()) {
                SyncPullResponse body = pullRes.body();

                // Save or update active students from server
                if (body.getStudents() != null) {
                    for (Student s : body.getStudents()) {
                        s.setSyncStatus("SYNCED");
                        studentDao.insertOrUpdate(s);
                    }
                }

                // Process Tombstones (soft deleted students)
                if (body.getDeletedStudentIds() != null) {
                    for (String deletedId : body.getDeletedStudentIds()) {
                        studentDao.markSoftDeleted(deletedId, "SYNCED");
                    }
                }

                sessionManager.setLastSyncTime(body.getTimestamp());
            }
        } catch (Exception e) {
            Log.e(TAG, "Sync Pull Error: " + e.getMessage());
            return Result.retry();
        }

        return Result.success();
    }
}
