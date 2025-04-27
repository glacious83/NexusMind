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
            root.put("last_processed_file", Optional.ofNullable(lastProcessedFile).orElse(""));  // Default to empty string
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

    /**
     * Validates if the checkpoint file exists and is readable.
     *
     * @return true if the file exists and is readable, false otherwise.
     */
    public boolean isCheckpointFileValid() {
        File file = new File(CHECKPOINT_FILE);
        return file.exists() && file.canRead();
    }

    /**
     * Deletes the checkpoint file if it exists.
     * Useful in scenarios where you want to reset the checkpoint completely.
     */
    public void deleteCheckpointFile() {
        try {
            Files.deleteIfExists(Paths.get(CHECKPOINT_FILE));
            LOGGER.info("Checkpoint file deleted successfully.");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error deleting checkpoint file", e);
        }
    }

    /**
     * Provides a clean string representation of the checkpoint for debugging purposes.
     *
     * @return String representation of the checkpoint.
     */
    public String getCheckpointInfo() {
        return String.format("Checkpoint - Last processed file: %s, Iteration: %d", lastProcessedFile, iteration);
    }

    /**
     * Checks if the checkpoint is initialized (i.e., not empty or null).
     *
     * @return true if checkpoint is initialized, false otherwise.
     */
    public boolean isCheckpointInitialized() {
        return lastProcessedFile != null && !lastProcessedFile.isEmpty() && iteration > 0;
    }

    /**
     * Resets the checkpoint to the given file and iteration values. This is a more flexible reset.
     *
     * @param lastProcessedFile The last processed file.
     * @param iteration The iteration number.
     */
    public void resetCheckpoint(String lastProcessedFile, int iteration) {
        this.lastProcessedFile = lastProcessedFile;
        this.iteration = iteration;
        LOGGER.info(String.format("Checkpoint reset to file: %s, iteration: %d", lastProcessedFile, iteration));
    }
}