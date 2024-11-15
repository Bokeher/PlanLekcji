package com.example.planlekcji.ckziu_elektryk.client.common;

import com.example.planlekcji.ckziu_elektryk.client.response.ErrorResponse;
import com.example.planlekcji.ckziu_elektryk.client.response.SuccessResponse;

import java.util.function.Consumer;
import java.util.function.Function;

public class APIResponseCall {

    private SuccessResponse successResponse;
    private ErrorResponse errorResponse;

    public APIResponseCall error(Consumer<ErrorResponse> errorResponseConsumer) {
        if (errorResponse == null) return this;

        errorResponseConsumer.accept(errorResponse);

        return this;
    }

    void setSuccessResponse(SuccessResponse successResponse) {
        this.successResponse = successResponse;
    }

    void setErrorResponse(ErrorResponse errorResponse) {
        this.errorResponse = errorResponse;
    }

    public <T> T success(Function<SuccessResponse, T> successResponseFunction) {
        if (successResponse == null) return null;

        return successResponseFunction.apply(successResponse);
    }

    public boolean hasResponse() {
        return successResponse != null || errorResponse != null;
    }
}
