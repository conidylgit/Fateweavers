@echo off
cd /d "%~dp0"
if not exist out mkdir out
javac -encoding UTF-8 -d out -sourcepath src;test src\fateweavers\Fateweavers.java test\fateweavers\logic\BotPlaytest.java
if errorlevel 1 (
  pause
  exit /b
)
rem Headless bot play-tests: AFK, turtle, rush, Test Mode, then 40 random games (about a minute). Pass a number for more.
java -cp out fateweavers.logic.BotPlaytest 40
pause
