package com.example.labgroupmanager.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.labgroupmanager.R;
import com.example.labgroupmanager.data.model.Student;
import com.example.labgroupmanager.ui.viewmodel.StudentViewModel;

import java.util.ArrayList;
import java.util.List;

public class AvailableCoursesActivity extends AppCompatActivity {

    private CheckBox cbAvailICT361, cbAvailCS311, cbAvailCS321, cbAvailCS331, cbAvailIT311, cbAvailIT321, cbAvailDS311, cbAvailDS321, cbAvailBMG;
    private TextView tvCourseApprovalStatus;
    private Button btnSubmitCourseRequest;

    private StudentViewModel studentViewModel;
    private Student currentStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_courses);

        cbAvailICT361 = findViewById(R.id.cbAvailICT361);
        cbAvailCS311 = findViewById(R.id.cbAvailCS311);
        cbAvailCS321 = findViewById(R.id.cbAvailCS321);
        cbAvailCS331 = findViewById(R.id.cbAvailCS331);
        cbAvailIT311 = findViewById(R.id.cbAvailIT311);
        cbAvailIT321 = findViewById(R.id.cbAvailIT321);
        cbAvailDS311 = findViewById(R.id.cbAvailDS311);
        cbAvailDS321 = findViewById(R.id.cbAvailDS321);
        cbAvailBMG = findViewById(R.id.cbAvailBMG);

        tvCourseApprovalStatus = findViewById(R.id.tvCourseApprovalStatus);
        btnSubmitCourseRequest = findViewById(R.id.btnSubmitCourseRequest);

        studentViewModel = new ViewModelProvider(this).get(StudentViewModel.class);

        studentViewModel.getOwnStudentProfile().observe(this, student -> {
            if (student != null) {
                currentStudent = student;
                String courses = student.getCourses();
                if (courses != null) {
                    cbAvailICT361.setChecked(courses.contains("ICT361"));
                    cbAvailCS311.setChecked(courses.contains("CS311"));
                    cbAvailCS321.setChecked(courses.contains("CS321"));
                    cbAvailCS331.setChecked(courses.contains("CS331"));
                    cbAvailIT311.setChecked(courses.contains("IT311"));
                    cbAvailIT321.setChecked(courses.contains("IT321"));
                    cbAvailDS311.setChecked(courses.contains("DS311"));
                    cbAvailDS321.setChecked(courses.contains("DS321"));
                    cbAvailBMG.setChecked(courses.contains("BMG"));
                }
            }
        });

        btnSubmitCourseRequest.setOnClickListener(v -> {
            if (currentStudent != null) {
                List<String> selected = new ArrayList<>();
                if (cbAvailICT361.isChecked()) selected.add("ICT361");
                if (cbAvailCS311.isChecked()) selected.add("CS311");
                if (cbAvailCS321.isChecked()) selected.add("CS321");
                if (cbAvailCS331.isChecked()) selected.add("CS331");
                if (cbAvailIT311.isChecked()) selected.add("IT311");
                if (cbAvailIT321.isChecked()) selected.add("IT321");
                if (cbAvailDS311.isChecked()) selected.add("DS311");
                if (cbAvailDS321.isChecked()) selected.add("DS321");
                if (cbAvailBMG.isChecked()) selected.add("BMG");

                String courseString = String.join(", ", selected);
                currentStudent.setCourses(courseString);
                studentViewModel.saveStudentProfile(currentStudent);

                tvCourseApprovalStatus.setText("Status: Pending Lecturer Approval (" + selected.size() + " courses requested)");
                tvCourseApprovalStatus.setTextColor(Color.parseColor("#E65100"));

                Toast.makeText(this, "Course registration selection submitted for lecturer approval!", Toast.LENGTH_LONG).show();
            }
        });
    }
}
