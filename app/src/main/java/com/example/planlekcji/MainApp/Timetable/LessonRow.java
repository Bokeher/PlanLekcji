package com.example.planlekcji.MainApp.Timetable;

import org.jsoup.nodes.Element;

import java.util.Map;

public class LessonRow {
    private Element lessonNumbers;
    private Element lessonHours;
    private Map<Integer, Element> dayElements;

    public static final int MONDAY = 1;
    public static final int TUESDAY = 2;
    public static final int WEDNESDAY = 3;
    public static final int THURSDAY = 4;
    public static final int FRIDAY = 5;

    public LessonRow(Element lessonNumbers, Element lessonHours, Map<Integer, Element> dayElements) {
        this.lessonNumbers = lessonNumbers;
        this.lessonHours = lessonHours;
        this.dayElements = dayElements;
    }

    public Element getLessonNumbers() {
        return lessonNumbers;
    }

    public Element getLessonHours() {
        return lessonHours;
    }

    public Element getDayElement(int day) {
        return dayElements.get(day);
    }

    public Map<Integer, Element> getDayElements() {
        return dayElements;
    }
}
