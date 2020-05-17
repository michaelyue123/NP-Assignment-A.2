
# Guessing Game
Guessing Game is a multi-player game supported by the socket connection established between server and client. Games

starts with server first randomly generating a number between 0~12 and then each player that successfully connects to

the game server can start guessing the number. Each player gets maximum of four guesses for one game round. At each wrong

guess, player receives a hint from the server saying "the guessed number is bigger or smaller than the random

number". If a player correctly guesses the number, the server then announces a message like "Congratulation XXX!". After

four guesses, if player still cannot get the number right, server will then announce the answer.


Importantly, each player connects to the server will be taken to the lobby first. The lobby can hold maximum of six

players in total. Then at each game round, the first three players in the lobby will be taken to the game round and

start playing the game. At the end of each game round, each player will be asked whether he or she wants to play again

or quit the game. If the player decides to quit the game, he or she will be removed from the lobby. However, if the

player decides to play again, he or she will be taken to the lobby first and then wait for a new round to start. The

same rule applies here: each game round will only take the first three players in the lobby.


## Built with
* Java

### Perquisites
* JDK8 or higher version

### Run the project on the terminal

```
cd Project Folder
```

```
cd Server Folder
```

* do this multiple times for client folder on different terminal windows to mimic multiple-player scenario

```
cd Client Folder 
```

```
javac *.java (compile server and client files)
```

launch server first

```
java Server 
```

* do this inside multiple terminal windows

```
java Client 
```

