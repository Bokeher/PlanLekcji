package com.example.planlekcji.Settings;

public class ClassInfo {
    private String token;
    private String timetableUrl;

    public ClassInfo(String token, String timetableUrl) {
        this.token = token;
        this.timetableUrl = timetableUrl;
    }

    public String getToken() {
        return token;
    }

    public String getTimetableUrl() {
        return timetableUrl;
    }
}
