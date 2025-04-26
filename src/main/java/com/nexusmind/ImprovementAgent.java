package com.nexusmind;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class ImprovementAgent {

    private final CheckpointManager checkpointManager;
    private final RepoManager repoManager;
    private final AICommunicator aiCommunicator;
    private final ChatReader chatReader;

    public ImprovementAgent(CheckpointManager checkpointManager, RepoManager repoManager) {
        this.checkpointManager = checkpointManager;
        this.repoManager = repoManager;
        this.aiCommunicator = new AICommunicator();
        this.chatReader = new ChatReader();
    }

    public void improveNextFile() {
        String lastFile = checkpointManager.getLastProcessedFile();
        String nextFilePath = repoManager.getNextFileToProcess(lastFile);

        if (nextFilePath == null) {
            System.out.println("No more files to process.");
            return;
        }

        File nextFile = new File(nextFilePath);
        try {
            String content = new String(Files.readAllBytes(nextFile.toPath()));

            aiCommunicator.sendPromptAutomatically(nextFile.getName(), content);

            chatReader.openExistingSession();

            System.out.println("Waiting for AI response (30 seconds)...");
            Thread.sleep(30000); // Give ChatGPT time to respond

            String improvedCode = chatReader.fetchLatestCodeBlock();
            chatReader.close();

            if (improvedCode != null && !improvedCode.isBlank()) {
                Files.write(nextFile.toPath(), improvedCode.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
                checkpointManager.saveCheckpoint(nextFilePath, checkpointManager.getIteration() + 1);
                System.out.println("Improved file saved and checkpoint updated.");
            } else {
                System.err.println("Failed to retrieve improved code.");
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Error improving file: " + e.getMessage());
        }
    }
}
