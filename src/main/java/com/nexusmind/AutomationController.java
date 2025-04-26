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
                String priority = planner.determinePriority(suggestion);
                String formattedBody = String.format(
                        "### Overview\nAuto-suggested improvement by NexusMind AI.\n\n### Suggested Improvement\n%s\n\n### Priority\n%s\n\n### Estimated Complexity\nMedium",
                        suggestion.trim(), priority
                );
                issueManager.createIssue(suggestion.trim(), formattedBody);

            }
        }

        System.out.println("All suggested issues created successfully.");
    }
}
