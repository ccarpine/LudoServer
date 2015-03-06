all:  
	javac -d ./bin -sourcepath src/ -cp ./libs/*.jar ./src/sd/core/register/*.java
	javac -d ./bin -sourcepath src/ -cp ./libs/*.jar ./src/sd/core/player/*.java
	javac -d ./bin -sourcepath src/ -cp ./libs/*.jar ./src/sd/ui/*.java
	javac -d ./bin -sourcepath src/ -cp ./libs/*.jar ./src/sd/util/*.java
	javac -d ./bin -sourcepath src/ -cp ./libs/*.jar ./src/sd/core/*.java

	cp -r src/sd/ui/images ./bin/sd/ui/

clean:
	rm -rf ./bin/sd

