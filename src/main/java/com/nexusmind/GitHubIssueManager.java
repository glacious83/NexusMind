package com.nexusmind;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GitHubIssueManager {

    private static final Logger LOGGER = Logger.getLogger(GitHubIssueManager.class.getName());
    private static final int MAX_TITLE_LENGTH = 250;
    private static final String GITHUB_TOKEN_FILE_PATH = "C:/nexusmind_secrets/github_token.txt";
    private static final String GITHUB_API_URL_TEMPLATE = "https://api.github.com/repos/%s/%s/issues";

    private final String githubRepoOwner;
    private final String githubRepoName;
    private final String githubToken;

    public GitHubIssueManager(String repoOwner, String repoName) {
        this.githubRepoOwner = (repoOwner != null && !repoOwner.trim().isEmpty()) ? repoOwner : "glacious83";
        this.githubRepoName = (repoName != null && !repoName.trim().isEmpty()) ? repoName : "NexusMind";
        this.githubToken = loadGithubToken();
    }

    /**
     * Loads the GitHub token from the file.
     *
     * @return the GitHub token
     */
    private String loadGithubToken() {
        try {
            Path tokenFilePath = Paths.get(GITHUB_TOKEN_FILE_PATH);
            if (!Files.exists(tokenFilePath)) {
                throw new RuntimeException("GitHub token file not found.");
            }
            return new String(Files.readAllBytes(tokenFilePath)).trim();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load GitHub Token from file: " + e.getMessage(), e);
            throw new RuntimeException("Failed to load GitHub Token", e);
        }
    }

    /**
     * Creates a new issue on GitHub.
     *
     * @param title the title of the issue
     * @param body  the body content of the issue
     */
    public void createIssue(String title, String body) {
        String truncatedTitle = truncateTitle(title);

        try {
            URL url = new URL(String.format(GITHUB_API_URL_TEMPLATE, githubRepoOwner, githubRepoName));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + githubToken);
            connection.setRequestProperty("Accept", "application/vnd.github+json");
            connection.setDoOutput(true);

            String jsonPayload = buildJsonPayload(truncatedTitle, body);
            sendPostRequest(connection, jsonPayload);
            handleResponse(connection);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error creating GitHub Issue: " + e.getMessage(), e);
        }
    }

    /**
     * Truncates the title to ensure it does not exceed the maximum length.
     *
     * @param title the title of the issue
     * @return the truncated title
     */
    private String truncateTitle(String title) {
        return (title.length() > MAX_TITLE_LENGTH) ? title.substring(0, MAX_TITLE_LENGTH) + "..." : title;
    }

    /**
     * Builds the JSON payload for the request.
     *
     * @param title the title of the issue
     * @param body  the body content of the issue
     * @return the JSON payload as a string
     */
    private String buildJsonPayload(String title, String body) {
        return String.format("{\"title\":\"%s\",\"body\":\"%s\"}", escapeJsonString(title), escapeJsonString(body));
    }

    /**
     * Sends the POST request to the GitHub API.
     *
     * @param connection the HTTP connection
     * @param jsonPayload the JSON payload
     * @throws IOException if an I/O error occurs
     */
    private void sendPostRequest(HttpURLConnection connection, String jsonPayload) throws IOException {
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
            os.write(input);
        }
    }

    /**
     * Handles the HTTP response from the GitHub API.
     *
     * @param connection the HTTP connection
     * @throws IOException if an I/O error occurs or if the response is an error
     */
    private void handleResponse(HttpURLConnection connection) throws IOException {
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_CREATED) {
            LOGGER.log(Level.SEVERE, "Failed to create GitHub Issue. HTTP code: " + responseCode);
            throw new RuntimeException("Failed to create GitHub Issue. HTTP code: " + responseCode);
        }

        LOGGER.info("Issue created successfully: " + connection.getURL());
    }

    /**
     * Escapes special characters in the string to ensure it is valid JSON.
     *
     * @param input the input string
     * @return the escaped string
     */
    private String escapeJsonString(String input) {
        return input.replace("\"", "\\\"");
    }
}