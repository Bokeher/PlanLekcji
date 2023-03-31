package com.example.planlekcji.MainApp.Replacements;

import java.util.List;

public class Replacement {
    private String title;
    private String teacher;
    private List<String> lesson;
    boolean singleDay;

    public Replacement(String title, String teacher, List<String> lesson, boolean singleDay) {
        this.title = title;
        this.teacher = teacher;
        this.lesson = lesson;
        this.singleDay = singleDay;
    }

    public String getTitle() {
        return title;
    }

    public String getTeacher() {
        return teacher;
    }

    public List<String> getLesson() {
        return lesson;
    }

    public boolean isSingleDay() {
        return singleDay;
    }

    public String toStringDebug() {
        return "Replacement{" +
                "title='" + title + '\'' +
                ", teacher='" + teacher + '\'' +
                ", lesson=" + lesson +
                '}';
    }
    @Override
    public String toString() {
        return teacher+"\n"+
                String.join("\n", lesson);
    }
}
