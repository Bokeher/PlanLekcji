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
import java.util.List;

public class GetReplacementsData implements Runnable {
    private String data;
    private ReplacementList replacementList = new ReplacementList();

    @Override
    public void run() {
        try {
            Context context = MainActivity.getContext();
            SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPrefs", 0);
            String classToken = sharedPreferences.getString("classToken", "4 PTN");

            Document doc = Jsoup.connect("http://zastepstwa.ckziu-elektryk.pl/").get();

            Elements tds = doc.select("table tr td");
            Elements teachers = doc.select(".st1");

            ArrayList<String> teacherNames = new ArrayList<String>();
            for(Element teacher : teachers) teacherNames.add(teacher.text());

            /**
             * Sprawdza czy zastępstwa są w formacie jednego dnia lub paru.
             * W przypadku jednego dnia jest inna forma danych w wierszach zawierajacych informacje o nauczycielach (st1) oraz nagłówku (st0).
             * Gdy jest parę dni to st0 zawiera dwa razy aktualny rok tzn. zawiera np. tekst: "Zastępstwa w dniu 2023-02-27 - 2023-03-03".
             */
            boolean singleDay = true;
            String titleOfReplacements = doc.select(".st0").text();
            int year = Calendar.getInstance().get(Calendar.YEAR);
            if(titleOfReplacements.split(String.valueOf(year)).length == 3) {
                singleDay = false;
            }

            String currentTeacherName = "";
            ArrayList<String> res = new ArrayList<>();

            /**
             * Sposób działania:
             *  1. Zapisuje dane nauczyciela.
             *  2. Sprawdza, czy dla danego nauczyciela są jakieś zastępstwa dla sprawdzanej klasy.
             *  3. Jeśli tak, zapisuje dane.
             *  4. Przechodzi do następnego nauczyciela.
             */
            for (int i = 0; i < tds.size(); i++) {
                String td = tds.get(i).text();
                Element tdElem = tds.get(i);

                // Sprawdza, czy dany wiersz zawiera nazwę nauczyciela i zapisuje ją w zmiennej currentTeacherName.
                if (teacherNames.contains(td)) {
                    currentTeacherName = td;
                }

                // Sprawdza, czy w danym wierszu znajdują się informacje o zastępstwie dla sprawdzanej klasy.
                String classNumber = classToken.substring(0, 1);
                if (td.contains(classToken) || (td.contains("_roz_kl" + classNumber) )) {
                    //&& !classNumber.equals("4")
                    // Jeśli tak, zapisuje informacje o zastępstwie.

                    String rowData = tdElem.parent().text();
                    res.add("\n" + currentTeacherName);
                    res.add(rowData.substring(0, 1) + " | " + rowData.substring(2));
                }
            Log.e("naprawa", res.toString());
            }

            // Łączy informacje o zastępstwach i usuwa duplikaty.
            data = String.join("\n", removeDuplicates(res));

            if(!singleDay) data = data.substring(1);
            else if(res.size() > 0) data = titleOfReplacements+data;

            // Dzieli dane o zastępstwach na oddzielne dni i przetwarza każdy dzień z osobna.
            String[] arr = data.split("\n\n");
            for (int i = 0; i < arr.length; i++) {
                String[] arr2 = arr[i].split("\n");
                int startingIndex = 1;

                // W przypadku zastępstw na jeden dzień, nazwę nauczyciela znajdujemy w pierwszym wierszu.
                String teacher = arr2[0];

                if(i == 0 && singleDay) {
                    startingIndex = 2;
                    teacher = arr2[1];
                }

                // Usuwa wiersz z nazwą nauczyciela, aby pozostałe informacje były łatwiejsze do przetworzenia.
                arr2 = Arrays.copyOfRange(arr2, startingIndex, arr2.length);

                // Tworzy nowy obiekt Replacement dla każdego dnia i dodaje go do listy zastępstw.
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

    private static List<String> removeDuplicates(ArrayList<String> list) {
        List<String> newList = new ArrayList<String>();

        for(String element : list) {
            if(!newList.contains(element) || element.charAt(2) == '|') {
                newList.add(element);
            }
        }

        return newList;
    }
}
