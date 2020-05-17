JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	./src/Server/ClientHandler.java \
	./src/Server/ServerTask.java \
	./src/Server/GameLogger.java \
	./src/Client/ClientTask.java \
	./src/Server/Server.java \
	./src/Client/Client.java \

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
