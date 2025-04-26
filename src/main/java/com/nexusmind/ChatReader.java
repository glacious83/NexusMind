package com.nexusmind;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.options.WaitForSelectorState;
import java.util.List;

public class ChatReader {

    private Browser browser;

    private Page page;

    public void openExistingSession() {
        Playwright playwright = Playwright.create();
        BrowserType chromium = playwright.chromium();

        // Connect to the existing Edge browser over websocket
        // Connect to your already-running Edge/Chrome (must have been launched with --remote-debugging-port=9222)
        this.browser = chromium.connectOverCDP("http://localhost:9222");
        // Grab the first open page (or open a new one if none exist)
        if (!browser.contexts().isEmpty() && !browser.contexts().get(0).pages().isEmpty()) {
            this.page = browser.contexts().get(0).pages().get(0);
        } else {
            this.page = browser.newContext().newPage();
        }
        // Make sure we're on the ChatGPT interface
        page.navigate("https://chat.openai.com/chat");
        Page page = browser.contexts().get(0).pages().get(0);
        this.page = page;

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
        try {
            try {
                // Wait up to 90 seconds for a code block to appear
                page.waitForSelector("div.markdown pre",
                        new Page.WaitForSelectorOptions()
                                .setState(WaitForSelectorState.VISIBLE)
                                .setTimeout(90000));

                // Query all matching code blocks (in case there are multiple)
                List<ElementHandle> codeBlocks = page.querySelectorAll("div.markdown pre");

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
