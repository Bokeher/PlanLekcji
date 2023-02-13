package com.example.test2;

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
            Document doc = Jsoup.connect("http://plan.ckziu-elektryk.pl/plany/o36.html").get();

            Elements trs = doc.select(".tabela tr");

            ArrayList<Elements> monday = new ArrayList<Elements>();
            ArrayList<Elements> tuesday = new ArrayList<Elements>();
            ArrayList<Elements> wednesday = new ArrayList<Elements>();
            ArrayList<Elements> thursday = new ArrayList<Elements>();
            ArrayList<Elements> friday = new ArrayList<Elements>();
            ArrayList<Elements> lessonNumbers = new ArrayList<Elements>();
            ArrayList<Elements> lessonHours = new ArrayList<Elements>();

            for (Element tr : trs) {
                lessonNumbers.add(tr.select("td:nth-child(1)"));
                lessonHours.add(tr.select("td:nth-child(2)"));
                monday.add(tr.select("td:nth-child(3)"));
                tuesday.add(tr.select("td:nth-child(4)"));
                wednesday.add(tr.select("td:nth-child(5)"));
                thursday.add(tr.select("td:nth-child(6)"));
                friday.add(tr.select("td:nth-child(7)"));
            }

            lessonHours.remove(0);
            lessonNumbers.remove(0);
            monday.remove(0);
            tuesday.remove(0);
            wednesday.remove(0);
            thursday.remove(0);
            friday.remove(0);

            lessons = new Lessons(lessonNumbers, lessonHours, monday, tuesday, wednesday, thursday, friday);
        } catch (Exception e) {
            System.out.println(e);
        }
    }


    public Lessons getLessons() {
        return lessons;
    }
}
