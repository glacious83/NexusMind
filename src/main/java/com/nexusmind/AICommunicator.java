package com.nexusmind;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

public class AICommunicator {

    /**
     * Copy-pastes a prompt into ChatGPT by way of the clipboard + AutoHotkey.
     */
    public void sendPromptAutomatically(String fileName, String fileContent) {
        String prompt = generatePrompt(fileContent);
        copyToClipboard(prompt);
        triggerAutoHotkeyScript();
        System.out.println("Prompt sent to ChatGPT automatically.");
    }

    /** Builds the LLM prompt around the file’s contents. */
    private String generatePrompt(String fileContent) {
        return "You are a senior software engineer. "
                + "Improve the code and provide the improved code inside a single code block.\n\n"
                + fileContent;
    }

    /** Puts the given text onto the system clipboard. */
    private void copyToClipboard(String text) {
        StringSelection selection = new StringSelection(text);
        Toolkit.getDefaultToolkit()
               .getSystemClipboard()
               .setContents(selection, null);
    }

    /** Locates and runs our bundled AutoHotkey script to paste & send the prompt. */
    private void triggerAutoHotkeyScript() {
        try {
            // Load the .ahk file from our resources folder
            String ahkScriptPath = new File(
                    Objects.requireNonNull(
                            AICommunicator.class.getResource("/nexus_ai.ahk")
                    ).toURI()
            ).getAbsolutePath();

            // Adjust this if your install path differs
            String autoHotkeyExe = "C:\\Program Files\\AutoHotkey\\v2\\AutoHotkey.exe";

            Runtime.getRuntime()
                   .exec(new String[] { autoHotkeyExe, ahkScriptPath });

        } catch (IOException | URISyntaxException e) {
            System.err.println("Error running AutoHotkey script: " + e.getMessage());
        }
    }

}
