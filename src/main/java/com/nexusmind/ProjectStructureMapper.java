package com.nexusmind;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ProjectStructureMapper {

    private final String projectRootPath;

    private static final Set<String> IGNORE_DIRS = Set.of(
            ".git",
            ".idea",
            "target",
            "out"
    );

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

        if (IGNORE_DIRS.contains(directory.getName())) {
            return;
        }

        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            // skip hidden or ignored dirs/files
            if (file.isDirectory() && IGNORE_DIRS.contains(file.getName())) {
                continue;
            }
            String prefix = "  ".repeat(depth);
            if (file.isDirectory()) {
                lines.add(prefix + "- " + file.getName() + "/");
                traverseDirectory(file, depth + 1, lines);
            } else if (file.getName().endsWith(".java") || file.getName().endsWith(".ahk") || file.getName().endsWith(".xml") || file.getName()
                                                                                                                                     .endsWith(".json")) {
                lines.add(prefix + "- " + file.getName());
            }
        }
    }
}
