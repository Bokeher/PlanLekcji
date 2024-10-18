package com.example.planlekcji.timetable.model;

import java.util.Arrays;

public enum DayOfWeek {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY;

    public int getDayOfWeekAsNumber() {
        return ordinal() + 1;
    }

    public static DayOfWeek getDayOfWeek(int dayNumber) {
        if (dayNumber <= 0 || dayNumber > 5) {
            throw new IllegalArgumentException("Illegal range of dayOfWeek. Range: 1-5");
        }

        return values()[dayNumber - 1];
    }

    public static Integer[] getDaysOfWeekAsNumbers() {
        return Arrays.stream(values())
                .map(DayOfWeek::getDayOfWeekAsNumber)
                .toArray(Integer[]::new);
    }
}
