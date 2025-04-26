package com.nexusmind;

public class AutomationController {

    public static void main(String[] args) {
        System.out.println("Starting NexusMind Automation...");

        // Setup
        CheckpointManager checkpointManager = new CheckpointManager();
        RepoManager repoManager = new RepoManager("https://github.com/glacious83/NexusMind", "C:\\Users\\mmamouze\\IdeaProjects\\NexusMind");

        // Update the repo
        repoManager.updateRepo();

        // Determine next file
        String lastFile = checkpointManager.getLastProcessedFile();
        String nextFile = repoManager.getNextFileToProcess(lastFile);

        if (nextFile == null) {
            System.out.println("No more files to process. Exiting.");
            return;
        }

        System.out.println("Next file to process: " + nextFile);

        // Simulate "processing" file
        try {
            Thread.sleep(2000); // Simulate delay (you will replace with real AI improvement step)
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Save checkpoint
        checkpointManager.saveCheckpoint(nextFile, checkpointManager.getIteration() + 1);

        System.out.println("Finished iteration " + (checkpointManager.getIteration() + 1));
    }
}
