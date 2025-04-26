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

public class GitManager {

    private final String localRepoPath;
    private final String githubRepoOwner;
    private final String githubRepoName;
    private final String githubToken;

    public GitManager(String localRepoPath) {
        this.localRepoPath = localRepoPath;
        this.githubRepoOwner = "glacious83";
        this.githubRepoName = "NexusMind";
        this.githubToken = loadGithubToken();
    }

    private String loadGithubToken() {
        File file = new File("C:/nexusmind_secrets/github_token.txt"); // <-- Your real local path
        try {
            return new String(Files.readAllBytes(file.toPath())).trim();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load GitHub Token from file: " + e.getMessage());
        }
    }

    public void addCommitPush(String commitMessage, int iteration) {
        String branchName = "nexusmind/batch-" + iteration + "-" + System.currentTimeMillis();

        try {
            System.out.println("Creating and pushing new branch: " + branchName);

            runCommand(new String[]{"git", "-C", localRepoPath, "checkout", "-b", branchName});
            runCommand(new String[]{"git", "-C", localRepoPath, "add", "."});
            runCommand(new String[]{"git", "-C", localRepoPath, "commit", "-m", commitMessage});
            runCommand(new String[]{"git", "-C", localRepoPath, "push", "-u", "origin", branchName});

            // Optional: small sleep to allow GitHub to "settle" the branch
            Thread.sleep(5000);

            System.out.println("Checking for existing Pull Request...");

            if (pullRequestExists(branchName)) {
                System.out.println("Pull Request already exists for branch: " + branchName);
                Notifier.sendSuccess("Pull Request already exists for branch: " + branchName + "\nCommit Message: " + commitMessage);
            } else {
                try {
                    createPullRequest(branchName, "main");
                    System.out.println("Pull Request created successfully!");
                    Notifier.sendSuccess("Pull Request created successfully for branch: " + branchName + "\nCommit Message: " + commitMessage);
                } catch (RuntimeException e) {
                    if (e.getMessage().contains("HTTP code: 422")) {
                        System.err.println("GitHub rejected Pull Request creation (likely already exists or locked).");
                        Notifier.sendSuccess("PR creation skipped for branch: " + branchName + " (likely already exists or GitHub UI lock).");
                    } else {
                        Notifier.sendError("Git operation failed: " + e.getMessage());
                        throw e; // rethrow if it's not 422
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Git operation failed: " + e.getMessage());
            Notifier.sendError("Git operation failed: " + e.getMessage());
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

    private void createPullRequest(String branchName, String baseBranch) throws IOException {
        URL url = new URL("https://api.github.com/repos/" + githubRepoOwner + "/" + githubRepoName + "/pulls");
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
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        if (responseCode != 201) {
            throw new RuntimeException("Failed to create Pull Request. HTTP code: " + responseCode);
        }
    }

    private boolean pullRequestExists(String branchName) {
        try {
            URL url = new URL("https://api.github.com/repos/" + githubRepoOwner + "/" + githubRepoName + "/pulls?head=" + githubRepoOwner + ":" + branchName);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + githubToken);
            connection.setRequestProperty("Accept", "application/vnd.github+json");

            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                String responseBody = new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

                // Parse the JSON array properly
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(responseBody);

                if (rootNode.isArray() && rootNode.size() > 0) {
                    return true; // PR already exists
                } else {
                    return false; // No PR exists
                }

            } else {
                System.err.println("Failed to check existing PRs. HTTP code: " + responseCode);
                return false; // Assume no PR if error
            }

        } catch (IOException e) {
            System.err.println("Error checking existing PRs: " + e.getMessage());
            return false;
        }
    }


}
