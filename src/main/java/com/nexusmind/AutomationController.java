package com.nexusmind;

public class AutomationController {

    public static void main(String[] args) {
        System.out.println("Starting NexusMind Automation...");

        CheckpointManager checkpointManager = new CheckpointManager();
        RepoManager repoManager = new RepoManager("https://github.com/glacious83/NexusMind", "C:\\Users\\mmamouze\\IdeaProjects\\NexusMind");
        ImprovementAgent agent = new ImprovementAgent(checkpointManager, repoManager);

        repoManager.updateRepo();
        agent.improveNextFile();
    }
}
