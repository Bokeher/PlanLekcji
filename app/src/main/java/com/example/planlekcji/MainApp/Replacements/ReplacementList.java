package com.example.planlekcji.MainApp.Replacements;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    public HashMap<Integer, List<Integer>> getReplacementInfo() {
        List<String> list = new ArrayList<>();
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
        Log.e("zastepstwa", map.toString());

        return map;
    }

    @Override
    public String toString() {
        String res = "";
        for (Replacement replacement : replacementList) {
            res += replacement.toString()+"\n\n";
        }
        return res;
    }
}