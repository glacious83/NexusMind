package com.nexusmind;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProjectStructureMapper {

    private final String projectRootPath;

    public ProjectStructureMapper(String projectRootPath) {
        this.projectRootPath = projectRootPath;
    }

    public String generateProjectStructure() {
        File root = new File(projectRootPath);
        List<String> lines = new ArrayList<>();
        traverseDirectory(root, 0, lines);
        return String.join("\n", lines);
    }

    private void traverseDirectory(File directory, int depth, List<String> lines) {
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
                lines.add(prefix + "- " + file.getName() + "/");
                traverseDirectory(file, depth + 1, lines);
            } else if (file.getName().endsWith(".java") || file.getName().endsWith(".ahk") || file.getName().endsWith(".xml") || file.getName().endsWith(".json")) {
                lines.add(prefix + "- " + file.getName());
            }
        }
    }
}
