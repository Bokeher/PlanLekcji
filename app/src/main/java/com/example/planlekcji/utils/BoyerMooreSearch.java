package com.example.planlekcji.utils;

import java.util.HashMap;
import java.util.Map;

public class BoyerMooreSearch {
    private final Map<Character, Integer> lastOccurrence;

    public BoyerMooreSearch() {
        lastOccurrence = new HashMap<>();
    }

    private void preprocessPattern(String pattern) {
        lastOccurrence.clear();
        for (int i = 0; i < pattern.length(); i++) {
            lastOccurrence.put(pattern.charAt(i), i);
        }
    }

    public boolean search(String text, String pattern) {
        int n = text.length();
        int m = pattern.length();
        preprocessPattern(pattern);

        int skip;
        for (int i = 0; i <= n - m; i += skip) {
            skip = 0;
            for (int j = m - 1; j >= 0; j--) {
                if (pattern.charAt(j) != text.charAt(i + j)) {
                    skip = Math.max(1, j - lastOccurrence.getOrDefault(text.charAt(i + j), -1));
                    break;
                }
            }
            if (skip == 0) return true;
        }
        return false;
    }
}
