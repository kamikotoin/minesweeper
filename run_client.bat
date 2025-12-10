@echo off

rem Path to JavaFX inside /lib/javafx
set JAVAFX_HOME=lib\javafx

java ^
 --module-path "%JAVAFX_HOME%\lib" ^
 --add-modules javafx.controls,javafx.fxml,javafx.graphics ^
 --enable-native-access=javafx.graphics ^
 -Djava.library.path="%JAVAFX_HOME%\bin" ^
 -cp "out;lib\sqlite-jdbc-3.36.0.3.jar;%JAVAFX_HOME%\lib\*" ^
 client.ClientApp

pause
