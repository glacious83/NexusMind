package com.nexusmind;

import java.util.Arrays;
import java.util.List;

public class NexusMindOrchestrator {

    private final RepoManager repoManager;
    private final CheckpointManager checkpointManager;
    private final GitManager gitManager;
    private final ImprovementAgent improvementAgent;
    private final ProjectStructureMapper structureMapper;
    private final AIPlanner aiPlanner;
    private final FeatureCreatorAgent featureCreatorAgent;
    private final GitHubIssueManager issueManager;

    public NexusMindOrchestrator() {
        this.repoManager = new RepoManager(
                "https://github.com/glacious83/NexusMind",
                "C:/Users/mmamouze/IdeaProjects/NexusMind"
        );
        this.checkpointManager = new CheckpointManager();
        this.gitManager = new GitManager(repoManager.getLocalPath());
        this.improvementAgent = new ImprovementAgent(checkpointManager, repoManager, gitManager);
        this.structureMapper = new ProjectStructureMapper(repoManager.getLocalPath());
        this.aiPlanner = new AIPlanner();
        this.featureCreatorAgent = new FeatureCreatorAgent(repoManager, checkpointManager);
        this.issueManager = new GitHubIssueManager();
    }

    public void startAutomationCycle() {
        while (true) {
            try {
                System.out.println("\n==== NexusMind New Cycle Started ====");

                // Step 1: Update repo
                repoManager.updateRepo();

                // Step 2: Improve next batch
                improvementAgent.improveNextFiles(5);

                // Step 3: Plan further evolution
                String projectSummary = structureMapper.generateProjectStructure();
                String suggestions = aiPlanner.generateImprovementSuggestions(projectSummary);

                if (suggestions != null && !suggestions.isBlank()) {

                    // Check if suggestions are Java code or real improvements
                    if (suggestions.contains("package ") || suggestions.contains("public class") || suggestions.contains("{")) {
                        System.out.println("[Warning] AI returned Java code instead of suggestions. Skipping feature creation.");
                    } else {
                        List<String> suggestionList = Arrays.asList(suggestions.split("\n"));
                        System.out.println("Received " + suggestionList.size() + " suggestions from AI.");

                        for (String suggestion : suggestionList) {
                            suggestion = suggestion.trim();
                            if (suggestion.isEmpty()) continue;
                            if (suggestion.length() < 5) continue;

                            issueManager.createIssue(
                                    suggestion.length() > 250 ? suggestion.substring(0, 250) + "..." : suggestion,
                                    suggestion
                            );
                        }

                        featureCreatorAgent.createFeaturesFromSuggestions(suggestionList);

                        // After feature creation, manually commit new classes
                        gitManager.addCommitPush("AI created new autonomous feature modules: " + String.join(", ", suggestionList));
                    }
                }

                Notifier.sendSuccess("NexusMind successfully completed an evolution cycle.");

                System.out.println("\nCycle completed. Sleeping for 1 hour...");
                Thread.sleep(60 * 60 * 1000);

            } catch (Exception e) {
                String errorMessage = "Critical Error during cycle: " + e.getMessage();
                System.err.println(errorMessage);
                Notifier.sendError(errorMessage);
                try {
                    Thread.sleep(10 * 60 * 1000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
