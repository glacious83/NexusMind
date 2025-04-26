package com.nexusmind;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GitHubIssueManager {

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
            return new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get("C:/nexusmind_secrets/github_token.txt"))).trim();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load GitHub Token from file: " + e.getMessage());
        }
    }

    public void createIssue(String title, String body) {
        try {
            // GitHub title length limit = 256 characters
            if (title.length() > 250) {
                title = title.substring(0, 250) + "...";
            }

            URL url = new URL("https://api.github.com/repos/" + githubRepoOwner + "/" + githubRepoName + "/issues");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + githubToken);
            connection.setRequestProperty("Accept", "application/vnd.github+json");
            connection.setDoOutput(true);

            String jsonPayload = String.format(
                    "{\"title\":\"%s\",\"body\":\"%s\"}",
                    title.replace("\"", "\\\""),
                    body.replace("\"", "\\\"")
            );

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode != 201) {
                throw new RuntimeException("Failed to create GitHub Issue. HTTP code: " + responseCode);
            }

            System.out.println("Issue created successfully: " + title);

        } catch (IOException e) {
            System.err.println("Error creating GitHub Issue: " + e.getMessage());
        }
    }
}
