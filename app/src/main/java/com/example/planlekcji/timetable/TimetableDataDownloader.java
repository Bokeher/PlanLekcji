package com.example.planlekcji.timetable;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.planlekcji.MainActivity;
import com.example.planlekcji.ckziu_elektryk.client.CKZiUElektrykClient;
import com.example.planlekcji.ckziu_elektryk.client.timetable.SchoolEntry;
import com.example.planlekcji.ckziu_elektryk.client.timetable.SchoolEntryType;
import com.example.planlekcji.ckziu_elektryk.client.timetable.TimetableService;
import com.example.planlekcji.ckziu_elektryk.client.timetable.info.TimetableInfo;
import com.example.planlekcji.listener.DownloadCompleteListener;
import com.example.planlekcji.timetable.model.DayOfWeek;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TimetableDataDownloader implements Runnable {
    private final DownloadCompleteListener listener;
    private final CKZiUElektrykClient client;

    public TimetableDataDownloader(CKZiUElektrykClient client, DownloadCompleteListener listener) {
        this.listener = listener;
        this.client = client;
    }

    @Override
    public void run() {
        Optional<TimetableInfo> timetableInfoOptional = client.getTimetableInfo();

        if (!timetableInfoOptional.isPresent()) return;

        TimetableInfo timetableInfo = timetableInfoOptional.get();

//        SchoolEntryType schoolEntryType = getTimetableType();
        SchoolEntryType schoolEntryType = SchoolEntryType.CLASSES;

        Log.e("asd", schoolEntryType+"");
        TimetableService timetableService = client.getTimetableService(schoolEntryType);
        List<SchoolEntry> schoolEntries = timetableService.getList();

        Map<DayOfWeek, List<String>> map = timetableService.getTimetable("{AW}");

        // this crashes, because map is null
//        Log.e("adsad", map.toString());
//        Log.e("adsad", map.get(DayOfWeek.MONDAY)+"");

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

    private SchoolEntryType getTimetableType() {
        Context context = MainActivity.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPrefs", 0);

        // 0 - classes, 1 - teachers, 2 - classrooms
        int typeOfTimetable = sharedPreferences.getInt("selectedTypeOfTimetable", 0);

        SchoolEntryType[] timetableTypes = {
            SchoolEntryType.CLASSES,
            SchoolEntryType.TEACHERS,
            SchoolEntryType.CLASSROOMS
        };

        return timetableTypes[typeOfTimetable];
    }
}
