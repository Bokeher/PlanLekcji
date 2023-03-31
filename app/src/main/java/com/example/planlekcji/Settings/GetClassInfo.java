package com.example.planlekcji.Settings;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GetClassInfo implements Runnable {
    private List<ClassInfo> classInfoList = new ArrayList<>();

    @Override
    public void run() {
        Log.e("set", "test2");

        try {
            Document doc = Jsoup.connect("http://plan.ckziu-elektryk.pl/lista.html").get();
            Elements classes = doc.select("#oddzialy .el");

            for (Element classData : classes) {
                // get class token
                String token = classData.text();
                token = token.substring(0, 1) + " " + token.substring(1);

                // get url of timetable
                String href = classData.selectFirst("[href]").attr("href");
                int index = href.indexOf("/");

                String url = "http://plan.ckziu-elektryk.pl/plany/"+href.substring(index+1);
                Log.e("set", url);

                // create ClassInfo Object and add to list
                ClassInfo classInfo = new ClassInfo(token, url);
                classInfoList.add(classInfo);
            }

            classInfoList.sort(Comparator.comparing(ClassInfo::getToken));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public List<ClassInfo> getClassInfoList() {
        return classInfoList;
    }
}
