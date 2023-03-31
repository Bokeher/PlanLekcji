package com.example.planlekcji.MainApp.Replacements;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.planlekcji.MainApp.MainActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class GetReplacementsData implements Runnable {
    private String data;
    private ReplacementList replacementList = new ReplacementList();

    @Override
    public void run() {
        try {
            Context context = MainActivity.getContext();
            SharedPreferences sharedPreferences = context.getSharedPreferences("classToken", 0);
            String classToken = sharedPreferences.getString("classToken", "4 PTN");

            Document doc = Jsoup.connect("http://zastepstwa.ckziu-elektryk.pl/").get();

            Elements tds = doc.select("table tr td");
            Elements teachers = doc.select(".st1");

            ArrayList<String> teacherList = new ArrayList<String>();
            for(Element teacher : teachers) teacherList.add(teacher.text());

            boolean singleDay = true;
            String titleOfReplacements = doc.select(".st0").text();
            int year = Calendar.getInstance().get(Calendar.YEAR);
            /**
             * Sprawdza czy zastepstwa sa w formacie jednego dnia lub paru
             * W przypadku jednego dnia jest inna forma w wierszach zawierajacych informacje o nauczycielach (st1) oraz naglowku (st0),
             * Gdy jest pare dni to st0 zawiera dwa razy aktualny rok tzn. zawiera np. tekst: "Zastępstwa w dniu 2023-02-27 - 2023-03-03"
             */
            if(titleOfReplacements.split(String.valueOf(year)).length == 3) singleDay = false;

            String teacherTemp = "";
            ArrayList<String> res = new ArrayList<String>();

            for(int i = 0; i < tds.size(); i++) {
                String td = tds.get(i).text();
                Element tdElem = tds.get(i);

                if(teacherList.contains(td)) {
                    teacherTemp = td;
                }

                String classNumber = classToken.substring(0, 1);
                if(td.contains(classToken) || (td.contains("_roz_kl"+classNumber) && !classNumber.equals("4"))) {
                    String trOfData = tdElem.parent().text();
                    res.add("\n"+teacherTemp);
                    res.add(trOfData.substring(0, 1)+" | "+trOfData.substring(2));
                }
            }

            data = String.join("\n", removeDuplicates(res));
            if(!singleDay) data = data.substring(1);
//            else if(res.size() > 0) data = titleOfReplacements+data;

            String[] arr = data.split("\n\n");

            for (int i = 0; i < arr.length; i++) {
                String[] arr2 = arr[i].split("\n");
                int startingIndex = 1;

                String teacher = arr2[0];

                if(i == 0 && singleDay) {
                    startingIndex = 2;
                    teacher = arr2[1];
                }

                arr2 = Arrays.copyOfRange(arr2, startingIndex, arr2.length);

                Replacement replacement = new Replacement(titleOfReplacements, teacher, Arrays.asList(arr2), singleDay);
                replacementList.add(replacement);
                Log.e("Zastepstwo", replacement.toString());
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public String getData() {
        return data;
    }

    public ReplacementList getReplacementList() {
        return replacementList;
    }

    private static <T> ArrayList<T> removeDuplicates(ArrayList<T> list) {
        ArrayList<T> newList = new ArrayList<T>();

        for(T element : list) {
            if(!newList.contains(element)) {
                newList.add(element);
            }
        }

        return newList;
    }
}
