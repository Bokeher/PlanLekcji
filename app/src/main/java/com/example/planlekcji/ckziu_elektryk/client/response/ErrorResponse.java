package com.example.planlekcji.ckziu_elektryk.client.response;

public class ErrorResponse extends APIResponse {

    private final String message;

    public ErrorResponse(int httpStatus, String message) {
        super(httpStatus);
        this.message = message;
    }

    public ErrorResponse(int httpStatus) {
        super(httpStatus);
        this.message = "";
    }

    public String getMessage() {
        return message;
    }
}
