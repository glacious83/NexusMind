package com.nexusmind;

import java.util.Arrays;
import java.util.List;

public class NexusMindOrchestrator {

    private final RepoManager repoManager;
    private final CheckpointManager checkpointManager;
    private final ImprovementAgent improvementAgent;
    private final GitManager gitManager;
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
        this.improvementAgent = new ImprovementAgent(checkpointManager, repoManager);
        this.gitManager = new GitManager(repoManager.getLocalPath());
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

                // Step 3: Push improvements
                gitManager.addCommitPush(
                        "AI improved batch with smarter prompts and safe improvements"
                );

                // Step 4: Plan further evolution
                String projectSummary = structureMapper.generateProjectStructure();
                String suggestions = aiPlanner.generateImprovementSuggestions(projectSummary);

                if (suggestions != null && !suggestions.isBlank()) {
                    List<String> suggestionList = Arrays.asList(suggestions.split("\n"));
                    System.out.println("Received " + suggestionList.size() + " suggestions from AI.");

                    // Step 5: Create GitHub issues for evolution tasks
                    for (String suggestion : suggestionList) {
                        if (!suggestion.trim().isEmpty()) {
                            issueManager.createIssue(
                                    suggestion.length() > 250 ? suggestion.substring(0, 250) + "..." : suggestion.trim(),
                                    suggestion.trim()
                            );
                        }
                    }

                    // Step 6: Create new feature agents based on suggestions
                    featureCreatorAgent.createFeaturesFromSuggestions(suggestionList);

                    // Step 7: Push created new features
                    gitManager.addCommitPush(
                            "AI created new autonomous feature modules based on self-evolution planning"
                    );
                }

                Notifier.sendSuccess("NexusMind successfully completed an evolution cycle.");

                System.out.println("\nCycle completed. Sleeping for 1 minute...");
                Thread.sleep(60 * 1000);

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
