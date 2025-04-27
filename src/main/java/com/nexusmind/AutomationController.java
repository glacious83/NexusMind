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

        // Initialize the orchestrator with proper null checks and exception handling
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
}