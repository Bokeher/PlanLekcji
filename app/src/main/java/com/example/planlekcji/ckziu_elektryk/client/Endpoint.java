package com.example.planlekcji.ckziu_elektryk.client;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public enum Endpoint {

    LATEST_REPLACEMENTS("replacements"),
    REPLACEMENTS_FROM_FILE("replacements/{file_name}");

    private String name;

    Endpoint(String name) {
        this.name = name;
    }

    public Endpoint withPlaceholders(@NotNull Map<String, String> placeholders) {
        if (!this.name.contains("{") || !this.name.contains("}")) {
            throw new IllegalStateException("This endpoint hasn't placeholders");
        }

        for (Map.Entry<String, String> keyValueEntry : placeholders.entrySet()) {
            this.name = this.name.replace(keyValueEntry.getKey(), keyValueEntry.getValue());
        }

        return this;
    }

    public String getName() {
        return name;
    }
}
