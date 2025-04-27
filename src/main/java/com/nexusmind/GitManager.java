package com.nexusmind;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GitManager {

    private static final Logger LOGGER = Logger.getLogger(GitManager.class.getName());
    private static final String GITHUB_API_URL = "https://api.github.com/repos/";
    private static final String GITHUB_TOKEN_FILE_PATH = "C:/nexusmind_secrets/github_token.txt";
    private static final String DEFAULT_GITHUB_REPO_OWNER = "glacious83";
    private static final String DEFAULT_GITHUB_REPO_NAME = "NexusMind";

    private final String localRepoPath;
    private final String githubRepoOwner;
    private final String githubRepoName;
    private final String githubToken;

    public GitManager(String localRepoPath, String githubRepoOwner, String githubRepoName) {
        this.localRepoPath = localRepoPath;
        this.githubRepoOwner = githubRepoOwner != null ? githubRepoOwner : DEFAULT_GITHUB_REPO_OWNER;
        this.githubRepoName = githubRepoName != null ? githubRepoName : DEFAULT_GITHUB_REPO_NAME;
        this.githubToken = loadGithubToken();
    }

    /**
     * Loads the GitHub token from the file.
     * @return GitHub token as a string
     */
    private String loadGithubToken() {
        try {
            return Files.readString(new File(GITHUB_TOKEN_FILE_PATH).toPath()).trim();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load GitHub Token from file", e);
            throw new RuntimeException("Failed to load GitHub Token: " + e.getMessage(), e);
        }
    }

    /**
     * Performs a commit and push operation for the given commit message.
     * @param commitMessage The commit message
     */
    public void addCommitPush(String commitMessage) {
        BranchEvolutionManager branchManager = new BranchEvolutionManager(localRepoPath);
        String branchName = branchManager.getOrCreateEvolutionBranch();

        try {
            LOGGER.info("Switching to evolution branch: " + branchName);

            runGitCommand("add", ".");

            if (hasChangesToCommit()) {
                runGitCommand("commit", "-m", commitMessage);
                runGitCommand("push", "-u", "origin", branchName);
                LOGGER.info("Pushed improvements to evolution branch: " + branchName);
                Notifier.sendSuccess("Pushed improvements to evolution branch: " + branchName + "\nCommit Message: " + commitMessage);
            } else {
                LOGGER.info("No changes to commit. Skipping Git commit and push.");
                Notifier.sendSuccess("No changes to commit for this cycle. No push performed.");
            }

        } catch (IOException | InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Git operation failed", e);
            Notifier.sendError("❌ Git operation failed: " + e.getMessage());
        }
    }

    /**
     * Executes a Git command with specified arguments.
     * @param command Command arguments
     * @throws IOException If an I/O error occurs
     * @throws InterruptedException If the process is interrupted
     */
    private void runGitCommand(String... command) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(new File(localRepoPath));
        pb.inheritIO();
        Process process = pb.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Git command failed: " + String.join(" ", command));
        }
    }

    /**
     * Creates a Pull Request for the specified branch.
     * @param branchName The name of the branch
     * @param baseBranch The base branch for the PR
     * @throws IOException If an I/O error occurs during PR creation
     */
    public void createPullRequest(String branchName, String baseBranch) throws IOException {
        URL url = new URL(GITHUB_API_URL + githubRepoOwner + "/" + githubRepoName + "/pulls");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + githubToken);
        connection.setRequestProperty("Accept", "application/vnd.github+json");
        connection.setDoOutput(true);

        String jsonPayload = String.format(
                "{\"title\":\"AI improvements - %s\",\"head\":\"%s\",\"base\":\"%s\"}",
                branchName, branchName, baseBranch
        );

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
            os.write(input);
        }

        int responseCode = connection.getResponseCode();
        if (responseCode != 201) {
            throw new RuntimeException("Failed to create Pull Request. HTTP code: " + responseCode);
        }
    }

    /**
     * Checks if a pull request already exists for the specified branch.
     * @param branchName The name of the branch
     * @return True if a PR exists, otherwise false
     */
    private boolean pullRequestExists(String branchName) {
        try {
            URL url = new URL(GITHUB_API_URL + githubRepoOwner + "/" + githubRepoName + "/pulls?head=" + githubRepoOwner + ":" + branchName);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + githubToken);
            connection.setRequestProperty("Accept", "application/vnd.github+json");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                String responseBody = new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(responseBody);

                return rootNode.isArray() && rootNode.size() > 0;
            } else {
                LOGGER.warning("Failed to check existing PRs. HTTP code: " + responseCode);
                return false;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error checking existing PRs", e);
            return false;
        }
    }

    /**
     * Checks if there are any staged changes in the Git repository.
     * @return True if there are changes to commit, otherwise false
     */
    private boolean hasChangesToCommit() {
        try {
            ProcessBuilder pb = new ProcessBuilder("git", "-C", localRepoPath, "diff", "--cached", "--quiet");
            Process process = pb.start();
            int exitCode = process.waitFor();
            return exitCode != 0;
        } catch (IOException | InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Failed to check git staged changes", e);
            throw new RuntimeException("Failed to check git staged changes: " + e.getMessage(), e);
        }
    }
}