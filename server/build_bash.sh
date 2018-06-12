newclasspath=".:libs/*"
echo -e "The following classpath will be used : \n$newclasspath\n"

echo -e "java -cp $newclasspath Main.java\n"
echo -e "Compiling with java ...\n"
java -cp $newclasspath Main.java && echo "java -cp .;libs/* Main" > start_program.cmd && echo "Compilation successful. You can execute 'start_program.cmd' to start the program."