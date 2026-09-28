package com.example.labgroupmanager.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.labgroupmanager.R;
import com.example.labgroupmanager.data.local.SessionManager;
import com.example.labgroupmanager.data.model.GroupSummary;
import com.example.labgroupmanager.data.model.Student;
import com.example.labgroupmanager.ui.adapter.StudentAdapter;
import com.example.labgroupmanager.ui.viewmodel.StudentViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Map;

public class LecturerRosterActivity extends AppCompatActivity {

    private TextInputEditText etSearchQuery;
    private Spinner spinnerFilterProgramme, spinnerFilterGroup;
    private TextView tvGroupCountsSummary;
    private RecyclerView rvStudentRoster;
    private SwipeRefreshLayout swipeRefresh;
    private FloatingActionButton fabAddStudent;
    private Button btnShareSummary, btnSyncNow;

    private StudentViewModel studentViewModel;
    private StudentAdapter studentAdapter;
    private SessionManager sessionManager;

    private String selectedProg = "All";
    private String selectedGroup = "All";
    private String currentQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer_roster);

        sessionManager = new SessionManager(this);

        etSearchQuery = findViewById(R.id.etSearchQuery);
        spinnerFilterProgramme = findViewById(R.id.spinnerFilterProgramme);
        spinnerFilterGroup = findViewById(R.id.spinnerFilterGroup);
        tvGroupCountsSummary = findViewById(R.id.tvGroupCountsSummary);
        rvStudentRoster = findViewById(R.id.rvStudentRoster);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        fabAddStudent = findViewById(R.id.fabAddStudent);
        btnShareSummary = findViewById(R.id.btnShareSummary);
        btnSyncNow = findViewById(R.id.btnSyncNow);

        ArrayAdapter<String> progAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"All", "CS", "IT", "DS"});
        progAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterProgramme.setAdapter(progAdapter);

        ArrayAdapter<String> groupAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"All", "G01", "G02", "G03", "G04", "Unassigned"});
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterGroup.setAdapter(groupAdapter);

        rvStudentRoster.setLayoutManager(new LinearLayoutManager(this));
        studentAdapter = new StudentAdapter(student -> {
            Intent intent = new Intent(LecturerRosterActivity.this, StudentEditorActivity.class);
            intent.putExtra("STUDENT_ID", student.getStudentId());
            startActivity(intent);
        });
        rvStudentRoster.setAdapter(studentAdapter);

        studentViewModel = new ViewModelProvider(this).get(StudentViewModel.class);

        studentViewModel.getFilteredStudents().observe(this, students -> {
            swipeRefresh.setRefreshing(false);
            studentAdapter.submitList(students);
            updateGroupCountsDisplay(students);
        });

        studentViewModel.getToastMessage().observe(this, msg -> {
            if (msg != null) Toast.makeText(LecturerRosterActivity.this, msg, Toast.LENGTH_SHORT).show();
        });

        studentViewModel.getGroupSummaryLiveData().observe(this, summary -> {
            if (summary != null) {
                shareGroupSummarySharesheet(summary);
            }
        });

        // Search & Filter listeners
        etSearchQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentQuery = s.toString();
                studentViewModel.updateFilter(selectedGroup, selectedProg, currentQuery);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        spinnerFilterProgramme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedProg = parent.getItemAtPosition(position).toString();
                studentViewModel.updateFilter(selectedGroup, selectedProg, currentQuery);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerFilterGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGroup = parent.getItemAtPosition(position).toString();
                studentViewModel.updateFilter(selectedGroup, selectedProg, currentQuery);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        swipeRefresh.setOnRefreshListener(() -> {
            studentViewModel.manualSync();
            studentViewModel.updateFilter(selectedGroup, selectedProg, currentQuery);
        });

        fabAddStudent.setOnClickListener(v -> {
            startActivity(new Intent(LecturerRosterActivity.this, StudentEditorActivity.class));
        });

        btnShareSummary.setOnClickListener(v -> {
            studentViewModel.fetchGroupSummaryForSharesheet();
        });

        btnSyncNow.setOnClickListener(v -> {
            studentViewModel.manualSync();
        });
    }

    private void updateGroupCountsDisplay(java.util.List<Student> students) {
        int g1 = 0, g2 = 0, g3 = 0, g4 = 0, unassigned = 0;
        for (Student s : students) {
            switch (s.getLabGroup()) {
                case "G01": g1++; break;
                case "G02": g2++; break;
                case "G03": g3++; break;
                case "G04": g4++; break;
                default: unassigned++; break;
            }
        }
        tvGroupCountsSummary.setText(String.format("Group Totals: G01: %d/15 | G02: %d/15 | G03: %d/15 | G04: %d/15 | Unassigned: %d", g1, g2, g3, g4, unassigned));
    }

    // Android Sharesheet integration for lecturer group summary export (Unit 7 requirement)
    private void shareGroupSummarySharesheet(GroupSummary summary) {
        StringBuilder sb = new StringBuilder();
        sb.append("ICT361 Lab Group Summary Report\n");
        sb.append("===================================\n");
        if (summary.getGroupCounts() != null) {
            for (Map.Entry<String, Integer> entry : summary.getGroupCounts().entrySet()) {
                sb.append(entry.getKey()).append(": ").append(entry.getValue());
                if (!"Unassigned".equals(entry.getKey())) {
                    sb.append("/15 members");
                }
                sb.append("\n");
            }
        }
        sb.append("===================================\n");
        sb.append("Total Active Students: ").append(summary.getTotalActiveStudents());

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "ICT361 Lab Group Counts Summary");
        shareIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());

        startActivity(Intent.createChooser(shareIntent, "Share Group Summary Report"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            sessionManager.clearSession();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
