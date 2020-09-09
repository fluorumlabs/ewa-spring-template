@echo off
cd src\main\frontend
where pnpm.cmd >nul 2>&1 && pnpm %* || npm %*
