//package com.example.planlekcji.MainApp.Replacements;
//
//import android.app.Activity;
//import android.widget.TextView;
//
//import com.example.planlekcji.R;
//
//import java.util.ArrayList;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//public class Replacements {
//    private ArrayList<String> replacementDataForTimetable = new ArrayList<String>();
//    private String data;
//    public Activity activity;
//
//    public Replacements(Activity activity) {
//        this.activity = activity;
//        GetReplacementsData getReplacementsData = new GetReplacementsData();
//        Thread thread = new Thread(getReplacementsData);
//        thread.start();
//        try {
//            thread.join();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//
//        data = getReplacementsData.getData();
//    }
//
//    public void getData() {
//        TextView textFieldReplacements = this.activity.findViewById(R.id.textView_replacements);
//        if (data == null || data.equals("")) textFieldReplacements.setText("Brak zastępstw");
//        else {
//            //set data to editText
//            textFieldReplacements.setText(data);
//
//            //get data needed for updating timetable
//            String[] arrayData = data.split("\n\n");
//            final String[] dayNames = {"poniedziałek", "wtorek", "środa", "czwartek", "piątek"};
//            for (String replacement : arrayData) {
//                for (String dayName : dayNames) {
//                    if (replacement.contains(dayName)) {
//                        int dayNumb = dayNameToIntValue(dayName);
//                        String lessonNumbers = getLessonNumberFromReplacement(replacement);
//                        replacementDataForTimetable.add(lessonNumbers + ";" + dayNumb);
//                    }
//                }
//            }
//        }
//    }
//
//    private int dayNameToIntValue(String dayName) {
//        final String[] dayNames = {"poniedziałek", "wtorek", "środa", "czwartek", "piątek"};
//
//        for(int i = 0; i < dayNames.length; i++) {
//            if(dayName.equals(dayNames[i])) return i+1;
//        }
//        return 0;
//    }
//
//    private String getLessonNumberFromReplacement(String text) {
//        Pattern pattern = Pattern.compile("([0-9]|[0-9]) \\| ");
//        Matcher matcher = pattern.matcher(text);
//
//        ArrayList<String> lessonNumbers = new ArrayList<String>();
//        while (matcher.find()) {
//            int startingIndex = matcher.start();
//            char ch = text.charAt(startingIndex);
//            String number = String.valueOf(ch);
//            lessonNumbers.add(number);
//        }
//
//        String res = String.join(",", lessonNumbers);
//
//        return res;
//    }
//}
