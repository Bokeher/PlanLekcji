package com.example.planlekcji.settings.model;

public class TimetableInfo {
    private final String token;
    private final String url;

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
