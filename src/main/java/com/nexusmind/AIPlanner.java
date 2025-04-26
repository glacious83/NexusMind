package com.nexusmind;

public class AIPlanner {

    private final AICommunicator aiCommunicator;
    private final ChatReader chatReader;

    public AIPlanner() {
        this.aiCommunicator = new AICommunicator();
        this.chatReader = new ChatReader();
    }

    public String generateImprovementSuggestions(String projectSummary) {
        String prompt = "Given this Java project structure, suggest technical improvements, missing best practices, possible refactorings, and new features. List them clearly and briefly. Structure them as action items.\n\nProject Structure:\n" + projectSummary;

        aiCommunicator.sendPromptAutomatically("Project Improvement Suggestions", prompt);

        chatReader.openExistingSession();

        System.out.println("Waiting for AI response (45 seconds)...");
        try {
            Thread.sleep(45000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String suggestions = chatReader.fetchLatestCodeBlock(); // ChatGPT response expected as a code block
        chatReader.close();

        return suggestions;
    }

}
