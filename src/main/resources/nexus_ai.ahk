; Script: nexus_ai.ahk
; Purpose: Automate ChatGPT web interface input (AHK v2)

SetTitleMatchMode(2) ; Allow partial match

Sleep(10000) ; Wait 10 seconds for ChatGPT window to open

if WinExist("ChatGPT") {
    WinActivate("ChatGPT")
    Sleep(500)
    Send("^a") ; Select all existing text
    Sleep(200)
    Send("^v") ; Paste from clipboard
    Sleep(200)
    Send("{Enter}") ; Submit
} else {
    MsgBox("ChatGPT window not found!")
}
ExitApp
