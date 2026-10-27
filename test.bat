@echo off
cd /d "%~dp0"
if not exist out mkdir out
javac -encoding UTF-8 -d out -sourcepath src;test src\fateweavers\Fateweavers.java test\fateweavers\logic\LogicTests.java test\fateweavers\logic\BotPlaytest.java test\fateweavers\swing\Shots.java test\fateweavers\swing\QolShots.java
if errorlevel 1 (
  pause
  exit /b
)
java -cp out fateweavers.logic.LogicTests
rem Off-screen screenshots of every card and screen, saved into the test folder
java -Djava.awt.headless=true -cp out fateweavers.swing.Shots test
java -Djava.awt.headless=true -cp out fateweavers.swing.QolShots test
pause
