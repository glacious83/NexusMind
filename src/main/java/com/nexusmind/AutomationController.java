package com.nexusmind;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entry point for the NexusMind automation system.
 * Handles initialization, structured logging, and controlled shutdown.
 */
public class AutomationController {

    private static final Logger logger = LoggerFactory.getLogger(AutomationController.class);

    public static void main(String[] args) {
        logger.info("Starting NexusMind AutomationController with arguments: {}", (Object) args);
        try {
            executeAutomationCycle();
            logger.info("NexusMind automation cycle completed successfully.");
            System.exit(0);
        } catch (Exception e) {
            logger.error("Automation cycle failed", e);
            System.exit(1);
        }
    }

    /**
     * Initializes and runs the automation cycle.
     *
     * @throws Exception if initialization or execution fails
     */
    private static void executeAutomationCycle() throws Exception {
        logger.debug("Initializing NexusMindOrchestrator");
        NexusMindOrchestrator orchestrator = new NexusMindOrchestrator();
        orchestrator.startAutomationCycle();
    }
}
