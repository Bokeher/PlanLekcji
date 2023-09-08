package com.example.planlekcji.MainApp.Replacements;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetReplacementsData implements Runnable {
    private String allReplacements = "";
    private List<String> replacementsForSearch = new ArrayList<>();

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

            String[] data = allReplacements.split("<br><br>");
            for (String singleData : data) {
                if(singleData == data[0]) continue;
                replacementsForSearch.add(singleData);
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
