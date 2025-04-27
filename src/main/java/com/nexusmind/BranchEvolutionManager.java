package com.nexusmind;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public class BranchEvolutionManager {

    private static final String EVOLUTION_BRANCH_FILE = "nexusmind_branch_checkpoint.txt";
    private final String localRepoPath;

    public BranchEvolutionManager(String localRepoPath) {
        if (localRepoPath == null || localRepoPath.isEmpty()) {
            throw new IllegalArgumentException("Local repository path cannot be null or empty.");
        }
        this.localRepoPath = localRepoPath;
    }

    public String getOrCreateEvolutionBranch() {
        try {
            String branchName = getExistingBranchName().orElseGet(this::createNewBranch);
            checkoutBranch(branchName);
            return branchName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to manage evolution branch: " + e.getMessage(), e);
        }
    }

    private Optional<String> getExistingBranchName() throws IOException {
        File checkpoint = new File(EVOLUTION_BRANCH_FILE);
        if (checkpoint.exists()) {
            String branchName = new String(Files.readAllBytes(checkpoint.toPath())).trim();
            System.out.println("Continuing on existing evolution branch: " + branchName);
            return Optional.of(branchName);
        }
        return Optional.empty();
    }

    private String createNewBranch() throws IOException {
        String newBranchName = "nexusmind/evolution-" + System.currentTimeMillis();
        Files.write(Paths.get(EVOLUTION_BRANCH_FILE), newBranchName.getBytes());
        System.out.println("Created new evolution branch: " + newBranchName);
        return newBranchName;
    }

    private void checkoutBranch(String branchName) throws IOException {
        runCommand(new String[]{"git", "-C", localRepoPath, "checkout", branchName});
    }

    private void runCommand(String[] command) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.inheritIO();
        try {
            Process process = pb.start();
            boolean completedInTime = process.waitFor(30, TimeUnit.SECONDS);
            if (!completedInTime) {
                process.destroy();
                throw new RuntimeException("Git command timed out: " + String.join(" ", command));
            }
            if (process.exitValue() != 0) {
                throw new RuntimeException("Git command failed: " + String.join(" ", command));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while running Git command", e);
        }
    }
}