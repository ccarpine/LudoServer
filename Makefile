all:  
	javac -d . -cp ../src ../src/sd/core/register/*.java
	javac -d . -cp ../src ../src/sd/core/player/*.java
	javac -d . -cp ../src ../src/sd/ui/*.java
	javac -d . -cp ../src ../src/sd/util/*.java
	javac -d . -cp ../src ../src/sd/core/*.java
	cp -r ../src/sd/ui/images ./sd/ui/

clean:
	rm -rf sd
	
	
player:
	java sd.core.player.UserPlayer 192.168.1.64

register:
	java sd.core.register.Register
