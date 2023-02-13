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
            final int[] numbers = {1, 2, 3, 4, 5, 6, 7, 8 ,9 , 10, 11, 12};
            final String classToken = "4 PTN";

            Document doc = Jsoup.connect("http://zastepstwa.ckziu-elektryk.pl/").get();

            Elements tds = doc.select("table tr td");
            Elements teachers = doc.select(".st1");

            ArrayList<String> teacherList = new ArrayList<String>();
            for (Element teacher : teachers) {
                teacherList.add(teacher.text());
            }

            boolean singleDay = true;

            int year = Calendar.getInstance().get(Calendar.YEAR);
            String st0 = doc.select(".st0").text();
            if(st0.split(String.valueOf(year)).length == 3){
                singleDay = false;
            }

            String teacherTemp = "";
            ArrayList<String> res = new ArrayList<String>();
            if(singleDay) res.add(""+st0);

            for(int i=0; i<tds.size(); i++) {
                String td = tds.get(i).text();
                Element tdElem = tds.get(i);
                //get teacher temp
                singleDay = true;
                if(singleDay){
                    if(teacherList.contains(td)) {
                        teacherTemp = td;
                    }
                }else{
                    if(td.contains("/")){
                        teacherTemp = td;
                    }
                }

                String classNumber = classToken.substring(0, 1);
                if(td.contains(classToken) || (td.contains("_roz_kl"+classNumber) && !classNumber.equals("4"))) {
                    String trOfData = tdElem.parent().text();
                    res.add("\n"+teacherTemp);
                    res.add(trOfData.substring(0, 1)+" | "+trOfData.substring(2));
                }

            }
//            System.out.println(res);

            data = String.join("\n", removeDuplicates(res));
            if(!singleDay) data = data.substring(1);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public String getData() {
        return data;
    }

    private static <T> ArrayList<T> removeDuplicates(ArrayList<T> list) {
        ArrayList<T> newList = new ArrayList<T>();

        for (T element : list) {
            if (!newList.contains(element)) {
                newList.add(element);
            }
        }

        return newList;
    }
}
