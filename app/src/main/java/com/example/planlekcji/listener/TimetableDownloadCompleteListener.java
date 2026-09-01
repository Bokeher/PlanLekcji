package com.example.planlekcji.listener;

import com.example.planlekcji.ckziu_elektryk.client.timetable.lesson.Lesson;
import com.example.planlekcji.utils.DayOfWeek;

import java.util.List;
import java.util.Map;

public interface TimetableDownloadCompleteListener extends DownloadCompleteListener<Map<DayOfWeek, List<Lesson>>> {

}
