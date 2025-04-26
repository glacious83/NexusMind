package com.nexusmind;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImprovementAgent {

    private final CheckpointManager checkpointManager;
    private final RepoManager repoManager;
    private final ProjectStructureMapper structureMapper;
    private final ImprovementPromptBuilder promptBuilder;
    private final AICommunicator aiCommunicator;
    private final ChatReader chatReader;
    private final GitManager gitManager;

    public ImprovementAgent(CheckpointManager checkpointManager, RepoManager repoManager, GitManager gitManager) {
        this.checkpointManager = checkpointManager;
        this.repoManager = repoManager;
        this.gitManager = gitManager;
        this.structureMapper = new ProjectStructureMapper(repoManager.getLocalPath());
        String projectStructure = structureMapper.generateProjectStructure();
        this.promptBuilder = new ImprovementPromptBuilder(projectStructure);
        this.aiCommunicator = new AICommunicator();
        this.chatReader = new ChatReader();
    }

    public void improveNextFiles(int batchSize) {
        String lastProcessed = checkpointManager.getLastProcessedFile();
        int iteration = checkpointManager.getIteration();

        List<String> improvedFiles = new ArrayList<>();
        List<String> commitMessages = new ArrayList<>();
        int filesProcessed = 0;

        while (filesProcessed < batchSize) {
            String nextFilePath = repoManager.getNextFileToProcess(lastProcessed);

            if (nextFilePath == null) {
                System.out.println("No more files to process.");
                break;
            }

            File nextFile = new File(nextFilePath);
            if (!nextFile.exists()) {
                System.err.println("File does not exist: " + nextFilePath);
                lastProcessed = nextFilePath;
                checkpointManager.saveCheckpoint(lastProcessed, iteration);
                continue;
            }

            System.out.println("Improving file: " + nextFilePath);

            List<String> dependencies = repoManager.findRelatedDependencies(nextFile);
            String prompt = promptBuilder.buildPromptForFile(nextFile, dependencies);

            aiCommunicator.sendPromptAutomatically("Improve Java Class and Provide Commit Message", prompt);

            chatReader.openExistingSession();
            System.out.println("Waiting for AI response (45 seconds)...");
            try {
                Thread.sleep(45000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            String fullResponse = chatReader.fetchLatestCodeBlock();
            chatReader.close();

            if (fullResponse == null) {
                System.err.println("Failed to retrieve improved code for file: " + nextFilePath);
                break;
            }

            String commitMessage = "AI Improvement"; // fallback
            int commitStart = fullResponse.indexOf("[COMMIT_MSG]");
            int commitEnd = fullResponse.indexOf("[/COMMIT_MSG]");

            if (commitStart != -1 && commitEnd != -1 && commitEnd > commitStart) {
                commitMessage = fullResponse.substring(commitStart + 12, commitEnd).trim();
                fullResponse = fullResponse.substring(0, commitStart).trim();
            }

            System.out.println("Extracted Commit Message: " + commitMessage);

            try (FileWriter writer = new FileWriter(nextFile)) {
                writer.write(fullResponse);
            } catch (IOException e) {
                System.err.println("Error writing improved file: " + nextFilePath);
            }

            improvedFiles.add(nextFilePath);
            commitMessages.add(commitMessage);

            lastProcessed = nextFilePath;
            filesProcessed++;
        }

        if (!improvedFiles.isEmpty()) {
            checkpointManager.saveCheckpoint(lastProcessed, checkpointManager.getIteration() + 1);

            String fullCommitMessage = String.join("\n", commitMessages);
            gitManager.addCommitPush(fullCommitMessage);
        }
    }
}
