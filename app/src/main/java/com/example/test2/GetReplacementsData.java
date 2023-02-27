package com.example.test2;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;

public class GetReplacementsData implements Runnable {
    private String data;
    private int dayNumb; // in case there are only singular day replacements

    @Override
    public void run() {
        try {
            final String classToken = "4 PTN";

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

            for(int i=0; i<tds.size(); i++) {
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
            else if(res.size() > 0) data = titleOfReplacements+data;
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public String getData() {
        return data;
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
