package com.nexusmind;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.File;
import java.io.IOException;

public class CheckpointManager {

    private static final String CHECKPOINT_FILE = "progress.json";

    private String lastProcessedFile;
    private int iteration;

    public CheckpointManager() {
        loadCheckpoint();
    }

    private void loadCheckpoint() {
        File file = new File(CHECKPOINT_FILE);
        if (!file.exists()) {
            System.out.println("No existing checkpoint found. Starting fresh.");
            this.lastProcessedFile = null;
            this.iteration = 0;
            return;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(file);
            this.lastProcessedFile = root.path("last_processed_file").asText(null);
            this.iteration = root.path("iteration").asInt(0);
            System.out.println("Loaded checkpoint: " + lastProcessedFile + " at iteration " + iteration);
        } catch (IOException e) {
            System.err.println("Error loading checkpoint: " + e.getMessage());
            this.lastProcessedFile = null;
            this.iteration = 0;
        }
    }

    public void saveCheckpoint(String lastProcessedFile, int iteration) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode root = mapper.createObjectNode();
            root.put("last_processed_file", lastProcessedFile);
            root.put("iteration", iteration);
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(CHECKPOINT_FILE), root);
            System.out.println("Checkpoint saved: " + lastProcessedFile + " at iteration " + iteration);
        } catch (IOException e) {
            System.err.println("Error saving checkpoint: " + e.getMessage());
        }
    }

    public String getLastProcessedFile() {
        return lastProcessedFile;
    }

    public int getIteration() {
        return iteration;
    }
}
