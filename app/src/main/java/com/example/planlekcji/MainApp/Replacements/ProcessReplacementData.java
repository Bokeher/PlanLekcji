package com.example.planlekcji.MainApp.Replacements;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.planlekcji.MainActivity;
import com.example.planlekcji.R;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ProcessReplacementData {
    private final Document document;
    private List<String> replacements = new ArrayList<>();
    private List<ReplacementToTimetable> replacementsForTimetable = new ArrayList<>();

    String classToken = "";

    public List<String> getReplacements() {
        return replacements;
    }

    public List<ReplacementToTimetable> getReplacementsForTimetable() {
        return replacementsForTimetable;
    }

    public ProcessReplacementData(Document document) {
        this.document = document;
    }

    public void process() {
        Elements tds = document.select("table tr td");
        Elements teachers = document.select(".st1");

        // return early if there are no replacements
        if(teachers.isEmpty() || teachers.first().text().contains("nie zaplanowano")) return;

        changeTeacherLastNamesToInitials(teachers);

        String title = document.select(".st0").get(0).text();
        boolean singleDay = !title.contains(" - ");

        if(singleDay) addDatesToTeachers(teachers, title);

        removeUnwantedData(tds);

        // in case of 0 replacements just return empty lists
        if(title.isEmpty()) {
            replacements = new ArrayList<>();
            replacements.add(teachers.get(0).text()); // teachers are in st1 class and the info about zero replacements too
            replacementsForTimetable = new ArrayList<>();
            return;
        }

        replacements = processReplacements(tds, teachers);
        replacementsForTimetable = processReplacementsForTimetable(replacements, singleDay, title);
    }

    private void addDatesToTeachers(Elements teachers, String title) {
        for(Element teacher : teachers) {
            String[] words = title.split(" ");
            String date = words[3] + " " + words[4];
            teacher.text(teacher.text() + " / " + date);
        }
    }

    private void changeTeacherLastNamesToInitials(Elements teachers) {
        for (Element teacher : teachers) {
            String originalText = teacher.text();

            int start = originalText.indexOf(" ");

            int end = originalText.indexOf("/");
            if(end == -1) end = originalText.length();

            if (start >= 0 && end > start) {
                String[] names = originalText.substring(start + 1, end).split("-");

                StringBuilder finalName = new StringBuilder();
                for (String name : names) {
                    finalName.append(getInitial(name)).append(". ");
                }

                String newName = finalName.toString();

                if (!newName.isEmpty()) {
                    String modifiedText = originalText.substring(0, start + 1) +
                            newName +
                            originalText.substring(end);

                    teacher.text(modifiedText);
                }
            }
        }
    }

    private String getInitial(String str) {
        if (str == null || str.length() < 2) {
            throw new IllegalArgumentException("Input string must have at least two character");
        }

        str = str.toLowerCase();
        char firstChar = str.charAt(0);
        char secondChar = str.charAt(1);

        final String[] polishDigraphs = { "sz", "cz", "dż", "dź", "rz", "ch", "dz" };

        for (String digraph : polishDigraphs) {
            if(digraph.charAt(0) == firstChar) {
                if(digraph.charAt(1) == secondChar) {
                    String ch1 = digraph.substring(0, 1).toUpperCase();
                    String ch2 = digraph.substring(1, 2);
                    return ch1.concat(ch2);
                }
            }
        }

        return String.valueOf(firstChar).toUpperCase();
    }

    private List<ReplacementToTimetable> processReplacementsForTimetable(List<String> replacements, boolean singleDay, String title) {
        if(!needsToContinue()) return new ArrayList<>();
        // this method also gets class token from shared preferences

        List<ReplacementToTimetable> resList = new ArrayList<>();

        for (String teacherAndHisReplacements : replacements) {
            String[] allLines = teacherAndHisReplacements.split("<br>");

            for (int i = 0; i < allLines.length; i++) {
                if(!singleDay && i == 0) continue;
                String line = allLines[i];

                Calendar dateOfReplacement = getDateOfReplacement(singleDay, title, allLines);

                // skips if this replacement isn't within this week
                if(skipThisReplacement(dateOfReplacement)) continue;

                if(line.contains(classToken)) {
                    int lessonNumber = getLessonNumber(line);
                    int groupNumber = getGroupNumber(line);
                    int dayNumber = getDayNumber(dateOfReplacement);
                    String extraInfo = getExtraInfo(line);

                    ReplacementToTimetable replacement = new ReplacementToTimetable(lessonNumber, dayNumber, groupNumber, extraInfo);
                    resList.add(replacement);
                }
            }
        }

        return resList;
    }

    private String getExtraInfo(String line) {
        int indexOfReplacementInfo = line.indexOf(" - ") + 3;
        String replacementInfo = line.substring(indexOfReplacementInfo);

        String[] allPossibleReplacementInfosOfNoLesson = {
                "Uczniowie przychodzą później",
                "Uczniowie zwolnieni do domu",
                "Okienko dla uczniów",
                "Bez konsekwencji",
                "Uczniowie w czytelni"
        };

        for (String text : allPossibleReplacementInfosOfNoLesson) {
            if (replacementInfo.contains(text)) {
                return "";
            }
        }

        // if the lesson is not cancelled then there is replacing teacher
        return replacementInfo;
    }

    private Calendar getDateOfReplacement(boolean singleDay, String title, String[] allLines) {
        Calendar calendar = Calendar.getInstance();
        String dateStr;

        if(singleDay) {
            String[] tempArr = title.split(" ");
            dateStr = tempArr[3];
        } else {
            String teacherText = allLines[0];

            dateStr = teacherText.substring(teacherText.indexOf("/")+2);
            dateStr = dateStr.substring(0, dateStr.indexOf(" "));
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            Date date = sdf.parse(dateStr);
            calendar.setTime(date);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        return calendar;
    }

    private int getDayNumber(Calendar dateOfReplacement) {
        return dateOfReplacement.get(Calendar.DAY_OF_WEEK) - 1;
    }

    private int getGroupNumber(String line) {
        // if there is a group division it is in a form: 4PTN(groupNumber) ex.: 4PTN(2), 4PTN(1)
        // this method grabs the number between curly brackets
        int firstCurlyBracket = line.indexOf("(");
        if(firstCurlyBracket == -1) return 0;

        int firstClosingCurlyBracket = line.indexOf(")");
        return Integer.parseInt(line.substring(firstCurlyBracket+1, firstClosingCurlyBracket));
    }

    private int getLessonNumber(String line) {
        int firstSpace = line.indexOf(" "); // get the first occurrence of space. The text before it is lesson number
        return Integer.parseInt(line.substring(0, firstSpace));
    }

    private boolean needsToContinue() {
        Context context = MainActivity.getContext();

        SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPrefs",0);
        final int selectedTypeOfTimetableKey = sharedPreferences.getInt(context.getString(R.string.selectedTypeOfTimetableKey), 0);
        final boolean replacementVisibilityOnTimetable = sharedPreferences.getBoolean(context.getString(R.string.replacementVisibilityOnTimetable), true);
        classToken = sharedPreferences.getString(context.getString(R.string.classTokenKey), "");

        return selectedTypeOfTimetableKey == 0 && replacementVisibilityOnTimetable && !classToken.isEmpty();
    }

    private List<String> processReplacements(Elements tds, Elements teachers) {
        if(teachers.isEmpty()) return null;
        StringBuilder result = new StringBuilder();

        for (Element td : tds) {
            if(tdIsLessonNumber(td)) {
                result.append("<br>");
            }

            if(teachers.contains(td)) {
                // if this is teacher then bold it and put <br> before
                result.append("<br><br><b>").append(td.text()).append("</b>");
            } else {
                // else just add data
                result.append(td.text()).append(" ");
            }

            if(tdIsLessonNumber(td)) {
                result.append("| ");
            }
        }

        String[] data = result.toString().split("<br><br>");
        return Arrays.asList(data);
    }

    private boolean skipThisReplacement(Calendar replacementCalendar) {
        Calendar currentCalendar = Calendar.getInstance();

        int replacementWeekNumber = replacementCalendar.get(Calendar.WEEK_OF_YEAR);
        int currentWeekNumber = currentCalendar.get(Calendar.WEEK_OF_YEAR);

        int currentDayOfWeek = currentCalendar.get(Calendar.DAY_OF_WEEK);

        if(currentDayOfWeek == Calendar.SATURDAY || currentDayOfWeek == Calendar.SUNDAY) {
            currentWeekNumber++;
        }

        return replacementWeekNumber != currentWeekNumber;
    }

    private void removeUnwantedData(Elements tds) {
        List<String> thingsToRemoveFromList = Arrays.asList("", "lekcja", "opis", "zastępca", "uwagi");
        for (int i = tds.size()-1; i >= 0; i--) {
            if(thingsToRemoveFromList.contains(tds.get(i).text())) {
                tds.remove(i);
            }
        }
    }

    private boolean tdIsLessonNumber(Element td) {
        return td.text().length() <= 1;
    }

}
