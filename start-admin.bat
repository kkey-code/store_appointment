@echo off
setlocal

set "ROOT=%~dp0"
set "BACKEND=%ROOT%store_appointment"
set "FRONTEND=%ROOT%frontend"
set "APP_URL=http://localhost:5173/"

echo Starting Store Appointment admin app...
echo.

where java >nul 2>nul
if errorlevel 1 (
    echo Java was not found. Please install JDK 17 and add it to PATH.
    pause
    exit /b 1
)

where mvn >nul 2>nul
if errorlevel 1 (
    echo Maven was not found. Please install Maven and add it to PATH.
    pause
    exit /b 1
)

where npm >nul 2>nul
if errorlevel 1 (
    echo npm was not found. Please install Node.js and add it to PATH.
    pause
    exit /b 1
)

netstat -ano | findstr /C:":8080" | findstr /C:"LISTENING" >nul
if errorlevel 1 (
    echo Starting backend on port 8080...
    start "Store Backend 8080" /D "%BACKEND%" cmd /k "mvn spring-boot:run -Dspring-boot.run.profiles=portable"
) else (
    echo Backend port 8080 is already running.
)

netstat -ano | findstr /C:":5173" | findstr /C:"LISTENING" >nul
if errorlevel 1 (
    echo Starting frontend on port 5173...
    if exist "%FRONTEND%\node_modules" (
        start "Store Frontend 5173" /D "%FRONTEND%" cmd /k "npm run dev"
    ) else (
        start "Store Frontend 5173" /D "%FRONTEND%" cmd /k "npm install && npm run dev"
    )
) else (
    echo Frontend port 5173 is already running.
)

echo.
echo Waiting a few seconds before opening the browser...
timeout /t 8 /nobreak >nul
start "" "%APP_URL%"

echo.
echo If the page is not ready yet, wait until the frontend window says VITE ready.
echo URL: %APP_URL%
pause
