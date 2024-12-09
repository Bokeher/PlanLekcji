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

        // document headers '\n \n' replacement1 '\n \n' replacement2 ...
        ArrayList<String> temp = new ArrayList<>(Arrays.asList(rawText.split("\n \n")));
        temp.remove(0); // remove first element containing unnecessary info

        for(String replacement : temp) {
            String[] lines = replacement.split("\n");

            lines[1] = ""; // remove table headers

            String newReplacement = String.join("<br>", lines); // add <br>s
            newReplacement = newReplacement.replaceAll(" {2,}", " "); // replace all consecutive spaces into one space

            replacements.add(newReplacement);
        }
    }

}
