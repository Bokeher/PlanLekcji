package com.example.planlekcji.MainApp.Replacements;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.planlekcji.MainActivity;
import com.example.planlekcji.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetReplacementsData implements Runnable {
    private String allReplacements = "";
    private List<String> replacementsForSearch = new ArrayList<>();
//    private List<ReplacementDataForTimetable> replacementDataForTimetableArr = new ArrayList<>();

    @Override
    public void run() {
        try {
            Document doc = Jsoup.connect("http://zastepstwa.ckziu-elektryk.pl/").get();

            // get all data and teacher names
            Elements tds = doc.select("table tr td");
            Elements teachers = doc.select(".st1");

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

            // List<ResultMap<dayNumber, lessonNumbers>>
            List<Map<Integer, List<Integer>>> mapWithAllResults = new ArrayList<>();

            if(selectedTypeOfTimetableKey == 0 && replacementVisibilityOnTimetable && !classToken.equals("")) {
                List<String> res = new ArrayList<>();
                for (String teacherAndHisReplacements : data) {
                    String[] linesOfReplacement = teacherAndHisReplacements.split("<br>");
                    boolean addTeacher = false;
                    for (int i = 0; i < linesOfReplacement.length; i++) {
                        String line = linesOfReplacement[i];
                        if (line.contains(classToken)) {
                            res.add(line);
                            addTeacher = true;
                        }
                    }
                    if(addTeacher) {
                        String teacherName = linesOfReplacement[0];

                        // check if single day
                        // example data: Anna Czaińska / 2023-10-17 wtorek
                        boolean singleDay = !teacherName.contains("/");
                        Log.e("test1", "singleDay: "+singleDay);


                        Map<Integer, List<Integer>> resultMap = new HashMap<>();


                        if(singleDay) {

                        } else {
                            String[] arr = teacherName.split("/");
                            arr[1] = arr[1].substring(1);
                            arr[1] = arr[1].substring(0, arr[1].indexOf(" "));
                            String dateOfReplacement = arr[1];

                            int dayNumber = 0;
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            try {
                                Date date = dateFormat.parse(dateOfReplacement);
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(date);
                                dayNumber = calendar.get(Calendar.DAY_OF_WEEK);
                                System.out.println("Data utworzonego obiektu Calendar: " + calendar.getTime());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            dayNumber--; // mon -> 1, tue -> 2, etc.

                            //TODO:
//                            mapWithAllResults.add()

                            Log.e("Test2", ""+dayNumber);
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
}
