@echo off
taskkill /f /im ${appName}
ping 1.1.1.1 -n 1 -w 1500
del /f "${fileName}"
ren "${fileName}.tmp" "${fileName}"
start "${fileName}"
del %0