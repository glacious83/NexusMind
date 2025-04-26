package com.nexusmind;

public class AICommunicator {

    public void displayPromptForUser(String fileName, String fileContent) {
        System.out.println("\n\n--- AI Communication ---");
        System.out.println(">> Please copy the following prompt into ChatGPT manually:");
        System.out.println(">> File: " + fileName);
        System.out.println("---------------------------");
        System.out.println(generatePrompt(fileContent));
        System.out.println("---------------------------");
        System.out.println("After ChatGPT replies, manually paste the improved code back.");
    }

    private String generatePrompt(String fileContent) {
        return "You are a senior software engineer. Improve the following Java code for performance, readability, and optimization. Only return the improved code inside a single code block.\n\n" + fileContent;
    }
}
