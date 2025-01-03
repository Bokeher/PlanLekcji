package com.example.planlekcji.timetable.model;

import java.util.List;
import java.util.Map;

public class LessonRow {
    private final String lessonNumbers;
    private final String lessonHours;
    private final Map<DayOfWeek, List<String>> dayData;

    public LessonRow(String lessonNumbers, String lessonHours, Map<DayOfWeek, List<String>> dayData) {
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

    public Map<DayOfWeek, List<String>> getDayData() {
        return dayData;
    }
}
