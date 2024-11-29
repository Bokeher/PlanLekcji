package com.example.planlekcji.ckziu_elektryk.client.response;

public class APIResponse {

    private final int httpStatus;

    public APIResponse(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
