package com.example.labgroupmanager.data.remote;

import com.example.labgroupmanager.data.model.ApiResponse;
import com.example.labgroupmanager.data.model.AuthResponse;
import com.example.labgroupmanager.data.model.GroupSummary;
import com.example.labgroupmanager.data.model.Student;
import com.example.labgroupmanager.data.model.StudentListResponse;
import com.example.labgroupmanager.data.model.SyncBatchRequest;
import com.example.labgroupmanager.data.model.SyncBatchResponse;
import com.example.labgroupmanager.data.model.SyncPullResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("auth/register")
    Call<AuthResponse> registerStudent(@Body Map<String, String> body);

    @POST("auth/login")
    Call<AuthResponse> login(@Body Map<String, String> body);

    @GET("auth/me")
    Call<AuthResponse> getCurrentSession();

    @GET("students")
    Call<StudentListResponse> getStudents(
            @Query("search") String search,
            @Query("group") String group,
            @Query("programme") String programme,
            @Query("page") int page,
            @Query("limit") int limit
    );

    @GET("students/summary")
    Call<GroupSummary> getGroupSummary();

    @GET("students/me")
    Call<ApiResponse<Student>> getStudentProfile();

    @POST("students")
    Call<ApiResponse<Student>> createStudent(@Body Map<String, String> body);

    @PUT("students/{id}")
    Call<ApiResponse<Student>> updateStudent(
            @Path("id") String studentId,
            @Body Map<String, Object> body
    );

    @POST("students/{id}/group")
    Call<ApiResponse<Student>> requestGroupChange(
            @Path("id") String studentId,
            @Body Map<String, String> body
    );

    @DELETE("students/{id}")
    Call<ApiResponse<Void>> deleteStudent(@Path("id") String studentId);

    @POST("sync/batch")
    Call<SyncBatchResponse> syncBatchOperations(@Body SyncBatchRequest request);

    @GET("sync/pull")
    Call<SyncPullResponse> pullSyncUpdates(@Query("since") String since);
}
