package com.example.test2;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReplacementList {
    final String[] dayNames = {"poniedziałek", "wtorek", "środa", "czwartek", "piątek"};
    private List<Replacement> replacementList = new ArrayList<>();

    public void add(Replacement replacement) {
        replacementList.add(replacement);
    }

    public Replacement get(int i) {
        return replacementList.get(i);
    }

    public List<Replacement> getReplacementList() {
        return replacementList;
    }

    public String getReplacementInfo() {
        getDataNeededFromReplacementsToTimetable();
        return "d";
    }
    private List<String> getDataNeededFromReplacementsToTimetable() {
        List<String> list = new ArrayList<>();
        final String[] dayNames = {"poniedziałek", "wtorek", "środa", "czwartek", "piątek"};
        List<String> arrayData = new ArrayList<>();

        HashMap<Integer, List<Integer>> map = new HashMap<>();
        for (int i = 0; i < replacementList.size(); i++) {
            Replacement replacement = replacementList.get(i);
            int dayNumb = 0;

            String phrase = "";
            if(replacement.singleDay) {
                phrase = replacement.getTitle();
            } else {
                phrase = replacement.getTeacher();
            }

            for (int j = 0; j < dayNames.length; j++) {
                if(phrase.contains(dayNames[j])) {
                    dayNumb = j+1;
                }
            }

            List<Integer> lessonNumbs = new ArrayList<>();
            for (int j = 0; j < replacement.getLesson().size(); j++) {
                String lesson = replacement.getLesson().get(j);

                //in case lessonNumber >= 10
                String toInt = ""+lesson.charAt(0);
                if(lesson.charAt(1) != ' ') toInt += lesson.charAt(1);

                int lessonNumb = Integer.parseInt(toInt);
                lessonNumbs.add(lessonNumb);
            }

            if(map.get(dayNumb) == null) map.put(dayNumb, lessonNumbs);
            else {
                List<Integer> mainList = map.get(dayNumb);
                mainList.addAll(lessonNumbs);
                map.put(dayNumb, mainList);
            }
        }
        Log.e("xd2", map.toString());

//        if (replacementsData.startsWith("Zastępstwa w dniu")) arrayData.add(replacementsData);
//        else arrayData = Arrays.asList(replacementsData.split("\n\n"));
//
//        for (String replacement : arrayData) {
//            for (String dayName : dayNames) {
//                if (replacement.contains(dayName)) {
//                    int dayNumb = dayNameToIntValue(dayName);
//                    String lessonNumbers = getLessonNumberFromReplacement(replacement);
//                    list.add(lessonNumbers+";"+dayNumb);
//                }
//            }
//        }
        return null;
    }
    private String getLessonNumberFromReplacement(String text) {
        Pattern pattern = Pattern.compile("([0-9]|[0-9]) \\| ");
        Matcher matcher = pattern.matcher(text);

        ArrayList<String> lessonNumbers = new ArrayList<String>();
        while (matcher.find()) {
            int startingIndex = matcher.start();
            char ch = text.charAt(startingIndex);
            String number = String.valueOf(ch);
            lessonNumbers.add(number);
        }

        return String.join(",", lessonNumbers);
    }
//    private int dayNameToIntValue(String dayName) {
//        for (int i = 0; i < dayNames.length; i++) {
//            if(dayName.equals(dayNames[i])) return i+1;
//        }
//        return 0;
//    }
}
