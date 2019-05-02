PHONY = run exe clean

CLASSPATH = .:./classes/:json-simple-1.1.1.jar

make: 
	javac -cp $(CLASSPATH) -d . application/*.java
	java -cp $(CLASSPATH) application.Main

jar: 
	jar cvmf manifest.txt executable.jar .

runjar:
	java -Xbootclasspath/a:json-simple-1.1.1.jar: -jar executable.jar


clean:
	\rm application/*.class
	\rm executable.jar


