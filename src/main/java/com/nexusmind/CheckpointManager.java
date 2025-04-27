package com.nexusmind;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages the checkpointing of process state, allowing resumption after interruption.
 * Stores the last processed file and the iteration count in a JSON file.
 */
public class CheckpointManager {

    private static final String CHECKPOINT_FILE = "progress.json";
    private static final ObjectMapper MAPPER = new ObjectMapper();  // Reuse ObjectMapper instance
    private static final Logger LOGGER = Logger.getLogger(CheckpointManager.class.getName());

    private String lastProcessedFile;
    private int iteration;

    /**
     * Default constructor that loads the checkpoint from the checkpoint file.
     */
    public CheckpointManager() {
        loadCheckpoint();
    }

    /**
     * Loads the checkpoint data from the checkpoint file.
     * If the file doesn't exist or is corrupted, starts fresh with default values.
     */
    private void loadCheckpoint() {
        File file = new File(CHECKPOINT_FILE);
        if (!file.exists()) {
            LOGGER.info("No existing checkpoint found. Starting fresh.");
            resetCheckpoint();
            return;
        }
        
        try {
            String jsonContent = new String(Files.readAllBytes(Paths.get(CHECKPOINT_FILE)));
            JsonNode root = MAPPER.readTree(jsonContent);
            this.lastProcessedFile = root.path("last_processed_file").asText(null);
            this.iteration = root.path("iteration").asInt(0);
            LOGGER.info(String.format("Loaded checkpoint: %s at iteration %d", lastProcessedFile, iteration));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading checkpoint", e);
            resetCheckpoint();
        }
    }

    /**
     * Resets the checkpoint data to default values.
     */
    private void resetCheckpoint() {
        this.lastProcessedFile = null;
        this.iteration = 0;
    }

    /**
     * Saves the checkpoint data to the checkpoint file.
     *
     * @param lastProcessedFile The last processed file.
     * @param iteration The current iteration.
     */
    public void saveCheckpoint(String lastProcessedFile, int iteration) {
        try {
            ObjectNode root = MAPPER.createObjectNode();
            root.put("last_processed_file", Optional.ofNullable(lastProcessedFile).orElse(""));
            root.put("iteration", iteration);

            // Writing to file using Files utility for better performance and exception handling
            Files.write(Paths.get(CHECKPOINT_FILE), MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(root).getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            LOGGER.info(String.format("Checkpoint saved: %s at iteration %d", lastProcessedFile, iteration));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving checkpoint", e);
        }
    }

    /**
     * Gets the last processed file from the checkpoint data.
     *
     * @return The last processed file.
     */
    public String getLastProcessedFile() {
        return lastProcessedFile;
    }

    /**
     * Gets the current iteration count from the checkpoint data.
     *
     * @return The current iteration count.
     */
    public int getIteration() {
        return iteration;
    }
}