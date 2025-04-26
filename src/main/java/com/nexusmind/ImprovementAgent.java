package com.nexusmind;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

public class ImprovementAgent {

    private final CheckpointManager checkpointManager;
    private final RepoManager repoManager;
    private final AICommunicator aiCommunicator;

    public ImprovementAgent(CheckpointManager checkpointManager, RepoManager repoManager) {
        this.checkpointManager = checkpointManager;
        this.repoManager = repoManager;
        this.aiCommunicator = new AICommunicator();
    }

    public void improveNextFile() {
        String lastFile = checkpointManager.getLastProcessedFile();
        String nextFilePath = repoManager.getNextFileToProcess(lastFile);

        if (nextFilePath == null) {
            System.out.println("No more files to process.");
            return;
        }

        File nextFile = new File(nextFilePath);
        try {
            String content = new String(Files.readAllBytes(nextFile.toPath()));
            aiCommunicator.displayPromptForUser(nextFile.getName(), content);

            System.out.println("Paste the improved code here (terminate with a line containing only 'EOF'):");
            Scanner scanner = new Scanner(System.in);
            StringBuilder improvedCode = new StringBuilder();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.trim().equals("EOF")) {
                    break;
                }
                improvedCode.append(line).append("\n");
            }

            Files.write(nextFile.toPath(), improvedCode.toString().getBytes(), StandardOpenOption.TRUNCATE_EXISTING);

            checkpointManager.saveCheckpoint(nextFilePath, checkpointManager.getIteration() + 1);
            System.out.println("Improved file saved and checkpoint updated.");

        } catch (IOException e) {
            System.err.println("Error improving file: " + e.getMessage());
        }
    }
}
