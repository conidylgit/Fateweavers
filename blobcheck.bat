@echo off
cd /d "%~dp0"
rem 26 Oct: blob renderer parity. Compares the game's BlobArt with tools\BlobPreview for every model in class_tree\class_blobs.txt
rem and writes side-by-side sheets to test\blob_parity\ (game on top, preview below).
if not exist out mkdir out
javac -encoding UTF-8 -d out tools\BlobPreview.java
javac -encoding UTF-8 -d out -cp out -sourcepath src;test test\fateweavers\swing\BlobParity.java
java -Djava.awt.headless=true -cp out fateweavers.swing.BlobParity class_tree\class_blobs.txt test\blob_parity
pause
