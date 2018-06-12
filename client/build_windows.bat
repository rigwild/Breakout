@echo off

echo.

chcp 65001

echo.

echo Applied UTF-8 to the console.

SET newclasspath=".;libs/*;"
echo The following classpath will be used : %newclasspath%

echo.

echo Compiling with java ...

echo.

javac -cp %newclasspath% Main.java

if %errorlevel% == 0 (
	echo.
	echo java -cp .;libs/* Main > start_program.cmd
	echo Compilation successful. You can execute 'start_program.cmd' to start the program.
)