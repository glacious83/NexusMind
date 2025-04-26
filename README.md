# NexusMind

NexusMind is a self-evolving AI-assisted Java project improvement system.

## Features

- Auto-pulls GitHub repo
- Auto-selects next Java file
- Auto-sends file to ChatGPT (via clipboard + AutoHotkey)
- Auto-reads ChatGPT response (via Playwright)
- Auto-updates the Java file
- Auto-commits and pushes improvements back to GitHub
- Full recovery on crash via checkpointing

## Requirements

- Java 21
- Maven
- AutoHotkey installed (for prompt automation)
- Playwright installed (for reading ChatGPT output)
- Git installed and accessible via CLI

## Setup

- Configure your GitHub repo URL and local path in `AutomationController.java`
- Configure your AutoHotkey script path in `AICommunicator.java`
- Ensure ChatGPT window is open manually
- Run `AutomationController.main()`

## Notes

- Manual login to ChatGPT is required on first Playwright session
- Improvements assume ChatGPT is set to reply with pure code blocks only
