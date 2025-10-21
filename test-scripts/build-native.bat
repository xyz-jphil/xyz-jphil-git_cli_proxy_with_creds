@echo off
rem change directory to correct directory where we have the pom.xml 
rem cd /d %coderepos%\xyz-jphil\xyz-jphil-git_cli_proxy_with_creds
cd..
echo [1/3] Setting up Visual Studio environment from VSBT portable ...
call %userapps%\vsbt_2022\devcmd.bat

echo [2/3] Building native executables...
call mvnd clean package -Pnative

if %ERRORLEVEL% NEQ 0 (
    echo Native build failed with error code %ERRORLEVEL%
    exit /b %ERRORLEVEL%
)

echo [3/3] Testing executables...
echo.
echo Testing git.exe:
target\git.exe

echo.
echo Testing gh.exe:
target\gh.exe

echo.
echo Build completed successfully!
echo Executables created:
dir target\*.exe /b
