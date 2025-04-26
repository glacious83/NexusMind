package com.nexusmind;

import java.io.File;
import java.io.IOException;

public class GitManager {

    private final String localRepoPath;

    public GitManager(String localRepoPath) {
        this.localRepoPath = localRepoPath;
    }

    public void addCommitPush(String commitMessage) {
        try {
            System.out.println("Staging changes...");
            runCommand(new String[]{"git", "-C", localRepoPath, "add", "."});

            System.out.println("Committing...");
            runCommand(new String[]{"git", "-C", localRepoPath, "commit", "-m", commitMessage});

            System.out.println("Pushing...");
            runCommand(new String[]{"git", "-C", localRepoPath, "push"});

            System.out.println("Changes pushed to GitHub successfully.");

        } catch (Exception e) {
            System.err.println("Git operation failed: " + e.getMessage());
        }
    }

    private void runCommand(String[] command) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(new File(localRepoPath));
        pb.inheritIO();
        Process process = pb.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Command failed: " + String.join(" ", command));
        }
    }
}
