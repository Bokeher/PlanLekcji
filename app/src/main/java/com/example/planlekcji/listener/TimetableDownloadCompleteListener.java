package com.example.planlekcji.listener;

import com.example.planlekcji.timetable.model.DayOfWeek;

import java.util.List;
import java.util.Map;

public interface TimetableDownloadCompleteListener {

    void onDownloadComplete(Map<DayOfWeek, List<String>> timetableMap);

    void onDownloadFailed();
}
