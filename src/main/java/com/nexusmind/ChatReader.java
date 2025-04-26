package com.nexusmind;

import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import java.util.List;

public class ChatReader {

    private final Playwright playwright;
    private final Page page;

    public ChatReader() {
        this.playwright = Playwright.create();
        this.page = playwright.chromium().launch().newContext().newPage();
        this.page.navigate("https://chat.openai.com/");
    }

    public void openExistingSession() {
        // No real open needed - session opened in constructor
    }

    public void close() {
        if (page != null) {
            try {
                page.close();
                playwright.close();
                System.out.println("[ChatReader] Page and Playwright closed successfully.");
            } catch (Exception e) {
                System.err.println("[ChatReader] Failed to close page: " + e.getMessage());
            }
        }
    }

    public String fetchLatestCodeBlock() {
        try {
            page.waitForSelector("pre code", new Page.WaitForSelectorOptions().setTimeout(90000));
            ElementHandle latestCodeBlock = page.querySelector("pre code");

            if (latestCodeBlock == null) {
                System.err.println("No code block found!");
                return null;
            }

            String codeContent = latestCodeBlock.innerText();
            System.out.println("[ChatReader] Fetched latest code block successfully.");
            return codeContent;
        } catch (Exception e) {
            System.err.println("[ChatReader] Unexpected error fetching latest code block: " + e.getMessage());
            return null;
        }
    }

    public String fetchFullAIReply() {
        try {
            page.waitForSelector("div[data-message-author-role='assistant']", new Page.WaitForSelectorOptions().setTimeout(90000));
            ElementHandle message = page.querySelector("div[data-message-author-role='assistant']");
            if (message == null) {
                System.err.println("No AI reply found!");
                return null;
            }

            List<ElementHandle> parts = message.querySelectorAll("div.markdown > *");
            StringBuilder fullResponse = new StringBuilder();

            for (ElementHandle part : parts) {
                String tagName = part.evaluate("e => e.tagName").toString().toLowerCase();
                if (tagName.equals("\"P\"")) {
                    fullResponse.append(part.innerText()).append("\n\n");
                } else if (tagName.equals("\"PRE\"")) {
                    ElementHandle code = part.querySelector("code");
                    if (code != null) {
                        String className = code.getAttribute("class");
                        if (className != null && className.contains("language-java")) {
                            fullResponse.append("```java\n").append(code.innerText()).append("\n```\n\n");
                        } else {
                            fullResponse.append("```\n").append(code.innerText()).append("\n```\n\n");
                        }
                    }
                }
            }

            String result = fullResponse.toString().trim();
            System.out.println("[ChatReader] Fetched full structured AI response successfully.");
            return result;
        } catch (Exception e) {
            System.err.println("[ChatReader] Unexpected error fetching full AI reply: " + e.getMessage());
            return null;
        }
    }
}
