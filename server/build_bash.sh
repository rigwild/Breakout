newclasspath=".:libs/*"
echo -e "The following classpath will be used : \n$newclasspath\n"

echo -e "javac -cp $newclasspath Main.java\n"
echo -e "Compiling with java ...\n"
java -cp $newclasspath Main.java && echo "java -cp $newclasspath Main" > start_program.sh && chmod u+x start_program.sh &&  echo "Compilation successful. You can execute './start_program.sh' to start the program."