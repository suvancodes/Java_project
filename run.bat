@echo off
cd /d "%~dp0"
if not exist "target\alphabet-sound-player-1.0.0.jar" call build.bat
if errorlevel 1 exit /b 1
java -jar "target\alphabet-sound-player-1.0.0.jar"
