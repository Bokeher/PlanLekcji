package com.example.planlekcji.MainApp.Timetable;

import java.util.Map;

public class LessonRow {
    private final String lessonNumbers;
    private final String lessonHours;
    private final Map<Integer, String> dayData;

    public static final int MONDAY = 1;
    public static final int TUESDAY = 2;
    public static final int WEDNESDAY = 3;
    public static final int THURSDAY = 4;
    public static final int FRIDAY = 5;

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
