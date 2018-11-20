@ECHO OFF

rmdir /Q /S classes
mkdir classes

copy ..\..\..\framework\as_straight_code\mframe\mframe.jar .\lib

REM cd src
javac -d classes -classpath ./;./lib/mframe.jar ChumChumMFGame.java
