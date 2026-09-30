package com.example.labgroupmanager.data.local;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "lab_group_manager_session";
    private static final String KEY_AUTH_TOKEN = "auth_token";
    private static final String KEY_ACCOUNT_ID = "account_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_USER_ROLE = "user_role"; // STUDENT, LECTURER
    private static final String KEY_STUDENT_ID = "student_id";
    private static final String KEY_LAST_SYNC_TIME = "last_sync_time";
    private static final String KEY_REMEMBERED_PORTAL = "remembered_portal";
    private static final String KEY_SERVER_URL = "server_url";
    public static final String DEFAULT_BASE_URL = "http://10.0.2.2:3000/api/";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveSession(String token, String accountId, String username, String role, String studentId) {
        prefs.edit()
                .putString(KEY_AUTH_TOKEN, token)
                .putString(KEY_ACCOUNT_ID, accountId)
                .putString(KEY_USERNAME, username)
                .putString(KEY_USER_ROLE, role)
                .putString(KEY_STUDENT_ID, studentId)
                .apply();
    }

    public String getAuthToken() {
        return prefs.getString(KEY_AUTH_TOKEN, null);
    }

    public String getAccountId() {
        return prefs.getString(KEY_ACCOUNT_ID, null);
    }

    public String getUsername() {
        return prefs.getString(KEY_USERNAME, null);
    }

    public String getUserRole() {
        return prefs.getString(KEY_USER_ROLE, null);
    }

    public String getStudentId() {
        return prefs.getString(KEY_STUDENT_ID, null);
    }

    public void setStudentId(String studentId) {
        prefs.edit().putString(KEY_STUDENT_ID, studentId).apply();
    }

    public String getLastSyncTime() {
        return prefs.getString(KEY_LAST_SYNC_TIME, null);
    }

    public void setLastSyncTime(String timestamp) {
        prefs.edit().putString(KEY_LAST_SYNC_TIME, timestamp).apply();
    }

    public void setRememberedPortal(String portal) {
        prefs.edit().putString(KEY_REMEMBERED_PORTAL, portal).apply();
    }

    public String getRememberedPortal() {
        return prefs.getString(KEY_REMEMBERED_PORTAL, null);
    }

    public String getServerUrl() {
        return prefs.getString(KEY_SERVER_URL, DEFAULT_BASE_URL);
    }

    public void setServerUrl(String url) {
        if (url != null && !url.endsWith("/")) {
            url = url + "/";
        }
        prefs.edit().putString(KEY_SERVER_URL, url).apply();
    }

    public boolean isLoggedIn() {
        return getAuthToken() != null && getAccountId() != null;
    }

    public boolean isLecturer() {
        return "LECTURER".equalsIgnoreCase(getUserRole());
    }

    public void clearSession() {
        prefs.edit().clear().apply();
    }
}
