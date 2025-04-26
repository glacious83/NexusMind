package com.nexusmind;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RepoAnalyzer {

    private final String repoPath;
    private final int maxDepth;
    private final int folderSummarizationThreshold;

    public RepoAnalyzer(String repoPath) {
        this.repoPath = repoPath;
        this.maxDepth = 3; // Limit depth to avoid scanning too deeply
        this.folderSummarizationThreshold = 10; // If folder has more than 10 Java files, summarize
    }

    public String generateProjectSummary() {
        File root = new File(repoPath);
        List<String> summaryLines = new ArrayList<>();
        scanDirectory(root, 0, summaryLines);
        return String.join("\n", summaryLines);
    }

    private void scanDirectory(File directory, int depth, List<String> summaryLines) {
        if (!directory.isDirectory() || depth > maxDepth) {
            return;
        }

        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        int javaFileCount = 0;
        List<File> subDirectories = new ArrayList<>();

        for (File file : files) {
            if (file.isDirectory()) {
                subDirectories.add(file);
            } else if (file.getName().endsWith(".java")) {
                javaFileCount++;
            }
        }

        if (javaFileCount > folderSummarizationThreshold) {
            summaryLines.add("  ".repeat(depth) + "[Folder] " + directory.getName() + " (Contains " + javaFileCount + " Java files)");
        } else {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".java")) {
                    summaryLines.add("  ".repeat(depth) + "[File] " + file.getName());
                }
            }
        }

        for (File subDir : subDirectories) {
            summaryLines.add("  ".repeat(depth) + "[Folder] " + subDir.getName());
            scanDirectory(subDir, depth + 1, summaryLines);
        }
    }
}
