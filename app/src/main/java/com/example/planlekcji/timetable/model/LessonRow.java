package com.example.planlekcji.timetable.model;

import java.util.Map;

public class LessonRow {
    private final String lessonNumbers;
    private final String lessonHours;
    private final Map<Integer, String> dayData;

    public LessonRow(String lessonNumbers, String lessonHours, Map<Integer, String> dayData) {
        this.lessonNumbers = lessonNumbers;
        this.lessonHours = lessonHours;
        this.dayData = dayData;
    }

    public String getLessonNumbers() {
        return lessonNumbers;
    }

    public String getLessonHours() {
        return lessonHours;
    }

    public Map<Integer, String> getDayData() {
        return dayData;
    }
}
