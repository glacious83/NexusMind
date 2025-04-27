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
                You are tasked with directly improving the following Java class.

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

                STRICT INSTRUCTIONS:
                - You MUST improve the provided Java code meaningfully.
                - DO NOT return the same code unchanged.
                - Always enhance performance, scalability, naming, structure, modularity, error handling, readability, and architecture.
                - If no major logic improvements are found, at least optimize formatting, comments, and minor refactoring.
                - You MUST return:
                    1. The full improved Java class inside a single ```java``` code block.
                    2. Always include the commit message within the code block, placed between [COMMIT_MSG] and [/COMMIT_MSG] tags. This should be a short, clear summary of the changes made
                - Both parts MUST be included every time. Otherwise, the response will be considered invalid.
                - Make sure the code you provide me will compile with no errors!
                - Don't forget to place the commit message inside the code block with [COMMIT_MSG] and [/COMMIT_MSG] tags.
                
                EXAMPLE OF EXPECTED RESPONSE:

                ```java
                package com.nexusmind;

                public class ExampleImprovement {
                    public void evolve() {
                        // Improved evolution logic
                    }
                }
                ```

                [COMMIT_MSG]
                Refactor Example: Improvement to optimize evolution logic and improve method clarity.
                [/COMMIT_MSG]

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
