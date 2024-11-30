package com.example.planlekcji.ckziu_elektryk.client.utils;

public final class ParamValidator {

    private ParamValidator() {
    }

    public static void checkNotNullAndNotEmpty(String text) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("This field can not be null or empty");
        }
    }
}
