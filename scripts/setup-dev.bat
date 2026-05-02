@echo off
echo Setting up project...
echo.

echo Checking tools...
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java not found
    pause
    exit /b 1
)

mvn -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Maven not found
    pause
    exit /b 1
)

node --version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Node.js not found
    pause
    exit /b 1
)

echo Tools found!
echo.

echo Building...
call mvn --%% -f shared\pom.xml clean install
if errorlevel 1 (
    echo ERROR: Build failed
    pause
    exit /b 1
)

call mvn --%% -f agent\pom.xml clean package
if errorlevel 1 (
    echo ERROR: Build failed
    pause
    exit /b 1
)

call mvn --%% -f server\pom.xml clean package
if errorlevel 1 (
    echo ERROR: Build failed
    pause
    exit /b 1
)

echo Installing deps...
cd client-web
call npm install
if errorlevel 1 (
    echo ERROR: Install failed
    pause
    exit /b 1
)
cd ..

echo.
echo Done!
echo.
echo Start project:
echo   1. mvn --%% -f server\pom.xml spring-boot:run
echo   2. java -jar agent\target\agent-1.0-SNAPSHOT.jar
echo   3. cd client-web && npm run dev
echo.
echo Dashboard: http://localhost:3000
pause
