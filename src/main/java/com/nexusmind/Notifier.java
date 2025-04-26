package com.nexusmind;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Notifier {

    private static final String WEBHOOK_URL = "https://hooks.slack.com/services/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";

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

            String payload = "{\"content\":\"" + content.replace("\"", "\\\"") + "\"}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = payload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode != 204) {
                System.err.println("Failed to send webhook notification. HTTP code: " + responseCode);
            }

        } catch (Exception e) {
            System.err.println("Error sending webhook notification: " + e.getMessage());
        }
    }
}
