package com.nexusmind;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class ImprovementAgent {

    private final CheckpointManager checkpointManager;
    private final RepoManager repoManager;
    private final AICommunicator aiCommunicator;
    private final ChatReader chatReader;
    private final GitManager gitManager;

    public ImprovementAgent(CheckpointManager checkpointManager, RepoManager repoManager) {
        this.checkpointManager = checkpointManager;
        this.repoManager = repoManager;
        this.aiCommunicator = new AICommunicator();
        this.chatReader = new ChatReader();
        this.gitManager = new GitManager("https://github.com/glacious83/NexusMind");
    }

    public void improveNextFiles(int batchSize) {
        List<File> filesToProcess = new ArrayList<>();
        String lastFile = checkpointManager.getLastProcessedFile();

        for (int i = 0; i < batchSize; i++) {
            String nextFilePath = repoManager.getNextFileToProcess(lastFile);
            if (nextFilePath == null) {
                System.out.println("No more files to process.");
                break;
            }
            filesToProcess.add(new File(nextFilePath));
            lastFile = nextFilePath;
        }

        if (filesToProcess.isEmpty()) {
            System.out.println("No files collected for batch improvement.");
            return;
        }

        StringBuilder combinedPrompt = new StringBuilder();
        List<String> originalContents = new ArrayList<>();

        for (File file : filesToProcess) {
            try {
                String content = new String(Files.readAllBytes(file.toPath()));
                originalContents.add(content);
                combinedPrompt.append("\n\n=== File: ").append(file.getName()).append(" ===\n");
                combinedPrompt.append(content).append("\n");
            } catch (IOException e) {
                System.err.println("Error reading file: " + file.getName() + " - " + e.getMessage());
            }
        }

        aiCommunicator.sendPromptAutomatically("Batch of " + filesToProcess.size() + " files", combinedPrompt.toString());

        chatReader.openExistingSession();

        System.out.println("Waiting for AI response (45 seconds)...");
        try {
            Thread.sleep(45000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String improvedCodeBatch = chatReader.fetchLatestCodeBlock();
        chatReader.close();

        if (improvedCodeBatch == null || improvedCodeBatch.isBlank()) {
            System.err.println("Failed to retrieve improved code batch.");
            return;
        }

        try {
            File batchOutput = new File("batch_improvement_output.txt");
            Files.write(batchOutput.toPath(), improvedCodeBatch.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("Saved improved batch output to: " + batchOutput.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error writing batch improvement output: " + e.getMessage());
        }

        boolean anyRealImprovement = false;

        for (int i = 0; i < filesToProcess.size(); i++) {
            File originalFile = filesToProcess.get(i);
            String originalContent = originalContents.get(i);

            // For now, simulate splitting (we can enhance later)
            String improvedContent = improvedCodeBatch; // TODO: Properly split per file in the future

            if (originalContent.trim().equals(improvedContent.trim())) {
                System.out.println("No real improvement detected for file: " + originalFile.getName() + ". Skipping save and commit for this file.");
                continue;
            }

            try {
                Files.write(originalFile.toPath(), improvedContent.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
                anyRealImprovement = true;
            } catch (IOException e) {
                System.err.println("Error writing improved file: " + originalFile.getName() + " - " + e.getMessage());
            }
        }

        if (anyRealImprovement) {
            if (!filesToProcess.isEmpty()) {
                checkpointManager.saveCheckpoint(filesToProcess.get(filesToProcess.size() - 1).getAbsolutePath(), checkpointManager.getIteration() + filesToProcess.size());
            }
            gitManager.addCommitPush("AI improved batch of " + filesToProcess.size() + " files");
        } else {
            System.out.println("No real improvements detected in the batch. No commit created.");
        }
    }
}
