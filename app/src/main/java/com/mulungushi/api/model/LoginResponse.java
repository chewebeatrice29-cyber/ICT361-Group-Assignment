package com.mulungushi.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Response from POST /api/auth/login
 * Contains the user object and a JWT token.
 */
public class LoginResponse {

    @SerializedName("user")
    private User user;

    @SerializedName("token")
    private String token;

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public static class User {

        @SerializedName("user_id")
        private String userId;

        @SerializedName("student_number")
        private String studentNumber;

        @SerializedName("full_name")
        private String fullName;

        @SerializedName("email")
        private String email;

        @SerializedName("programme_id")
        private String programmeId;

        @SerializedName("role")
        private String role;

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public String getStudentNumber() { return studentNumber; }
        public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getProgrammeId() { return programmeId; }
        public void setProgrammeId(String programmeId) { this.programmeId = programmeId; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
}