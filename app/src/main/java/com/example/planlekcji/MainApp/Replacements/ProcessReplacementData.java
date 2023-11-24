package com.example.planlekcji.MainApp.Replacements;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

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
    private Document document;
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
        // get all data and teacher names
        Elements tds = document.select("table tr td");
        Elements teachers = document.select(".st1");

        String title = document.select(".st0").get(0).text();
        boolean singleDay = !title.contains(" - ");

        removeUnwantedData(tds);

        // String containing all replacements with html tags (used for showing all replacements in replacement tab)
        replacements = processReplacements(tds, teachers);
        replacementsForTimetable = processReplacementsForTimetable(replacements, singleDay, title);
    }

    private List<ReplacementToTimetable> processReplacementsForTimetable(List<String> replacements, boolean singleDay, String title) {
        if(!needsToContinue()) return null;
        // this method also gets class token from shared preferences

        Log.e("run", classToken);
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

        Log.e("order", "endOfProcessing");

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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
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

        return selectedTypeOfTimetableKey == 0 && replacementVisibilityOnTimetable && !classToken.equals("");
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

    private boolean skipThisReplacement(Calendar calendar) {
        Calendar currentCalendar = Calendar.getInstance();

        int weekNumber = calendar.get(Calendar.WEEK_OF_YEAR);
        int currentWeekNumber = currentCalendar.get(Calendar.WEEK_OF_YEAR);

        int currentDayOfWeek = currentCalendar.get(Calendar.DAY_OF_WEEK);
        if(currentDayOfWeek == Calendar.SUNDAY || currentDayOfWeek == Calendar.SATURDAY) {
            weekNumber++;
        }

        return weekNumber > currentWeekNumber;
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
