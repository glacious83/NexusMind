package com.nexusmind;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AutomationController class orchestrates the NexusMind automation cycles.
 * It initializes the orchestrator and ensures that the automation cycle starts successfully.
 */
public class AutomationController {

    // Logger for tracking the lifecycle and errors in the automation process
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

        if (orchestrator != null) {
            // Proceed to start the automation cycle only if the orchestrator initialization is successful
            startAutomationCycle(orchestrator);
        } else {
            logger.error("Orchestrator initialization failed. Aborting automation cycle.");
        }
    }

    /**
     * Initializes the NexusMindOrchestrator with exception handling and logging.
     *
     * @return NexusMindOrchestrator instance if initialization is successful; null otherwise.
     */
    private static NexusMindOrchestrator initializeOrchestrator() {
        try {
            // Return a new instance of the orchestrator
            return new NexusMindOrchestrator();
        } catch (Exception e) {
            // Log the exception with clear context
            logger.error("Failed to initialize NexusMindOrchestrator.", e);
            return null; // Return null if orchestration initialization fails
        }
    }

    /**
     * Starts the automation cycle, catching any errors that may occur during execution.
     *
     * @param orchestrator The NexusMindOrchestrator instance managing the automation.
     */
    private static void startAutomationCycle(NexusMindOrchestrator orchestrator) {
        try {
            // Start the automation cycle and log its progress
            orchestrator.startAutomationCycle();
            logger.info("Automation cycle started successfully.");
        } catch (Exception e) {
            // Detailed logging to help diagnose issues in the automation cycle
            logger.error("An error occurred while starting the automation cycle.", e);
        }
    }
    
    /**
     * Utility method to log the start of a specific process step in the automation.
     *
     * @param stepDescription The description of the process step.
     */
    private static void logProcessStart(String stepDescription) {
        logger.info("Starting process: {}", stepDescription);
    }

    /**
     * Utility method to log the completion of a specific process step in the automation.
     *
     * @param stepDescription The description of the process step.
     */
    private static void logProcessCompletion(String stepDescription) {
        logger.info("Completed process: {}", stepDescription);
    }

    /**
     * Utility method to handle exceptions with detailed context.
     *
     * @param e The exception to log.
     * @param context The context or description of where the error occurred.
     */
    private static void handleError(Exception e, String context) {
        logger.error("Error occurred during '{}'.", context, e);
    }
}