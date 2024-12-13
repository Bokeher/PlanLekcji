package com.example.planlekcji.replacements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReplacementDataProcessor {
    private final String rawReplacements;
    private final List<String> replacements = new ArrayList<>();

    public List<String> getReplacements() {
        return replacements;
    }

    public ReplacementDataProcessor(String rawReplacements) {
        this.rawReplacements = rawReplacements;
    }

    public void process() {
        // document headers '\n \n' replacement1 '\n \n' replacement2 ...
        ArrayList<String> temp = new ArrayList<>(Arrays.asList(rawReplacements.split("\n \n")));
        temp.remove(0); // remove first element containing unnecessary info

        for(String replacement : temp) {
            String[] lines = replacement.split("\n");

            // remove table headers
            for(int i = 0; i < lines.length; i++) {
                if (lines[i].contains("LP. ") || lines[i].length() < 8) {
                    lines[i] = "";
                }
            }

            String newReplacement = String.join("<br>", lines); // add <br>s
            newReplacement = newReplacement.replaceAll(" {2,}", " "); // replace all consecutive spaces into one space

            replacements.add(newReplacement);
        }
    }

}
