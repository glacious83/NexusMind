package com.nexusmind;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The AutomationController class orchestrates the NexusMind automation cycles.
 * It initializes the orchestrator and ensures that the automation cycle starts successfully.
 */
public class AutomationController {

    // Logger for tracking lifecycle events and errors during the automation process
    private static final Logger logger = LoggerFactory.getLogger(AutomationController.class);

    /**
     * Main entry point for starting the NexusMind automation process.
     *
     * @param args Command line arguments (currently unused).
     */
    public static void main(String[] args) {
        logger.info("Starting NexusMind Orchestrator...");

        // Initialize the orchestrator with proper exception handling
        NexusMindOrchestrator orchestrator = initializeOrchestrator();

        // Only start the automation cycle if the orchestrator is successfully initialized
        if (orchestrator != null) {
            startAutomationCycle(orchestrator);
        } else {
            logger.error("Orchestrator initialization failed. Aborting automation cycle.");
        }
    }

    /**
     * Initializes the NexusMindOrchestrator with proper exception handling and logging.
     *
     * @return NexusMindOrchestrator instance if initialization is successful; null otherwise.
     */
    private static NexusMindOrchestrator initializeOrchestrator() {
        try {
            // Return a new instance of the orchestrator
            return new NexusMindOrchestrator();
        } catch (Exception e) {
            // Log the exception with clear context for easier diagnosis
            logger.error("Failed to initialize NexusMindOrchestrator.", e);
            return null;
        }
    }

    /**
     * Starts the automation cycle with error handling to capture issues during the cycle execution.
     *
     * @param orchestrator The NexusMindOrchestrator instance managing the automation process.
     */
    private static void startAutomationCycle(NexusMindOrchestrator orchestrator) {
        try {
            orchestrator.startAutomationCycle();
            logger.info("Automation cycle started successfully.");
        } catch (Exception e) {
            logger.error("An error occurred while starting the automation cycle.", e);
        }
    }

    /**
     * Logs the start of a specific process step in the automation cycle.
     *
     * @param stepDescription A brief description of the process step.
     */
    private static void logProcessStart(String stepDescription) {
        logger.info("Starting process: {}", stepDescription);
    }

    /**
     * Logs the completion of a specific process step in the automation cycle.
     *
     * @param stepDescription A brief description of the process step.
     */
    private static void logProcessCompletion(String stepDescription) {
        logger.info("Completed process: {}", stepDescription);
    }

    /**
     * A utility method for handling exceptions, providing detailed context.
     *
     * @param e The exception to log.
     * @param context A description of where the error occurred.
     */
    private static void handleError(Exception e, String context) {
        logger.error("Error occurred during '{}'.", context, e);
    }
    
}