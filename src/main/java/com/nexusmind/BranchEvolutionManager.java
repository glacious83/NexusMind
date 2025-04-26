package com.nexusmind;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BranchEvolutionManager {

    private static final String EVOLUTION_BRANCH_FILE = "nexusmind_branch_checkpoint.txt";
    private final String localRepoPath;

    public BranchEvolutionManager(String localRepoPath) {
        this.localRepoPath = localRepoPath;
    }

    public String getOrCreateEvolutionBranch() {
        try {
            File checkpoint = new File(EVOLUTION_BRANCH_FILE);
            if (checkpoint.exists()) {
                String branchName = new String(Files.readAllBytes(checkpoint.toPath())).trim();
                System.out.println("Continuing on existing evolution branch: " + branchName);
                checkoutBranch(branchName);
                return branchName;
            } else {
                String newBranchName = "nexusmind/evolution-" + System.currentTimeMillis();
                checkoutNewBranch(newBranchName);
                Files.write(Paths.get(EVOLUTION_BRANCH_FILE), newBranchName.getBytes());
                System.out.println("Created new evolution branch: " + newBranchName);
                return newBranchName;
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to manage evolution branch: " + e.getMessage(), e);
        }
    }

    private void checkoutNewBranch(String branchName) throws IOException {
        runCommand(new String[]{"git", "-C", localRepoPath, "checkout", "-b", branchName});
    }

    private void checkoutBranch(String branchName) throws IOException {
        runCommand(new String[]{"git", "-C", localRepoPath, "checkout", branchName});
    }

    private void runCommand(String[] command) throws IOException {
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.inheritIO();
            Process process = pb.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Git command failed: " + String.join(" ", command));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while running Git command");
        }
    }
}
