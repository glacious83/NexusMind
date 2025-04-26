package com.nexusmind;

import com.microsoft.playwright.*;

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
        page.waitForSelector("pre code");

        ElementHandle latestCodeBlock = page.querySelector("pre code");

        if (latestCodeBlock == null) {
            System.err.println("No code block found!");
            return null;
        }

        String codeContent = latestCodeBlock.innerText();

        System.out.println("Fetched code block successfully.");
        return codeContent;
    }

    public void close() {
        if (browser != null) {
            browser.close();
        }
    }
}
