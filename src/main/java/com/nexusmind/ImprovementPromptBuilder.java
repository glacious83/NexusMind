package com.nexusmind;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class ImprovementPromptBuilder {

    private final String projectStructure;

    public ImprovementPromptBuilder(String projectStructure) {
        this.projectStructure = projectStructure;
    }

    public String buildPromptForFile(File file, List<String> dependencies) {
        try {
            String fileContent = new String(Files.readAllBytes(file.toPath())).trim();
            String relativePath = getRelativePath(file);
            String packageName = extractPackageName(fileContent);

            StringBuilder prompt = new StringBuilder();

            prompt.append("""
                You are tasked with directly improving this existing Java class.

                File Path: %s
                Package: %s

                Dependencies:
                """.formatted(relativePath, packageName != null ? packageName : "(not found)"));

            if (dependencies.isEmpty()) {
                prompt.append("- None\n");
            } else {
                for (String dep : dependencies) {
                    prompt.append("- ").append(dep).append("\n");
                }
            }

            prompt.append("""

                Project Structure Overview:
                %s

                Instructions:
                - Carefully improve the provided Java class code.
                - Focus on improving performance, scalability, error handling, modularity, architecture, and readability.
                - Expand the class if needed to add capabilities for AI self-improvement, self-repair, meta-learning, or autonomous behavior.
                - DO NOT discuss improvements. Just directly modify and return the full improved Java code.
                - If you detect missing critical modules or services, note their creation ideas briefly inside the commit message.
                - Return ONLY the full improved Java code inside a ```java``` block.
                - Additionally, provide a short Git commit message summarizing your changes inside [COMMIT_MSG]...[/COMMIT_MSG].

                Current Code:
                """.formatted(projectStructure));

            prompt.append("\n```java\n").append(fileContent).append("\n```");

            return prompt.toString();

        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + file.getName(), e);
        }
    }

    private String extractPackageName(String fileContent) {
        for (String line : fileContent.split("\n")) {
            line = line.trim();
            if (line.startsWith("package ")) {
                return line.replace("package ", "").replace(";", "").trim();
            }
        }
        return null;
    }

    private String getRelativePath(File file) {
        return file.getPath().replace("\\", "/").replaceFirst(".*/src/", "src/");
    }
}
