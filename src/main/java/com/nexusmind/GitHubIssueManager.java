package com.nexusmind;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GitHubIssueManager {

    private static final Logger LOGGER = Logger.getLogger(GitHubIssueManager.class.getName());
    private static final int MAX_TITLE_LENGTH = 250;
    private final String githubRepoOwner;
    private final String githubRepoName;
    private final String githubToken;

    public GitHubIssueManager() {
        this.githubRepoOwner = "glacious83";
        this.githubRepoName = "NexusMind";
        this.githubToken = loadGithubToken();
    }

    private String loadGithubToken() {
        try {
            return new String(Files.readAllBytes(Paths.get("C:/nexusmind_secrets/github_token.txt"))).trim();
        } catch (IOException e) {
            String errorMessage = "Failed to load GitHub Token from file: " + e.getMessage();
            LOGGER.log(Level.SEVERE, errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }
    }

    public void createIssue(String title, String body) {
        // Ensure the title does not exceed GitHub's character limit
        String truncatedTitle = title.length() > MAX_TITLE_LENGTH ? title.substring(0, MAX_TITLE_LENGTH) + "..." : title;

        try {
            URL url = new URL("https://api.github.com/repos/" + githubRepoOwner + "/" + githubRepoName + "/issues");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + githubToken);
            connection.setRequestProperty("Accept", "application/vnd.github+json");
            connection.setDoOutput(true);

            String jsonPayload = String.format(
                    "{\"title\":\"%s\",\"body\":\"%s\"}",
                    escapeJsonString(truncatedTitle),
                    escapeJsonString(body)
            );

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_CREATED) {
                String errorMessage = "Failed to create GitHub Issue. HTTP code: " + responseCode;
                LOGGER.log(Level.SEVERE, errorMessage);
                throw new RuntimeException(errorMessage);
            }

            LOGGER.info("Issue created successfully: " + truncatedTitle);

        } catch (IOException e) {
            String errorMessage = "Error creating GitHub Issue: " + e.getMessage();
            LOGGER.log(Level.SEVERE, errorMessage, e);
        }
    }

    private String escapeJsonString(String input) {
        return input.replace("\"", "\\\"");
    }
}