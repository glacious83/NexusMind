; Script: nexus_ai.ahk
; Purpose: Automate ChatGPT web interface input (AHK v2)

SetTitleMatchMode(2) ; Allow partial match

Sleep(15000) ; Wait 10 seconds for ChatGPT window to open

if WinExist("ChatGPT") {
    WinActivate("ChatGPT")
    Sleep(5000)
    Send("^a") ; Select all existing text
    Sleep(2000)
    Send("^v") ; Paste from clipboard
    Sleep(2000)
    Send("{Enter}") ; Submit
} else {
    MsgBox("ChatGPT window not found!")
}
ExitApp
