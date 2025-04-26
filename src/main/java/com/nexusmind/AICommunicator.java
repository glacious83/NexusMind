package com.nexusmind;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

public class AICommunicator {

    public void sendPromptAutomatically(String fileName, String fileContent) {
        String prompt = generatePrompt(fileContent);
        copyToClipboard(prompt);
        triggerAutoHotkeyScript();
        System.out.println("Prompt sent to ChatGPT automatically.");
    }

    private String generatePrompt(String fileContent) {
        return "You are a senior software engineer. Improve the following Java code for performance, readability, and optimization. Only return the improved code inside a single code block.\n\n" + fileContent;
    }

    private void copyToClipboard(String text) {
        Toolkit.getDefaultToolkit()
               .getSystemClipboard()
               .setContents(new StringSelection(text), null);
    }

    private void triggerAutoHotkeyScript() {
        try {
            String ahkScriptPath = new File(
                    Objects.requireNonNull(AICommunicator.class.getResource("/nexus_ai.ahk")).toURI()
            ).getAbsolutePath();

            String autoHotkeyPath = "C:\\Program Files\\AutoHotkey\\AutoHotkey.exe";

            Runtime.getRuntime().exec(new String[] {autoHotkeyPath, ahkScriptPath});

        } catch (IOException | URISyntaxException e) {
            System.err.println("Error running AutoHotkey script: " + e.getMessage());
        }
    }

}
