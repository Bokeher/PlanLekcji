package com.example.planlekcji.settings;

import android.content.SharedPreferences;

import com.example.planlekcji.ckziu_elektryk.client.timetable.lesson.LessonDetails;
import com.example.planlekcji.utils.DayOfWeek;

/**
 * Utility for managing group-related user preferences in SharedPreferences.
 */
public final class GroupPreferenceManager {

    public static final String KEY_HIDE_UNSELECTED = "group_pref_hide_unselected";
    public static final String KEY_LOCK_SELECTION = "group_pref_lock_selection";
    private static final String PREF_SLOT_PREFIX = "group_slot_";

    private GroupPreferenceManager() {}

    public static boolean isHideUnselected(SharedPreferences prefs) {
        return prefs != null && prefs.getBoolean(KEY_HIDE_UNSELECTED, false);
    }

    public static void setHideUnselected(SharedPreferences prefs, boolean hide) {
        if (prefs != null) {
            prefs.edit().putBoolean(KEY_HIDE_UNSELECTED, hide).apply();
        }
    }

    public static boolean isLockSelection(SharedPreferences prefs) {
        return prefs != null && prefs.getBoolean(KEY_LOCK_SELECTION, false);
    }

    public static void setLockSelection(SharedPreferences prefs, boolean lock) {
        if (prefs != null) {
            prefs.edit().putBoolean(KEY_LOCK_SELECTION, lock).apply();
        }
    }

    public static String getSubjectName(LessonDetails details) {
        return (details != null && details.getSubject() != null && details.getSubject().name() != null)
                ? details.getSubject().name().trim() : "";
    }

    public static String getTeacher(LessonDetails details) {
        return (details != null && details.getTeacher() != null) ? details.getTeacher().trim() : "";
    }

    public static String getClassroom(LessonDetails details) {
        return (details != null && details.getClassroom() != null) ? details.getClassroom().trim() : "";
    }

    public static String buildLessonKey(String classToken, DayOfWeek dayOfWeek, int lessonNumber, String subject, String teacher, String classroom) {
        String token = classToken != null ? classToken.trim() : "";
        String day = dayOfWeek != null ? dayOfWeek.name() : "";
        String sub = subject != null ? subject.trim() : "";
        String t = teacher != null ? teacher.trim() : "";
        String c = classroom != null ? classroom.trim() : "";
        return PREF_SLOT_PREFIX + token + "_" + day + "_" + lessonNumber + "_" + sub + "_" + t + "_" + c;
    }

    public static String buildLessonKey(String classToken, DayOfWeek dayOfWeek, int lessonNumber, LessonDetails details) {
        return buildLessonKey(classToken, dayOfWeek, lessonNumber, getSubjectName(details), getTeacher(details), getClassroom(details));
    }

    public static boolean isLessonHidden(SharedPreferences prefs, String classToken, DayOfWeek dayOfWeek, int lessonNumber, LessonDetails details) {
        if (prefs == null || classToken == null || classToken.trim().isEmpty() || dayOfWeek == null || details == null) {
            return false;
        }
        return prefs.getBoolean(buildLessonKey(classToken, dayOfWeek, lessonNumber, details), false);
    }

    public static void setLessonHidden(SharedPreferences prefs, String classToken, DayOfWeek dayOfWeek, int lessonNumber, LessonDetails details, boolean hidden) {
        if (prefs == null || classToken == null || classToken.trim().isEmpty() || dayOfWeek == null || details == null) return;
        String key = buildLessonKey(classToken, dayOfWeek, lessonNumber, details);
        if (hidden) {
            prefs.edit().putBoolean(key, true).apply();
        } else {
            prefs.edit().remove(key).apply();
        }
    }

    public static void toggleLessonHidden(SharedPreferences prefs, String classToken, DayOfWeek dayOfWeek, int lessonNumber, LessonDetails details) {
        boolean current = isLessonHidden(prefs, classToken, dayOfWeek, lessonNumber, details);
        setLessonHidden(prefs, classToken, dayOfWeek, lessonNumber, details, !current);
    }

    public static void resetClassChoices(SharedPreferences prefs, String classToken) {
        if (prefs == null || classToken == null || classToken.trim().isEmpty()) return;
        String prefix = PREF_SLOT_PREFIX + classToken.trim() + "_";
        SharedPreferences.Editor editor = prefs.edit();
        for (String key : prefs.getAll().keySet()) {
            if (key != null && key.startsWith(prefix)) {
                editor.remove(key);
            }
        }
        editor.apply();
    }
}


