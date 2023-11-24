package com.example.planlekcji.MainApp.Timetable;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class ProcessTimetableData {
    private Lessons lessons;

    // Constructor that takes a Document as a parameter
    public ProcessTimetableData(Document document) {
        processDocument(document);
    }

    // Method to process the downloaded document
    private void processDocument(Document doc) {

        // Select all rows in the timetable table
        Elements trs = doc.select(".tabela tr");

        // Initialize ArrayLists for each day of the week and other timetable data
        ArrayList<Elements> monday = new ArrayList<>();
        ArrayList<Elements> tuesday = new ArrayList<>();
        ArrayList<Elements> wednesday = new ArrayList<>();
        ArrayList<Elements> thursday = new ArrayList<>();
        ArrayList<Elements> friday = new ArrayList<>();
        ArrayList<Elements> lessonNumbers = new ArrayList<>();
        ArrayList<Elements> lessonHours = new ArrayList<>();

        // Iterate through each row in the table
        for (int i = 1; i < trs.size(); i++) {
            Element tr = trs.get(i);

            // Extract and add timetable data for each day of the week
            lessonNumbers.add(tr.select("td:nth-child(1)"));
            lessonHours.add(tr.select("td:nth-child(2)"));
            monday.add(tr.select("td:nth-child(3)"));
            tuesday.add(tr.select("td:nth-child(4)"));
            wednesday.add(tr.select("td:nth-child(5)"));
            thursday.add(tr.select("td:nth-child(6)"));
            friday.add(tr.select("td:nth-child(7)"));
        }

        // Create a Lessons object with the extracted data
        lessons = new Lessons(lessonNumbers, lessonHours, monday, tuesday, wednesday, thursday, friday);
    }

    // Getter method to retrieve the processed Lessons object
    public Lessons getLessons() {
        return lessons;
    }
}
