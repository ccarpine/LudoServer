all:  
	javac -d ./bin -sourcepath src/ -cp ./includedJARS/*.jar ./src/sd/core/register/*.java
	javac -d ./bin -sourcepath src/ -cp ./includedJARS/*.jar ./src/sd/core/player/*.java
	javac -d ./bin -sourcepath src/ -cp ./includedJARS/*.jar ./src/sd/ui/*.java
	javac -d ./bin -sourcepath src/ -cp ./includedJARS/*.jar ./src/sd/util/*.java
	javac -d ./bin -sourcepath src/ -cp ./includedJARS/*.jar ./src/sd/core/*.java

	cp -r src/sd/ui/images ./bin/sd/ui/

clean:
	rm -rf ./bin/sd
	
	
player:
	cd bin/;java -cp tablelayout.jar sd.core.player.UserPlayer 192.168.1.64

register:
	cd bin/;java sd.core.register.Register
