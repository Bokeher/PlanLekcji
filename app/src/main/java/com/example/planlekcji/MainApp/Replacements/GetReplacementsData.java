package com.example.planlekcji.MainApp.Replacements;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.planlekcji.MainActivity;
import com.example.planlekcji.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GetReplacementsData implements Runnable {
    private String allReplacements = "";
    private List<String> replacementsForSearch = new ArrayList<>();
    List<ReplacementToTimetable> replacementDataForTimetable = new ArrayList<>();

    @Override
    public void run() {
        try {
            Document doc = Jsoup.connect("http://zastepstwa.ckziu-elektryk.pl/").get();

            // get all data and teacher names
            Elements tds = doc.select("table tr td");
            Elements teachers = doc.select(".st1");
            String title = doc.select(".st0").get(0).text();

            boolean singleDay = true;
            if (title.contains(" - ")) {
                singleDay = false;
            }

            removeUnwantedData(tds);

            for (Element td : tds) {
                if(tdIsLessonNumber(td)) {
                    allReplacements += "<br>";
                }

                if(teachers.contains(td)) {
                    // if this is teacher then bold it and put <br> before
                    allReplacements += "<br><br><b>"+td.text()+"</b>";
                } else {
                    // else just add data
                    allReplacements += td.text()+" ";
                }

                if(tdIsLessonNumber(td)) {
                    allReplacements += "| ";
                }
            }
            if(teachers.isEmpty()) allReplacements = "Brak zastępstw";

            // prepare data for searching
            String[] data = allReplacements.split("<br><br>");
            for (String singleData : data) {
                if(singleData == data[0]) continue;
                replacementsForSearch.add(singleData);
            }

            // check if needed and then prepare data for showing it on timetable
            Context context = MainActivity.getContext();

            SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPrefs",0);
            final int selectedTypeOfTimetableKey = sharedPreferences.getInt(context.getString(R.string.selectedTypeOfTimetableKey), 0);
            final boolean replacementVisibilityOnTimetable = sharedPreferences.getBoolean(context.getString(R.string.replacementVisibilityOnTimetable), true);
            final String classToken = sharedPreferences.getString(context.getString(R.string.classTokenKey), "");

            if(selectedTypeOfTimetableKey == 0 && replacementVisibilityOnTimetable && !classToken.equals("")) {
                for (String teacherAndHisReplacements : data) {
                    String[] allLines = teacherAndHisReplacements.split("<br>");

                    for (int i = 0; i < allLines.length; i++) {
                        String line = allLines[i];
                        if(line.contains(classToken)) {
                            // get lesson number
                            int firstSpace = line.indexOf(" "); // get the first occurrence of space and the text before it is lesson number
                            int lessonNumber = Integer.parseInt(line.substring(0, firstSpace));

                            // get group number
                            int groupNumber = 0;

                            int firstCurlyBracket = line.indexOf("(");
                            if(firstCurlyBracket != -1) {
                                int firstClosingCurlyBracket = line.indexOf(")");
                                groupNumber = Integer.parseInt(line.substring(firstCurlyBracket+1, firstClosingCurlyBracket));
                            }

                            // get lesson number
                            int dayNumber = 0; // 1 - monday, 2 - tuesday, etc...

                            Calendar calendar = Calendar.getInstance();
                            if(singleDay) {
                                String currYear = ""+calendar.get(Calendar.YEAR);
                                int dateStartingIndex = title.indexOf(currYear);
                                String yearAndRestOfString = title.substring(dateStartingIndex);
                                int dateEndingIndex = yearAndRestOfString.indexOf(" ");

                                String dateStr = yearAndRestOfString.substring(0, dateEndingIndex);

                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                Date date = sdf.parse(dateStr);
                                calendar.setTime(date);

                                dayNumber = calendar.get(Calendar.DAY_OF_WEEK)-1;
                            } else {
                                String teacherText = allLines[0];
                                String[] dayNames = {
                                        "poniedziałek",
                                        "wtorek",
                                        "środa",
                                        "czwartek",
                                        "piątek"
                                };

                                for (int j = 0; j < dayNames.length; j++) {
                                    if(teacherText.contains(dayNames[j])) {
                                        dayNumber = j;
                                    }
                                }

                                dayNumber++;
                            }

                            // get extra info if necessary
                            String extraInfo = "";

                            int indexOfReplacementInfo = line.indexOf(" - ")+3;
                            String replacementInfo = line.substring(indexOfReplacementInfo);

                            String[] allPossibleReplacementInfosOfNoLesson = {
                                    "Uczniowie przychodzą później",
                                    "Uczniowie zwolnieni do domu",
                                    "Okienko dla uczniów",
                                    "Bez konsekwencji"
                            };

                            boolean foundNoLesson = false;
                            for (String text : allPossibleReplacementInfosOfNoLesson) {
                                if(replacementInfo.contains(text)) foundNoLesson = true;
                            }

                            if(!foundNoLesson) {
                                extraInfo = replacementInfo;
                            }

                            ReplacementToTimetable replacement = new ReplacementToTimetable(lessonNumber, dayNumber, groupNumber, extraInfo);
                            replacementDataForTimetable.add(replacement);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public String getAllReplacements() {
        return allReplacements;
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
        if(td.text().length() > 1) return false;
        return true;
    }

    public List<String> getReplacementsForSearch() {
        return replacementsForSearch;
    }

    public List<ReplacementToTimetable> getReplacementDataForTimetable() {
        return replacementDataForTimetable;
    }
}
