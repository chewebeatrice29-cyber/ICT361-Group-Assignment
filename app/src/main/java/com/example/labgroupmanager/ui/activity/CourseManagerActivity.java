package com.example.labgroupmanager.ui.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.labgroupmanager.R;
import com.example.labgroupmanager.data.local.AppDatabase;
import com.example.labgroupmanager.data.model.Student;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class CourseManagerActivity extends AppCompatActivity {

    private TextInputEditText etTargetStudentNum;
    private CheckBox cbCourseICT361, cbCourseBMG, cbCourseCyber;
    private Button btnAssignCoursesSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_manager);

        etTargetStudentNum = findViewById(R.id.etTargetStudentNum);
        cbCourseICT361 = findViewById(R.id.cbCourseICT361);
        cbCourseBMG = findViewById(R.id.cbCourseBMG);
        cbCourseCyber = findViewById(R.id.cbCourseCyber);
        btnAssignCoursesSubmit = findViewById(R.id.btnAssignCoursesSubmit);

        btnAssignCoursesSubmit.setOnClickListener(v -> {
            String studentNum = etTargetStudentNum.getText() != null ? etTargetStudentNum.getText().toString().trim() : "";
            if (studentNum.isEmpty()) {
                Toast.makeText(this, "Please enter target student number.", Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> selectedCourses = new ArrayList<>();
            if (cbCourseICT361.isChecked()) selectedCourses.add("ICT361");
            if (cbCourseBMG.isChecked()) selectedCourses.add("BMG");
            if (cbCourseCyber.isChecked()) selectedCourses.add("Cyber Security");

            String courseString = String.join(", ", selectedCourses);

            Executors.newSingleThreadExecutor().execute(() -> {
                Student student = AppDatabase.getInstance(getApplicationContext()).studentDao().getStudentByNumber(studentNum);
                if (student != null) {
                    student.setCourses(courseString);
                    AppDatabase.getInstance(getApplicationContext()).studentDao().insertOrUpdate(student);
                    runOnUiThread(() -> {
                        Toast.makeText(CourseManagerActivity.this, "Courses updated for Student " + studentNum + ": " + courseString, Toast.LENGTH_LONG).show();
                        finish();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(CourseManagerActivity.this, "Student " + studentNum + " not found.", Toast.LENGTH_SHORT).show());
                }
            });
        });
    }
}
