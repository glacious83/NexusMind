package com.nexusmind;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Notifier {

    private static final String WEBHOOK_URL = loadWebhookUrl();

    private static String loadWebhookUrl() {
        try {
            return new String(java.nio.file.Files.readAllBytes(
                    java.nio.file.Paths.get("C:/nexusmind_secrets/slack_webhook.txt")
            )).trim();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load Slack webhook URL from file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        sendSuccess("Test Message: NexusMind webhook notification successful!");
    }

    public static void sendSuccess(String message) {
        sendMessage("[✅ NexusMind] " + message);
    }

    public static void sendError(String message) {
        sendMessage("[❌ NexusMind] " + message);
    }

    private static void sendMessage(String content) {
        try {
            URL url = new URL(WEBHOOK_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String payload = "{\"text\":\"" + content.replace("\"", "\\\"") + "\"}"; // <<< CHANGE to "text"

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = payload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode != 204 && responseCode != 200) {
                System.err.println("Failed to send webhook notification. HTTP code: " + responseCode);
            } else {
                System.out.println("Message send correctly");
            }

        } catch (Exception e) {
            System.err.println("Error sending webhook notification: " + e.getMessage());
        }
    }
}
