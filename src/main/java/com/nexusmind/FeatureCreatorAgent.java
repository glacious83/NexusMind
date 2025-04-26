package com.nexusmind;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class FeatureCreatorAgent {

    private final RepoManager repoManager;
    private final CheckpointManager checkpointManager;

    public FeatureCreatorAgent(RepoManager repoManager, CheckpointManager checkpointManager) {
        this.repoManager = repoManager;
        this.checkpointManager = checkpointManager;
    }

    public void createFeaturesFromSuggestions(List<String> suggestions) {
        int createdCount = 0;

        for (String suggestion : suggestions) {
            String cleanedSuggestion = cleanSuggestionText(suggestion);

            if (isValidClassName(cleanedSuggestion)) {
                String className = extractClassName(cleanedSuggestion);
                if (className == null) continue;

                // Set target directory for new agents
                File targetDir = new File(repoManager.getLocalPath() + "/src/main/java/com/nexusmind/agents/");
                if (!targetDir.exists()) {
                    targetDir.mkdirs();
                }

                File newClassFile = new File(targetDir, className + ".java");

                if (newClassFile.exists()) {
                    System.out.println("Class already exists: " + className + ". Skipping...");
                    continue;
                }

                try (FileWriter writer = new FileWriter(newClassFile)) {
                    writer.write(generateClassSkeleton(className));
                    createdCount++;
                    System.out.println("Created new AI agent: " + className);
                } catch (IOException e) {
                    System.err.println("Failed to create feature class: " + className + " -> " + e.getMessage());
                }
            }
        }

        if (createdCount > 0) {
            checkpointManager.saveCheckpoint(null, checkpointManager.getIteration() + 1);
        }
    }

    private String cleanSuggestionText(String suggestion) {
        return suggestion.replaceAll("[^A-Za-z0-9 ]", "").trim();
    }

    private boolean isValidClassName(String text) {
        return text.toLowerCase().contains("agent") || text.toLowerCase().contains("service") || text.toLowerCase().contains("engine");
    }

    private String extractClassName(String text) {
        String[] words = text.split(" ");
        StringBuilder className = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                className.append(Character.toUpperCase(word.charAt(0)));
                className.append(word.substring(1).toLowerCase());
            }
        }
        return className.length() > 0 ? className.toString() : null;
    }

    private String generateClassSkeleton(String className) {
        return """
            package com.nexusmind.agents;

            public class %s {

                // TODO: Implement intelligent logic for %s

            }
            """.formatted(className, className);
    }
}
