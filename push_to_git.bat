@echo off
setlocal

:: Generate default commit message with date and time
for /f "tokens=1-2 delims==." %%I in ('wmic os get localdatetime /value') do set datetime=%%J
set msg=Auto-commit %datetime:~0,4%-%datetime:~4,2%-%datetime:~6,2% %datetime:~8,2%:%datetime:~10,2%:%datetime:~12,2%

git add .
git commit -m "%msg%"
git push

pause 