package com.nexusmind;

import com.microsoft.playwright.*;
import java.util.List;

public class ChatReader {

    private Browser browser;
    private Page page;

    public void openExistingSession() {
        Playwright playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        page = browser.newPage();
        page.navigate("https://chat.openai.com/");

        System.out.println("Please log in manually if not logged in already.");
        System.out.println("Waiting 20 seconds for login...");
        try {
            Thread.sleep(20000); // Wait for manual login
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public String fetchLatestCodeBlock() {
        try{
            try {
                // Wait up to 90 seconds for a code block to appear
                page.waitForSelector("pre code", new Page.WaitForSelectorOptions().setTimeout(90000));

                // Query all matching code blocks (in case there are multiple)
                List<ElementHandle> codeBlocks = page.querySelectorAll("pre code");

                if (codeBlocks.isEmpty()) {
                    System.err.println("No code blocks found after waiting.");
                    return null;
                }

                // Get the last one (most recent)
                ElementHandle latestCodeBlock = codeBlocks.get(codeBlocks.size() - 1);
                String codeContent = latestCodeBlock.innerText();

                System.out.println("Fetched code block successfully.");
                return codeContent;

            } catch (PlaywrightException e) {
                System.err.println("Timeout or error while waiting for code block: " + e.getMessage());
                Notifier.sendError("Timeout or error while waiting for code block: " + e.getMessage());
                return null;
            }
        } catch (Exception e) {
            String errorMsg = "Unexpected error in fetchLatestCodeBlock(): " + e.getMessage();
            System.err.println(errorMsg);
            Notifier.sendError(errorMsg);
            return null;
        }

    }


    public void close() {
        if (browser != null) {
            browser.close();
        }
    }
}
