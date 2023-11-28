package com.example.planlekcji.MainApp.Timetable;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessTimetableData {
    private List<LessonRow> lessonRows = new ArrayList<>();

    // Constructor that takes a Document as a parameter
    public ProcessTimetableData(Document document) {
        processDocument(document);
    }

    // Method to process the downloaded document
    private void processDocument(Document doc) {
        // Select all rows in the timetable table
        Elements trs = doc.select(".tabela tr");

        trs.remove(0);

        for (Element tr: trs) {
            LessonRow lessonRow = createLessonRow(tr);
            lessonRows.add(lessonRow);
        }
    }

    private LessonRow createLessonRow(Element tr) {
        Map<Integer, String> dayElements = new HashMap<>();

        int[] dayNumbs = { LessonRow.MONDAY, LessonRow.TUESDAY, LessonRow.WEDNESDAY, LessonRow.THURSDAY, LessonRow.FRIDAY };
        for (int dayNumb : dayNumbs) {
            String searchingPhrase = "td:nth-child(" + (dayNumb+2) + ")";
            dayElements.put(dayNumb, tr.selectFirst(searchingPhrase).html());
        }

        return new LessonRow(
            tr.selectFirst("td:nth-child(1)").text(),
            tr.selectFirst("td:nth-child(2)").text(),
            dayElements
        );
    }

    public List<LessonRow> getLessonRows() {
        return lessonRows;
    }
}
