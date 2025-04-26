package com.nexusmind;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RepoManager {

    private final String repoUrl;
    private final String localPath;

    public RepoManager(String repoUrl, String localPath) {
        this.repoUrl = repoUrl;
        this.localPath = localPath;
    }

    public void updateRepo() {
        File repoDir = new File(localPath);
        if (repoDir.exists()) {
            System.out.println("Repository exists. Pulling latest changes...");
            runCommand(new String[]{"git", "-C", localPath, "pull"});
        } else {
            System.out.println("Cloning repository...");
            runCommand(new String[]{"git", "clone", repoUrl, localPath});
        }
    }

    private void runCommand(String[] command) {
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.inheritIO();
            Process process = pb.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            System.err.println("Error running command: " + e.getMessage());
        }
    }

    public List<File> listAllJavaFiles(File directory) {
        List<File> javaFiles = new ArrayList<>();
        File[] files = directory.listFiles();
        if (files == null) {
            return javaFiles;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                javaFiles.addAll(listAllJavaFiles(file));
            } else if (file.getName().endsWith(".java")) {
                javaFiles.add(file);
            }
        }
        return javaFiles;
    }

    public String getNextFileToProcess(String lastProcessedFile) {
        List<File> javaFiles = listAllJavaFiles(new File(localPath));
        javaFiles.sort((a, b) -> a.getAbsolutePath().compareToIgnoreCase(b.getAbsolutePath()));

        boolean foundLast = (lastProcessedFile == null);

        for (File file : javaFiles) {
            if (foundLast) {
                return file.getAbsolutePath();
            }
            if (file.getAbsolutePath().equals(lastProcessedFile)) {
                foundLast = true;
            }
        }
        return null;
    }
}
