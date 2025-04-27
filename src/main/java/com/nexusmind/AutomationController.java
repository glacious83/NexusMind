package com.nexusmind;

public class AutomationController {

    public static void main(String[] args) {
        // Log the beginning of the automation process
        System.out.println("Starting NexusMind Orchestrator...");

        // Initialize the orchestrator and start the automation cycle
        NexusMindOrchestrator orchestrator = new NexusMindOrchestrator();
        try {
            orchestrator.startAutomationCycle();
        } catch (Exception e) {
            // Handle any exception that might occur during the automation cycle
            System.err.println("An error occurred while starting the automation cycle: " + e.getMessage());
            e.printStackTrace();
        }
    }
}