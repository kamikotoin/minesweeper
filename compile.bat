@echo off
setlocal enabledelayedexpansion

echo Creating output directory...
if not exist out mkdir out

echo Compiling...

javac --module-path lib/javafx/lib ^
      --add-modules javafx.controls,javafx.fxml ^
      -cp "lib/*;." ^
      -d out ^
      src/common/*.java ^
      src/client/*.java ^
      src/server/*.java ^
      src/server/dao/*.java

if errorlevel 1 (
    echo.
    echo ==========================
    echo COMPILATION FAILED!
    echo ==========================
    pause
    exit /b
)

echo.
echo ==========================
echo COMPILATION SUCCESSFUL
echo ==========================

echo Copying resources...
xcopy src\client\views out\client\views /E /Y >nul

echo Done.
