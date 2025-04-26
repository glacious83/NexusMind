package com.nexusmind;

public class AutomationController {

    public static void main(String[] args) {
        System.out.println("Starting NexusMind Orchestrator...");

        NexusMindOrchestrator orchestrator = new NexusMindOrchestrator();
        orchestrator.startAutomationCycle();
    }
}
