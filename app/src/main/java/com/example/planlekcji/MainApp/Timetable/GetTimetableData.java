package com.example.planlekcji.MainApp.Timetable;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.planlekcji.MainActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class GetTimetableData implements Runnable {
    private Lessons lessons;

    @Override
    public void run() {
        try {
            //36
            Context context = MainActivity.getContext();
            SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPrefs", 0);

            // 0 - klasy, 1 - nauczyciel, 2 - sale
            int typeOfTimetable = sharedPreferences.getInt("selectedTypeOfTimetable", 0);
            String sharedPrefUrl = "";
            if(typeOfTimetable == 0) {
                sharedPrefUrl = sharedPreferences.getString("classTimetableUrl", "http://plan.ckziu-elektryk.pl/plany/o1.html");
            } else if(typeOfTimetable == 1){
                sharedPrefUrl = sharedPreferences.getString("teacherTimetableUrl", "http://plan.ckziu-elektryk.pl/plany/o1.html");
            } else {
                sharedPrefUrl = sharedPreferences.getString("classroomTimetableUrl", "http://plan.ckziu-elektryk.pl/plany/o1.html");
            }
            String url = sharedPrefUrl;

            Document doc = Jsoup.connect(url).get();

            // za duzo bledow to sypie, aby to przerobic na tablice dwuwymiarowa
            Elements trs = doc.select(".tabela tr");

            ArrayList<Elements> monday = new ArrayList<Elements>();
            ArrayList<Elements> tuesday = new ArrayList<Elements>();
            ArrayList<Elements> wednesday = new ArrayList<Elements>();
            ArrayList<Elements> thursday = new ArrayList<Elements>();
            ArrayList<Elements> friday = new ArrayList<Elements>();
            ArrayList<Elements> lessonNumbers = new ArrayList<Elements>();
            ArrayList<Elements> lessonHours = new ArrayList<Elements>();

            for (int i = 1; i < trs.size(); i++) {
                Element tr = trs.get(i);

                lessonNumbers.add(tr.select("td:nth-child(1)"));
                lessonHours.add(tr.select("td:nth-child(2)"));
                monday.add(tr.select("td:nth-child(3)"));
                tuesday.add(tr.select("td:nth-child(4)"));
                wednesday.add(tr.select("td:nth-child(5)"));
                thursday.add(tr.select("td:nth-child(6)"));
                friday.add(tr.select("td:nth-child(7)"));
            }

            lessons = new Lessons(lessonNumbers, lessonHours, monday, tuesday, wednesday, thursday, friday);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public Lessons getLessons() {
        return lessons;
    }
}
