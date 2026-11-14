@echo off
setlocal EnableExtensions DisableDelayedExpansion
pushd "%~dp0" || exit /b 1
if not defined MVN set "MVN=mvn"

call "%MVN%" --batch-mode --no-transfer-progress clean verify %*
if errorlevel 1 goto :error

if not exist "target\distribucion" mkdir "target\distribucion"
if errorlevel 1 goto :error
copy /y "estacion-trabajo\target\servidor.jar" "target\distribucion\servidor.jar" >nul
if errorlevel 1 goto :error
copy /y "servidor-web\target\web.war" "target\distribucion\web.war" >nul
if errorlevel 1 goto :error
xcopy "configuracion-ejemplo" "target\distribucion\configuracion-ejemplo\" /e /i /y >nul
if errorlevel 1 goto :error
xcopy "migraciones" "target\distribucion\migraciones\" /e /i /y >nul
if errorlevel 1 goto :error
copy /y "EJECUTAR.md" "target\distribucion\LEEME.md" >nul
if errorlevel 1 goto :error

echo.
echo Listo: target\distribucion\servidor.jar y target\distribucion\web.war
popd
exit /b 0

:error
set "codigo=%errorlevel%"
if "%codigo%"=="0" set "codigo=1"
echo No se pudo completar la distribucion.
popd
exit /b %codigo%
