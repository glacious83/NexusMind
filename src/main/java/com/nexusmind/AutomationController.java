package com.nexusmind;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AutomationController class responsible for managing the orchestration of NexusMind automation cycles.
 * It initializes the orchestrator and handles exceptions gracefully.
 */
public class AutomationController {

    // Logger for better debugging and error tracking
    private static final Logger logger = LoggerFactory.getLogger(AutomationController.class);

    /**
     * Main entry point to start the NexusMind automation process.
     * 
     * @param args Command line arguments (unused in this version).
     */
    public static void main(String[] args) {
        // Log the beginning of the automation process
        logger.info("Starting NexusMind Orchestrator...");

        // Initialize the orchestrator with proper null checks and exception handling
        NexusMindOrchestrator orchestrator = initializeOrchestrator();

        if (orchestrator != null) {
            startAutomationCycle(orchestrator);
        }
    }

    /**
     * Initializes the NexusMindOrchestrator with exception handling.
     * 
     * @return NexusMindOrchestrator instance or null if initialization fails.
     */
    private static NexusMindOrchestrator initializeOrchestrator() {
        try {
            return new NexusMindOrchestrator();
        } catch (Exception e) {
            logger.error("Failed to initialize NexusMindOrchestrator", e);
            return null; // Return null if orchestration initialization fails
        }
    }

    /**
     * Starts the automation cycle in a try-catch block to handle potential errors gracefully.
     * 
     * @param orchestrator NexusMindOrchestrator instance responsible for automation.
     */
    private static void startAutomationCycle(NexusMindOrchestrator orchestrator) {
        try {
            orchestrator.startAutomationCycle();
        } catch (Exception e) {
            // Log the exception with a detailed message and stack trace for better debugging
            logger.error("An error occurred while starting the automation cycle.", e);
        }
    }
}