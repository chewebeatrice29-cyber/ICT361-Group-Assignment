package com.example.labgroupmanager.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.labgroupmanager.data.local.SessionManager;
import com.example.labgroupmanager.data.model.GroupSummary;
import com.example.labgroupmanager.data.model.Student;
import com.example.labgroupmanager.data.remote.ApiClient;
import com.example.labgroupmanager.data.remote.ApiService;
import com.example.labgroupmanager.data.repository.StudentRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudentViewModel extends AndroidViewModel {

    private final StudentRepository repository;
    private final ApiService apiService;
    private final SessionManager sessionManager;

    private final MutableLiveData<FilterState> filterStateLiveData = new MutableLiveData<>(new FilterState("All", "All", ""));
    private final LiveData<List<Student>> filteredStudents;

    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<GroupSummary> groupSummaryLiveData = new MutableLiveData<>();

    public static class FilterState {
        public final String group;
        public final String programme;
        public final String query;

        public FilterState(String group, String programme, String query) {
            this.group = group;
            this.programme = programme;
            this.query = query;
        }
    }

    public StudentViewModel(@NonNull Application application) {
        super(application);
        this.repository = new StudentRepository(application);
        this.apiService = ApiClient.getService(application);
        this.sessionManager = new SessionManager(application);

        this.filteredStudents = Transformations.switchMap(filterStateLiveData, filter ->
                repository.getFilteredStudents(filter.group, filter.programme, filter.query)
        );
    }

    public LiveData<List<Student>> getFilteredStudents() { return filteredStudents; }
    public LiveData<String> getToastMessage() { return toastMessage; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<GroupSummary> getGroupSummaryLiveData() { return groupSummaryLiveData; }

    public LiveData<Student> getOwnStudentProfile() {
        String accountId = sessionManager.getAccountId();
        return repository.getStudentByAccountId(accountId);
    }

    public LiveData<Student> getStudentById(String studentId) {
        return repository.getStudentById(studentId);
    }

    public void updateFilter(String group, String programme, String query) {
        filterStateLiveData.setValue(new FilterState(
                group != null ? group : "All",
                programme != null ? programme : "All",
                query != null ? query : ""
        ));
    }

    public void saveStudentProfile(Student student) {
        repository.saveStudentLocalAndSync(student, new StudentRepository.Callback<Student>() {
            @Override
            public void onSuccess(Student result) {
                toastMessage.setValue("Profile updated successfully!");
            }

            @Override
            public void onError(String message) {
                errorMessage.setValue(message);
            }
        });
    }

    public void createStudentByLecturer(String number, String name, String programme, String group) {
        repository.createStudent(number, name, programme, group, new StudentRepository.Callback<Student>() {
            @Override
            public void onSuccess(Student result) {
                toastMessage.setValue("Student " + name + " added successfully.");
            }

            @Override
            public void onError(String message) {
                errorMessage.setValue(message);
            }
        });
    }

    public void deleteStudent(String studentId) {
        repository.deleteStudent(studentId, new StudentRepository.Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                toastMessage.setValue("Student record deleted.");
            }

            @Override
            public void onError(String message) {
                errorMessage.setValue(message);
            }
        });
    }

    public void requestGroupChange(String studentId, String targetGroup) {
        repository.requestGroupChange(studentId, targetGroup, new StudentRepository.Callback<String>() {
            @Override
            public void onSuccess(String result) {
                toastMessage.setValue("Group change request to " + result + " submitted.");
            }

            @Override
            public void onError(String message) {
                errorMessage.setValue(message);
            }
        });
    }

    public void fetchGroupSummaryForSharesheet() {
        apiService.getGroupSummary().enqueue(new Callback<GroupSummary>() {
            @Override
            public void onResponse(@NonNull Call<GroupSummary> call, @NonNull Response<GroupSummary> response) {
                if (response.isSuccessful() && response.body() != null) {
                    groupSummaryLiveData.setValue(response.body());
                } else {
                    errorMessage.setValue("Unable to fetch group summary for sharing.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<GroupSummary> call, @NonNull Throwable t) {
                errorMessage.setValue("Offline: Unable to load remote group summary.");
            }
        });
    }

    public void manualSync() {
        repository.triggerWorkManagerSync();
        toastMessage.setValue("Sync triggered in background.");
    }
}
