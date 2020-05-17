JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
        $(JC) $(JFLAGS) $*.java

CLASSES = \
        Server.java \
        ClientHandler.java \
        ServerTask.java \
        GameLogger.java \
        Client.java \
        ClientTask.java \

default: classes

classes: $(CLASSES:.java=.class)

clean:
        $(RM) *.class