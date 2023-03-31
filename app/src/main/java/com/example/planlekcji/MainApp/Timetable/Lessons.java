package com.example.planlekcji.MainApp.Timetable;

import org.jsoup.select.Elements;

import java.util.ArrayList;

public class Lessons {
    private ArrayList<Elements> lessonNumbers;
    private ArrayList<Elements> lessonHours;
    private ArrayList<Elements> monday;
    private ArrayList<Elements> tuesday;
    private ArrayList<Elements> wednesday;
    private ArrayList<Elements> thursday;
    private ArrayList<Elements> friday;

    public Lessons(ArrayList<Elements> lessonNumbers, ArrayList<Elements> lessonHours, ArrayList<Elements> monday, ArrayList<Elements> tuesday, ArrayList<Elements> wednesday, ArrayList<Elements> thursday, ArrayList<Elements> friday) {
        this.lessonNumbers = lessonNumbers;
        this.lessonHours = lessonHours;
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
    }

    public ArrayList<Elements> getLessonNumbers() {
        return lessonNumbers;
    }

    public ArrayList<Elements> getLessonHours() {
        return lessonHours;
    }

    public ArrayList<Elements> getMonday() {
        return monday;
    }

    public ArrayList<Elements> getTuesday() {
        return tuesday;
    }

    public ArrayList<Elements> getWednesday() {
        return wednesday;
    }

    public ArrayList<Elements> getThursday() {
        return thursday;
    }

    public ArrayList<Elements> getFriday() {
        return friday;
    }
}
