@echo off
rem Quick test wrapper for gh proxy - compiles and runs latest code
rem This can be symlinked to global PATH for testing

rem Save current directory
set ORIGINAL_DIR=%CD%

rem Switch to project root
pushd "%~dp0.."

rem Run using mvn exec with all arguments passed through, setting user.dir to original directory
call mvn compile exec:java -Dexec.mainClass="xyz.jphil.git_cli_proxy_with_creds.GhProxyMain" -Dexec.args="%*" -Duser.dir="%ORIGINAL_DIR%" -q

rem Restore original directory
popd