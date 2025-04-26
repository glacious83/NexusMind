package com.nexusmind;

public class AIPlanner {

    private final AICommunicator aiCommunicator;
    private final ChatReader chatReader;

    public AIPlanner() {
        this.aiCommunicator = new AICommunicator();
        this.chatReader = new ChatReader();
    }

    public String generateImprovementSuggestions(String projectSummary) {
        String prompt = """
            You are not only tasked with improving existing code.

            Your true goal is to evolve this project into a highly intelligent autonomous AI engineer.

            You should:
            - Propose missing classes, modules, and services that would create new AI capabilities.
            - Design systems for self-improvement, self-repair, self-expansion, and self-learning.
            - Think proactively about the architecture needed for an AI to create new AIs.
            - Suggest new packages, directories, service layers, and intelligent agents.
            - Improve code quality, but also expand the project into a real thinking machine.

            Focus on evolution, creativity, scalability, and recursive improvement.

            Base your suggestions on the following current project structure:

            %s
            """.formatted(projectSummary);

        aiCommunicator.sendPromptAutomatically("Project Evolution Planning", prompt);

        chatReader.openExistingSession();

        System.out.println("Waiting for AI response (45 seconds)...");
        try {
            Thread.sleep(45000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String suggestions = chatReader.fetchLatestCodeBlock();
        chatReader.close();

        return suggestions;
    }
}
