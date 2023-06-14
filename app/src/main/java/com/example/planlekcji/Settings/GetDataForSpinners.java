package com.example.planlekcji.Settings;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GetDataForSpinners implements Runnable {
    private List<TimetableInfo> classInfoList = new ArrayList<>();
    private List<TimetableInfo> teachersInfoList = new ArrayList<>();
    private List<TimetableInfo> classroomsInfoList = new ArrayList<>();

    @Override
    public void run() {
        try {
            Document doc = Jsoup.connect("http://plan.ckziu-elektryk.pl/lista.html").get();

            Elements classes = doc.select("#oddzialy .el");
            for (Element classData : classes) {
                // get class token
                String token = classData.text();
                token = token.substring(0, 1) + " " + token.substring(1);

                // get url of timetable
                String href = classData.selectFirst("[href]").attr("href");
                int index = href.indexOf("/");

                String url = "http://plan.ckziu-elektryk.pl/plany/"+href.substring(index+1);

                // create Info Object and add to list
                TimetableInfo classInfo = new TimetableInfo(token, url);
                classInfoList.add(classInfo);
            }

            Elements teachers = doc.select("#nauczyciele .el");
            for (Element teacherData : teachers) {
                //get teacher token
                String token = teacherData.text();

                // get url of timetable
                String href = teacherData.selectFirst("[href]").attr("href");
                int index = href.indexOf("/");

                String url = "http://plan.ckziu-elektryk.pl/plany/"+href.substring(index+1);

                // create ClassInfo Object and add to list
                TimetableInfo teacherInfo = new TimetableInfo(token, url);
                teachersInfoList.add(teacherInfo);
            }

            Elements classrooms = doc.select("#sale .el");
            for (Element classroomsData : classrooms) {
                //get teacher token
                String token = classroomsData.text();

                // get url of timetable
                String href = classroomsData.selectFirst("[href]").attr("href");
                int index = href.indexOf("/");

                String url = "http://plan.ckziu-elektryk.pl/plany/"+href.substring(index+1);

                // create ClassInfo Object and add to list
                TimetableInfo classroomsInfo = new TimetableInfo(token, url);
                classroomsInfoList.add(classroomsInfo);
            }

            // sort all lists
            classInfoList.sort(Comparator.comparing(TimetableInfo::getToken));
            teachersInfoList.sort(Comparator.comparing(TimetableInfo::getToken));
            classroomsInfoList.sort(Comparator.comparing(TimetableInfo::getToken));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public List<TimetableInfo> getClassInfoList() {
        return classInfoList;
    }

    public List<TimetableInfo> getTeachersInfoList() {
        return teachersInfoList;
    }

    public List<TimetableInfo> getClassroomsInfoList() {
        return classroomsInfoList;
    }
}
