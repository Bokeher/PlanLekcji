package com.example.planlekcji.replacements.model;

/**
 * This class is used for transferring data from replacement (got from website) to data needed to show this replacement on timetable.
 */
public class ReplacementToTimetable {
    private final int lessonNumber;
    private final int dayNumber;
    private final int groupNumber; // only used when the replacement is for only one group (0 when no group division)
    private final String extraInfo; // empty means there is no replacing teacher and there is no lesson at the moment

    public int getLessonNumber() {
        return lessonNumber;
    }

    public int getDayNumber() {
        return dayNumber;
    }

    public int getGroupNumber() {
        return groupNumber;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public ReplacementToTimetable(int lessonNumber, int dayNumber, int groupNumber, String extraInfo) {
        this.lessonNumber = lessonNumber;
        this.dayNumber = dayNumber;
        this.groupNumber = groupNumber;
        this.extraInfo = extraInfo;
    }
}
