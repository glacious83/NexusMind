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
                You are improving the following Java class:

                File path: %s
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
                - Improve performance, scalability, readability
                - Avoid changing public method signatures unless absolutely necessary
                - Keep the file inside its current package
                - Keep import paths correct
                - Do not move methods to other files
                - Maintain clean code principles
                - Return ONLY the improved full file content. No extra comments.

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
