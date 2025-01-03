package com.example.planlekcji.timetable;

import com.example.planlekcji.timetable.model.DayOfWeek;

import java.util.List;
import java.util.Map;

public class TimetableDataProcessor {
    private final Map<DayOfWeek, List<String>> timetableMap;

    // Constructor that takes a Document as a parameter
    public TimetableDataProcessor(Map<DayOfWeek, List<String>> timetableMap) {
        this.timetableMap = timetableMap;
    }

    public Map<DayOfWeek, List<String>> getTimetableMap() {
        return timetableMap;
    }
}
