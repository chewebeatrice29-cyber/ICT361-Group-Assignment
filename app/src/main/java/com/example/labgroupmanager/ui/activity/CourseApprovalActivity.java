package com.example.labgroupmanager.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.labgroupmanager.R;
import com.google.android.material.card.MaterialCardView;

public class CourseApprovalActivity extends AppCompatActivity {

    private MaterialCardView cardApprovalItem1, cardApprovalItem2;
    private Button btnApproveCourses1, btnRejectCourses1, btnApproveCourses2, btnRejectCourses2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_approval);

        cardApprovalItem1 = findViewById(R.id.cardApprovalItem1);
        cardApprovalItem2 = findViewById(R.id.cardApprovalItem2);

        btnApproveCourses1 = findViewById(R.id.btnApproveCourses1);
        btnRejectCourses1 = findViewById(R.id.btnRejectCourses1);
        btnApproveCourses2 = findViewById(R.id.btnApproveCourses2);
        btnRejectCourses2 = findViewById(R.id.btnRejectCourses2);

        btnApproveCourses1.setOnClickListener(v -> {
            Toast.makeText(this, "Courses Approved for James Banda (202109428)", Toast.LENGTH_LONG).show();
            cardApprovalItem1.setVisibility(View.GONE);
        });

        btnRejectCourses1.setOnClickListener(v -> {
            Toast.makeText(this, "Course Request Rejected for James Banda", Toast.LENGTH_SHORT).show();
            cardApprovalItem1.setVisibility(View.GONE);
        });

        btnApproveCourses2.setOnClickListener(v -> {
            Toast.makeText(this, "Courses Approved for Mulenga Chanda (202500001)", Toast.LENGTH_LONG).show();
            cardApprovalItem2.setVisibility(View.GONE);
        });

        btnRejectCourses2.setOnClickListener(v -> {
            Toast.makeText(this, "Course Request Rejected for Mulenga Chanda", Toast.LENGTH_SHORT).show();
            cardApprovalItem2.setVisibility(View.GONE);
        });
    }
}
