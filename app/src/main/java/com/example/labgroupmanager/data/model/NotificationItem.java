package com.example.labgroupmanager.data.model;

public class NotificationItem {
    private final String title;
    private final String body;
    private final String time;

    public NotificationItem(String title, String body, String time) {
        this.title = title;
        this.body = body;
        this.time = time;
    }

    public String getTitle() { return title; }
    public String getBody() { return body; }
    public String getTime() { return time; }
}
