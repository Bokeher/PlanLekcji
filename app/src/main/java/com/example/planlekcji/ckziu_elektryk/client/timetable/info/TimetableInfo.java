package com.example.planlekcji.ckziu_elektryk.client.timetable.info;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record TimetableInfo(String applyAt, LocalDate generatedAt) {

    public static final DateTimeFormatter GENERATED_AT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
}
