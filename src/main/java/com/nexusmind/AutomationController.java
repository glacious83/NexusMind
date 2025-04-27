package com.nexusmind;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutomationController {

    // Logger for better debugging and error tracking
    private static final Logger logger = LoggerFactory.getLogger(AutomationController.class);

    public static void main(String[] args) {
        // Log the beginning of the automation process
        logger.info("Starting NexusMind Orchestrator...");

        // Initialize the orchestrator and start the automation cycle
        NexusMindOrchestrator orchestrator = new NexusMindOrchestrator();
        
        // Wrap the automation cycle in a try-with-resources block for better exception handling and resource management
        try {
            orchestrator.startAutomationCycle();
        } catch (Exception e) {
            // Log the exception message with stack trace for better debugging
            logger.error("An error occurred while starting the automation cycle: {}", e.getMessage(), e);
        }
    }
}