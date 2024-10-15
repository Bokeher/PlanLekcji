package com.example.planlekcji.timetable;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.planlekcji.MainActivity;
import com.example.planlekcji.listener.DownloadCompleteListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class TimetableDataDownloader implements Runnable {
    private final DownloadCompleteListener listener;

    public TimetableDataDownloader(DownloadCompleteListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        try {
            Document doc = Jsoup.connect(getTimetableUrl()).get();
            listener.onDownloadComplete(doc);
        } catch (IOException e) {
            e.printStackTrace();
            listener.onDownloadFailed();
        }
    }

    private String getTimetableUrl() {
        Context context = MainActivity.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPrefs", 0);

        // 0 - classes, 1 - teachers, 2 - classrooms
        int typeOfTimetable = sharedPreferences.getInt("selectedTypeOfTimetable", 0);
        String sharedPrefUrl;
        if (typeOfTimetable == 0) {
            sharedPrefUrl = sharedPreferences.getString("classTimetableUrl", "http://plan.ckziu-elektryk.pl/plany/o1.html");
        } else if (typeOfTimetable == 1) {
            sharedPrefUrl = sharedPreferences.getString("teacherTimetableUrl", "http://plan.ckziu-elektryk.pl/plany/o1.html");
        } else {
            sharedPrefUrl = sharedPreferences.getString("classroomTimetableUrl", "http://plan.ckziu-elektryk.pl/plany/o1.html");
        }
        return sharedPrefUrl;
    }
}
