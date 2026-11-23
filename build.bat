@echo off
setlocal
cd /d "%~dp0"
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0generate-audio.ps1"
if errorlevel 1 exit /b 1
call mvnw.cmd -q clean package
if errorlevel 1 exit /b 1
echo Built target\alphabet-sound-player-1.0.0.jar
endlocal
