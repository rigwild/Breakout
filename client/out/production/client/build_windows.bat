@echo off

echo.

chcp 65001

echo.

SET newclasspath=".;%CLASSPATH%;./libs/MG2D.jar;./libs/java_websocket.jar"
echo Classpath système : %CLASSPATH%
echo Classpath appliqué : %newclasspath%

echo.

echo Compilation en cours de %1.java ...

echo.

javac -cp %newclasspath% %1.java

if %errorlevel% == 0 (
	echo.

	echo Compilation réussie. Démarrage de la commande : java %1 ...
	java -cp %newclasspath% %1
) else (
	echo.

	echo Erreur de compilation. Arrêt du programme.
)