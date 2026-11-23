@echo off
setlocal EnableExtensions
set "MAVEN_VERSION=3.9.10"
set "MAVEN_HOME=%~dp0.maven\apache-maven-%MAVEN_VERSION%"
set "MAVEN_EXE=%MAVEN_HOME%\bin\mvn.cmd"
if not exist "%MAVEN_EXE%" (
  echo Downloading Maven %MAVEN_VERSION% for this project...
  set "MAVEN_ARCHIVE=%TEMP%\apache-maven-%MAVEN_VERSION%-bin.zip"
  powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -Uri 'https://archive.apache.org/dist/maven/maven-3/%MAVEN_VERSION%/binaries/apache-maven-%MAVEN_VERSION%-bin.zip' -OutFile $env:MAVEN_ARCHIVE"
  if errorlevel 1 exit /b 1
  if not exist "%~dp0.maven" mkdir "%~dp0.maven"
  powershell -NoProfile -ExecutionPolicy Bypass -Command "Expand-Archive -LiteralPath $env:MAVEN_ARCHIVE -DestinationPath '%~dp0.maven' -Force"
  if errorlevel 1 exit /b 1
  del /q "%MAVEN_ARCHIVE%" >nul 2>&1
)
call "%MAVEN_EXE%" %*
exit /b %errorlevel%
