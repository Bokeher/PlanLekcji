package com.example.planlekcji.replacements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReplacementDataProcessor {
    private final String document;
    private List<String> replacements = new ArrayList<>();

    public List<String> getReplacements() {
        return replacements;
    }

    public ReplacementDataProcessor(String document) {
        this.document = document;
    }

    public void process() {
        String rawText = document;

        // Find index of first word after skipped 14 words
        // These 14 words are unnecessary for this processing
        int index = -1;
        for (int i = 0; i < 14; i++) {
            index = rawText.indexOf(" ", index + 1);
            if (index == -1) break;
        }

        // +3 to skip first number, so theres no empty element at first index
        rawText = rawText.substring(index + 3);

        // "FirstName SecondName extraInfo 0 FirstName ... " => [FirstName SecondName extraInfo, FirstName ...]
        String[] replacementsArray = rawText.split(" \\d ");
        replacements = Arrays.asList(replacementsArray);
    }

}
