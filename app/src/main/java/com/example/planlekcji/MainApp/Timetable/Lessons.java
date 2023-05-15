package com.example.planlekcji.MainApp.Timetable;

import org.jsoup.select.Elements;

import java.util.List;

public class Lessons {
    private List<Elements> lessonNumbers;
    private List<Elements> lessonHours;
    private List<Elements> monday;
    private List<Elements> tuesday;
    private List<Elements> wednesday;
    private List<Elements> thursday;
    private List<Elements> friday;

    public Lessons(List<Elements> lessonNumbers, List<Elements> lessonHours, List<Elements> monday, List<Elements> tuesday, List<Elements> wednesday, List<Elements> thursday, List<Elements> friday) {
        this.lessonNumbers = lessonNumbers;
        this.lessonHours = lessonHours;
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
    }

    public List<Elements> getLessonNumbers() {
        return lessonNumbers;
    }

    public List<Elements> getLessonHours() {
        return lessonHours;
    }

    public List<Elements> getMonday() {
        return monday;
    }

    public List<Elements> getTuesday() {
        return tuesday;
    }

    public List<Elements> getWednesday() {
        return wednesday;
    }

    public List<Elements> getThursday() {
        return thursday;
    }

    public List<Elements> getFriday() {
        return friday;
    }
}
