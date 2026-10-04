package com.example.labgroupmanager.ui.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.labgroupmanager.R;
import com.example.labgroupmanager.data.local.SessionManager;
import com.example.labgroupmanager.data.model.Student;
import com.example.labgroupmanager.ui.BottomNavHelper;
import com.example.labgroupmanager.ui.viewmodel.StudentViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

public class StudentProfileActivity extends AppCompatActivity {

    private TextView tvStudentGreeting, tvStudentNumberDisplay, tvProgrammeDisplay, tvDetailStudentNumber, tvDetailGroup, tvDetailSyncStatus, tvOfflineBanner;
    private Button btnViewProfileTimetable, btnEditProfileTop;
    private ImageView ivProfileAvatar;
    private BottomNavigationView bottomNavProfile;

    private StudentViewModel studentViewModel;
    private SessionManager sessionManager;
    private Student currentStudent;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        sessionManager = new SessionManager(this);

        tvStudentGreeting = findViewById(R.id.tvStudentGreeting);
        tvStudentNumberDisplay = findViewById(R.id.tvStudentNumberDisplay);
        tvProgrammeDisplay = findViewById(R.id.tvProgrammeDisplay);
        tvDetailStudentNumber = findViewById(R.id.tvDetailStudentNumber);
        tvDetailGroup = findViewById(R.id.tvDetailGroup);
        tvDetailSyncStatus = findViewById(R.id.tvDetailSyncStatus);
        tvOfflineBanner = findViewById(R.id.tvOfflineBanner);
        btnViewProfileTimetable = findViewById(R.id.btnViewProfileTimetable);
        btnEditProfileTop = findViewById(R.id.btnEditProfileTop);
        ivProfileAvatar = findViewById(R.id.ivProfileAvatar);
        View btnChangeProfilePhoto = findViewById(R.id.btnChangeProfilePhoto);
        bottomNavProfile = findViewById(R.id.bottomNavProfile);

        // Load saved profile picture if available
        String savedUri = sessionManager.getProfileImageUri();
        if (savedUri != null && ivProfileAvatar != null) {
            try {
                ivProfileAvatar.setImageURI(Uri.parse(savedUri));
            } catch (Exception ignored) {}
        }

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            try {
                                getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            } catch (Exception ignored) {}
                            sessionManager.setProfileImageUri(uri.toString());
                            if (ivProfileAvatar != null) {
                                ivProfileAvatar.setImageURI(uri);
                            }
                            Toast.makeText(this, "Profile picture updated successfully!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        if (btnChangeProfilePhoto != null) {
            btnChangeProfilePhoto.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                imagePickerLauncher.launch(intent);
            });
        }

        studentViewModel = new ViewModelProvider(this).get(StudentViewModel.class);

        studentViewModel.getOwnStudentProfile().observe(this, student -> {
            if (student != null) {
                currentStudent = student;
                tvStudentGreeting.setText(student.getStudentName());
                tvStudentNumberDisplay.setText("336 friends • 128 posts");
                tvProgrammeDisplay.setText("📍 Lusaka  •  " + student.getProgramme() + " Student");
                if (tvDetailStudentNumber != null) tvDetailStudentNumber.setText("Student # " + student.getStudentNumber() + " • " + student.getProgramme());
                if (tvDetailGroup != null) tvDetailGroup.setText("Lab Group " + student.getLabGroup() + " (12 / 15 members)");
                if (tvDetailSyncStatus != null) tvDetailSyncStatus.setText("Sync Status: " + student.getSyncStatus());

                if ("SAVED_LOCALLY".equalsIgnoreCase(student.getSyncStatus()) || "PENDING".equalsIgnoreCase(student.getSyncStatus())) {
                    tvOfflineBanner.setVisibility(View.VISIBLE);
                } else {
                    tvOfflineBanner.setVisibility(View.GONE);
                }
            }
        });

        studentViewModel.getToastMessage().observe(this, msg -> {
            if (msg != null) Toast.makeText(StudentProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
        });

        studentViewModel.getErrorMessage().observe(this, err -> {
            if (err != null) Toast.makeText(StudentProfileActivity.this, err, Toast.LENGTH_LONG).show();
        });

        if (btnViewProfileTimetable != null) {
            btnViewProfileTimetable.setOnClickListener(v -> startActivity(new Intent(this, StudentTimetableActivity.class)));
        }

        if (btnEditProfileTop != null) {
            btnEditProfileTop.setOnClickListener(v -> showEditProfileDialog());
        }

        BottomNavHelper.setupBottomNav(this, R.id.nav_profile);
    }

    private void showEditProfileDialog() {
        if (currentStudent == null) {
            Toast.makeText(this, "Profile data not loaded yet.", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Profile Details");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_profile, null);
        TextInputEditText etDialogEditName = dialogView.findViewById(R.id.etDialogEditName);
        Spinner spinnerDialogEditProgramme = dialogView.findViewById(R.id.spinnerDialogEditProgramme);

        etDialogEditName.setText(currentStudent.getStudentName());

        ArrayAdapter<String> progAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"CS", "IT", "DS"});
        progAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDialogEditProgramme.setAdapter(progAdapter);

        if ("IT".equalsIgnoreCase(currentStudent.getProgramme())) {
            spinnerDialogEditProgramme.setSelection(1);
        } else if ("DS".equalsIgnoreCase(currentStudent.getProgramme())) {
            spinnerDialogEditProgramme.setSelection(2);
        } else {
            spinnerDialogEditProgramme.setSelection(0);
        }

        builder.setView(dialogView);
        builder.setPositiveButton("Save Changes", (dialog, which) -> {
            String newName = etDialogEditName.getText() != null ? etDialogEditName.getText().toString().trim() : "";
            String newProg = spinnerDialogEditProgramme.getSelectedItem().toString();

            if (newName.isEmpty()) {
                Toast.makeText(this, "Please enter your name.", Toast.LENGTH_SHORT).show();
                return;
            }

            currentStudent.setStudentName(newName);
            currentStudent.setProgramme(newProg);
            studentViewModel.saveStudentProfile(currentStudent);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
