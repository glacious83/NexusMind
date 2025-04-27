package com.nexusmind;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class BranchEvolutionManager {

    private static final String EVOLUTION_BRANCH_FILE = "nexusmind_branch_checkpoint.txt";
    private final String localRepoPath;

    public BranchEvolutionManager(String localRepoPath) {
        validateLocalRepoPath(localRepoPath);
        this.localRepoPath = localRepoPath;
    }

    /**
     * Validates the local repository path.
     *
     * @param localRepoPath The repository path to validate
     * @throws IllegalArgumentException if the repository path is null or empty
     */
    private void validateLocalRepoPath(String localRepoPath) {
        if (localRepoPath == null || localRepoPath.trim().isEmpty()) {
            throw new IllegalArgumentException("Local repository path cannot be null or empty.");
        }
    }

    public String getOrCreateEvolutionBranch() {
        try {
            // Try to find an existing branch name
            Optional<String> existing = getExistingBranchName();

            // If not present, create a new one
            String branch = existing.orElseGet(() -> {
                try {
                    return createNewBranch();
                } catch (IOException ioe) {
                    // wrap the checked exception
                    throw new UncheckedIOException(ioe);
                }
            });

            // Now we have a branch name (existing or new) — do the checkout
            checkoutBranch(branch);
            return branch;

        } catch (UncheckedIOException | IOException e) {
            // unwrap or rewrap into your RuntimeException if you like
            Throwable cause = (e instanceof UncheckedIOException) ? e.getCause() : e;
            throw new RuntimeException("Failed to manage evolution branch: " + cause.getMessage(), cause);
        }
    }


    /**
     * Reads the checkpoint file to get the name of the existing evolution branch.
     *
     * @return The branch name, if found
     * @throws IOException if there is an error reading the file
     */
    private Optional<String> getExistingBranchName() throws IOException {
        Path checkpointPath = Paths.get(EVOLUTION_BRANCH_FILE);
        if (Files.exists(checkpointPath)) {
            return Optional.of(Files.readString(checkpointPath).trim());
        }
        return Optional.empty();
    }

    /**
     * Creates a new evolution branch with a timestamp-based name and writes it to the checkpoint file.
     *
     * @return The new branch name
     * @throws IOException if there is an error creating or writing to the branch file
     */
    private String createNewBranch() throws IOException {
        String newBranchName = "nexusmind/evolution-" + System.currentTimeMillis();
        Files.writeString(Paths.get(EVOLUTION_BRANCH_FILE), newBranchName);
        return newBranchName;
    }

    /**
     * Checks out the specified branch using the git command.
     *
     * @param branchName The branch to checkout
     * @throws IOException if there is an error executing the git command
     */
    private void checkoutBranch(String branchName) throws IOException {
        runCommand(new String[]{"git", "-C", localRepoPath, "checkout", branchName});
    }

    /**
     * Executes a shell command with a timeout to ensure it doesn't hang indefinitely.
     *
     * @param command The command to execute
     * @throws IOException if there is an error executing the command
     */
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

    /**
     * Deletes the checkpoint file after the evolution branch is no longer needed.
     * This helps to keep the system clean and avoids leftover state.
     *
     * @throws IOException if there is an error deleting the file
     */
    public void deleteCheckpointFile() throws IOException {
        Path checkpointPath = Paths.get(EVOLUTION_BRANCH_FILE);
        if (Files.exists(checkpointPath)) {
            Files.delete(checkpointPath);
        }
    }
}