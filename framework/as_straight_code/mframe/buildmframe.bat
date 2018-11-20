@ECHO OFF

REM "C:\Program Files\Java\jdk1.8.0_181\bin\javac" -cp ./src;./src/utility;./src/userinterface src/TUCGrader.java
REM javac -classpath ./src/com/troutmoon/mframe -d ./classes 
REM javac -d ./classes com/troutmoon/mframe/*.java
REM "C:\Program Files\Java\jdk1.8.0_181\bin\javac" com.troutmoon.mframe


REM rmdir /Q /S classes
mkdir classes

cd src
javac -d ../classes -classpath ./lib/j3dutils.jar com/troutmoon/mframe/*.java
cd ..

cd classes
jar cfm ../mframe.jar ../Manifest.txt com/troutmoon/mframe/*.class
cd ..

rmdir /Q /S classes