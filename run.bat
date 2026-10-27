@echo off
cd /d "%~dp0"
if not exist out mkdir out
javac -encoding UTF-8 -d out -sourcepath src src\fateweavers\Fateweavers.java
if errorlevel 1 (
  pause
  exit /b
)
rem The OpenGL flag draws with the graphics card. If the window looks broken, delete -Dsun.java2d.opengl=true
java -Dsun.java2d.opengl=true -cp out fateweavers.Fateweavers
