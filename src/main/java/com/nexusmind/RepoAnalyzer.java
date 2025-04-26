package com.nexusmind;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RepoAnalyzer {

    private final String repoPath;

    public RepoAnalyzer(String repoPath) {
        this.repoPath = repoPath;
    }

    public String generateProjectSummary() {
        File root = new File(repoPath);
        List<String> summaryLines = new ArrayList<>();
        scanDirectory(root, 0, summaryLines);
        return String.join("\n", summaryLines);
    }

    private void scanDirectory(File directory, int depth, List<String> summaryLines) {
        if (!directory.isDirectory()) {
            return;
        }

        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            String prefix = "  ".repeat(depth);
            if (file.isDirectory()) {
                summaryLines.add(prefix + "[Folder] " + file.getName());
                scanDirectory(file, depth + 1, summaryLines);
            } else if (file.getName().endsWith(".java")) {
                summaryLines.add(prefix + "[File] " + file.getName());
            }
        }
    }
}
