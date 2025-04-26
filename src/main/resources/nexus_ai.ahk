; Script: nexus_ai.ahk
; Purpose: Automate ChatGPT web interface input

SetTitleMatchMode, 2  ; Allow partial title match
if WinExist("ChatGPT") 
{
    WinActivate
    Sleep 500
    SendInput ^a  ; Select all existing text
    Sleep 200
    SendInput ^v  ; Paste new code from clipboard
    Sleep 200
    SendInput {Enter}  ; Submit
}
else
{
    MsgBox, ChatGPT window not found!
}
Exit
