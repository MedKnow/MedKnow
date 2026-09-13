@echo off
rem ============================================================
rem MedKnow backend regression test launcher (Windows double-click)
rem NOTE: keep this file ASCII-only, cmd.exe reads bat files in GBK
rem ============================================================
rem use bin\bash.exe (launcher) NOT usr\bin\bash.exe - the latter has no MSYS tools on PATH when started from cmd
set "BASH=D:\Program Files\Git\bin\bash.exe"

if not exist "%BASH%" (
  echo [ERROR] Git Bash not found: %BASH%
  echo Please install Git for Windows or edit BASH path in this file.
  pause
  exit /b 1
)

cd /d "%~dp0"
echo ============================================
echo   MedKnow Backend Test (61 assertions)
echo   Requirement: app running on localhost:8090
echo ============================================
"%BASH%" test-api.sh

echo.
pause
