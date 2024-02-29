package com.example.planlekcji.Tools;
import java.util.HashMap;
import java.util.Map;

public class BoyerMooreSearch {
    private static void preProcessPattern(char[] pattern, Map<Character, Integer> shift) {
        int m = pattern.length;
        for (int i = 0; i < m; i++) {
            shift.put(pattern[i], m - i - 1);
        }
    }

    public static boolean search(String text, String pattern) {
        int n = text.length();
        int m = pattern.length();
        Map<Character, Integer> shift = new HashMap<>();
        preProcessPattern(pattern.toCharArray(), shift);
        int s = 0;
        while (s <= n - m) {
            int j = m - 1;
            while (j >= 0 && pattern.charAt(j) == text.charAt(s + j))
                j--;
            if (j < 0) {
                return true;
            } else {
                char mismatchedChar = text.charAt(s + j);
                Integer charShift = shift.getOrDefault(mismatchedChar, m);
                s += Math.max(1, j - charShift);
            }
        }
        return false;
    }
}
