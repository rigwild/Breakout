newclasspath=".:$CLASSPATH:./libs/MG2D.jar:./libs/java_websocket.jar"
echo -e "The following classpath will be used : \n$newclasspath\n"

echo -e "java -cp $newclasspath Main.java\n"
echo -e "Compiling with java ...\n"
java -cp $newclasspath Main.java && echo "Compilation successful. Use java Main to start the program."