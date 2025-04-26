package com.nexusmind;

public class SimpleJavaValidator {

    public static boolean isValidJavaClass(String javaContent) {
        if (javaContent == null || javaContent.isEmpty()) {
            return false;
        }

        javaContent = javaContent.trim();

        // Basic structural checks
        if (!javaContent.startsWith("package ") && !javaContent.contains("public class") && !javaContent.contains("class ")) {
            System.out.println("[Validator] Missing package declaration or class definition.");
            return false;
        }

        int openBraces = countChar(javaContent, '{');
        int closeBraces = countChar(javaContent, '}');

        if (openBraces != closeBraces) {
            System.out.println("[Validator] Mismatch between { and } braces. Open: " + openBraces + ", Close: " + closeBraces);
            return false;
        }

        if (!javaContent.endsWith("}")) {
            System.out.println("[Validator] Java class does not properly close with }");
            return false;
        }

        return true;
    }

    private static int countChar(String input, char target) {
        int count = 0;
        for (char c : input.toCharArray()) {
            if (c == target) {
                count++;
            }
        }
        return count;
    }
}
