package com.nexusmind;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * Coordinates the AI‐driven improvement of Java source files, extraction of commit messages,
 * and atomic Git commits only when valid changes are present.
 */
public class ImprovementAgent {

    private static final Logger logger = LoggerFactory.getLogger(ImprovementAgent.class);

    private final CheckpointManager checkpointManager;
    private final RepoManager repoManager;
    private final ProjectStructureMapper structureMapper;
    private final ImprovementPromptBuilder promptBuilder;
    private final AICommunicator aiCommunicator;
    private final ChatReader chatReader;
    private final GitManager gitManager;

    public ImprovementAgent(CheckpointManager checkpointManager,
            RepoManager repoManager,
            GitManager gitManager) {
        this.checkpointManager = checkpointManager;
        this.repoManager = repoManager;
        this.gitManager = gitManager;
        this.structureMapper = new ProjectStructureMapper(repoManager.getLocalPath());
        String projectStructure = structureMapper.generateProjectStructure();
        this.promptBuilder = new ImprovementPromptBuilder(projectStructure);
        this.aiCommunicator = new AICommunicator();
        this.chatReader = new ChatReader();
    }

    /**
     * Processes up to {@code batchSize} files: prompts the AI, validates output,
     * records valid improvements, and commits them all in one meaningful Git commit.
     */
    public void improveNextFiles(int batchSize) {
        String lastProcessed = checkpointManager.getLastProcessedFile();
        int iteration = checkpointManager.getIteration();

        List<String> improvedFiles = new ArrayList<>();
        List<String> commitMessages = new ArrayList<>();

        for (int i = 0; i < batchSize; i++) {
            String filePath = repoManager.getNextFileToProcess(lastProcessed);
            if (filePath == null) {
                logger.info("No more files to process.");
                break;
            }

            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                logger.warn("File not found, skipping: {}", filePath);
                lastProcessed = filePath;
                checkpointManager.saveCheckpoint(lastProcessed, iteration);
                continue;
            }

            logger.info("Improving file: {}", filePath);
            String prompt = promptBuilder.buildPromptForFile(path.toFile(),
                    repoManager.findRelatedDependencies(path.toFile()));
            aiCommunicator.sendPromptAutomatically("Improve Java Class and Provide Commit Message", prompt);

            chatReader.openExistingSession();
            try {
                // TODO: replace sleep with smarter wait-for-response logic
                Thread.sleep(30_000);
                String fullResponse = chatReader.fetchLatestCodeBlock();
                if (fullResponse == null) {
                    logger.error("AI response timed out for file: {}", filePath);
                    break;
                }

                Optional<String> maybeMsg = extractCommitMessage(fullResponse);
                if (maybeMsg.isEmpty()) {
                    logger.error("No [COMMIT_MSG] tag in response; skipping: {}", filePath);
                    lastProcessed = filePath;
                    checkpointManager.saveCheckpoint(lastProcessed, iteration);
                    continue;
                }
                String commitMsg = maybeMsg.get();

                String improvedCode = extractImprovedCode(fullResponse);
                if (!SimpleJavaValidator.isValidJavaClass(improvedCode)) {
                    logger.error("AI-produced code invalid for file: {}; skipping", filePath);
                    lastProcessed = filePath;
                    checkpointManager.saveCheckpoint(lastProcessed, iteration);
                    continue;
                }

                Files.writeString(path, improvedCode, StandardOpenOption.TRUNCATE_EXISTING);
                logger.info("File updated: {}", filePath);
                improvedFiles.add(filePath);
                commitMessages.add(path.getFileName() + ": " + commitMsg);

                lastProcessed = filePath;
                iteration++;
                checkpointManager.saveCheckpoint(lastProcessed, iteration);

            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                logger.error("Interrupted while waiting for AI response", ie);
                break;
            } catch (IOException ioe) {
                logger.error("Error writing improved code to file: {}", filePath, ioe);
            } finally {
                chatReader.close();
            }
        }

        if (improvedFiles.isEmpty()) {
            logger.info("No valid improvements detected; skipping Git commit.");
            return;
        }

        String fullCommit = String.join("\n\n", commitMessages);
        logger.info("Committing {} files with message:\n{}", improvedFiles.size(), fullCommit);
        gitManager.addCommitPush(fullCommit);
    }

    private Optional<String> extractCommitMessage(String response) {
        int start = response.indexOf("[COMMIT_MSG]");
        int end   = response.indexOf("[/COMMIT_MSG]");
        if (start >= 0 && end > start) {
            return Optional.of(response.substring(start + 12, end).trim());
        }
        return Optional.empty();
    }

    private String extractImprovedCode(String response) {
        int idx = response.indexOf("[COMMIT_MSG]");
        return idx >= 0 ? response.substring(0, idx).trim() : response.trim();
    }
}
