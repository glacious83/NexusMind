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

    public ImprovementAgent(CheckpointManager checkpointManager, RepoManager repoManager) {
        this.checkpointManager = checkpointManager;
        this.repoManager = repoManager;
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

            // Generate a safe, focused prompt
            List<String> dependencies = repoManager.findRelatedDependencies(nextFile); // (Optional: implement this later)
            String prompt = promptBuilder.buildPromptForFile(nextFile, dependencies);

            // Send prompt to ChatGPT
            aiCommunicator.sendPromptAutomatically("Improve Java Class", prompt);

            chatReader.openExistingSession();
            System.out.println("Waiting for AI response (45 seconds)...");
            try {
                Thread.sleep(45000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            String improvedCode = chatReader.fetchLatestCodeBlock();
            chatReader.close();

            if (improvedCode == null) {
                System.err.println("Failed to retrieve improved code for file: " + nextFilePath);
                break;
            }

            // Save improved file
            try (FileWriter writer = new FileWriter(nextFile)) {
                writer.write(improvedCode);
            } catch (IOException e) {
                System.err.println("Error writing improved file: " + nextFilePath);
            }

            improvedFiles.add(nextFilePath);
            lastProcessed = nextFilePath;
            filesProcessed++;
        }

        if (!improvedFiles.isEmpty()) {
            checkpointManager.saveCheckpoint(lastProcessed, iteration + 1);
        }
    }
}
