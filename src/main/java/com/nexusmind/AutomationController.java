package com.nexusmind;

public class AutomationController {

    public static void main(String[] args) {
        System.out.println("Starting NexusMind Automation...");

        // Analyze Repo Structure
        RepoAnalyzer analyzer = new RepoAnalyzer("C:\\Users\\mmamouze\\IdeaProjects\\NexusMind");
        String projectSummary = analyzer.generateProjectSummary();
        System.out.println("Project structure summary generated.");

        // Plan Improvements with AI
        AIPlanner planner = new AIPlanner();
        String improvementSuggestions = planner.generateImprovementSuggestions(projectSummary);
        System.out.println("Improvement suggestions generated.");

        // Create GitHub Issues from Suggestions
        GitHubIssueManager issueManager = new GitHubIssueManager();

        // Basic split by lines - assuming ChatGPT returns action items line by line
        String[] suggestions = improvementSuggestions.split("\n");
        for (String suggestion : suggestions) {
            if (!suggestion.trim().isEmpty()) {
                issueManager.createIssue(suggestion.trim(), "Auto-generated improvement suggestion by NexusMind AI.");
            }
        }

        System.out.println("All suggested issues created successfully.");
    }
}
