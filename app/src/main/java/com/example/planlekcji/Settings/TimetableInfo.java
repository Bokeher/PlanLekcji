package com.example.planlekcji.Settings;

public class TimetableInfo {
    private String token;
    private String url;

    public TimetableInfo(String token, String url) {
        this.token = token;
        this.url = url;
    }

    public String getToken() {
        return token;
    }

    public String getUrl() {
        return url;
    }
}
