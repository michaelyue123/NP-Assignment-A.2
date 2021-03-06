//package Server;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    private static final int PORT = 61246;
    private Socket connection;
    LinkedList<ClientHandler> gameRound;
    LinkedList<ClientHandler> lobby;
    private static final int MIN = 0;
    private static final int MAX = 12;
    private static int randomNum;
    private static final int ALLOWED_NUMBER = 3;
    private static Logger MyLogger = GameLogger.getLogger();
    private TreeMap<Integer, String> playerInfo = new TreeMap<>();


    public Server() {
        ServerSocket server = null;

        // initialise two lists
        gameRound = new LinkedList<>();
        lobby = new LinkedList<>();

        try {
            // create a server socket
            server = new ServerSocket(PORT);
            MyLogger.log(Level.INFO, "Server starts");
            System.out.println("Server starts\n");

            MyLogger.log(Level.INFO, "Waiting for new player loading for 10s");
            System.out.println("Waiting for new player loading for 10s\n");

            while (true) {
                while (true) {
                    try {
                        server.setSoTimeout(20000); // after 20s, server will not accept any connection
                        // accept connection from the client
                        connection = server.accept();

                        if (lobby.size() <= 6) { // Lobby holds maximum 6 players.
                            ClientHandler clientHandler = new ClientHandler(connection, this, null);
                            lobby.add(clientHandler);
                        } else {
                            MyLogger.log(Level.INFO, "Lobby can only hold maximum 6 players!");
                            System.out.println("Lobby can only hold maximum 6 players!\n");
                        }
                        System.out.println("One player has joined the game!\n");
                        MyLogger.log(Level.INFO, "One player has joined the game!\n");

                    } catch (SocketTimeoutException e) {
                        if (lobby.size() == 0) {
                            System.out.println("No player is in the lobby! Game has stopped!");
                            MyLogger.log(Level.INFO, "No player is in the lobby! Game has stopped!\n");
                            server.close();
                            System.exit(0);
                        } else {
                            // check if players in lobby are more than 3, if true, only allow first 3 players to play
                            // if false, all players inside lobby will be taken to game round
                            if (lobby.size() > 3) {
                                for (int i = 0; i < ALLOWED_NUMBER; i++) {
                                    gameRound.add(lobby.get(i));
                                }
                                for (int i=0; i<ALLOWED_NUMBER; i++) {
                                    lobby.remove(); // remove first three elements inside lobby
                                }
                            } else {
                                gameRound.addAll(lobby);
                                lobby.removeAll(lobby); // remove all elements inside lobby
                            }


                            System.out.println("Time's up! New players will not be accepted!\n");
                            MyLogger.log(Level.INFO, "Time's up! New players will not be accepted!\n");
                            break;
                        }
                    } catch (SocketException e) {
                        System.out.print("");
                    }
                }

                Long startTime = System.currentTimeMillis();

                while (true) {
                    try {
                        // create start time and end time to control server expire time
                        Long endTime = System.currentTimeMillis();
                        if((endTime - startTime)/1000 == 300) {
                            System.out.println("5-minute mark has reached. Game will stop!");
                            MyLogger.log(Level.INFO, "5-minute mark has reached. Game will stop!");
                            System.exit(0);
                            server.close();
                        }

                        System.out.println("---------------------------------------------");

                        for (ClientHandler player : gameRound) {
                            // check if player in game round has a name
                            if(player.getPlayerName() == null) {
                                try {
                                    player.start();
                                } catch (IllegalThreadStateException e) {
                                    player.run();
                                }
                            }else {
                                // has a name then break the loop
                                break;
                            }
                        }

                        while (true) {
                            System.out.print("Current Player: \n");

                            for(ClientHandler player : gameRound) {
                                player.join(); // wait for all players entering their name
                                System.out.print("* " + player.getPlayerName() + "\n");
                                MyLogger.log(Level.INFO, (player.getPlayerName() + " "));
                            }

                            // start the game round
                            System.out.println("\n---------------------------------------------");
                            setRandomNum(generateRanInt(MIN,MAX));
                            System.out.println("Randomly generated number: " + getRandomNum() + "\n");
                            MyLogger.log(Level.INFO, ("Randomly generated number: " + getRandomNum()));
                            int n = 0;
                            for(ClientHandler player : gameRound) {
                                // reset game round with newly updated players
                                ClientHandler readyPlayer = new ClientHandler(player.getConnection(), this, player.getPlayerName());
                                gameRound.set(n, readyPlayer);
                                n++;
                            }

                            for(ClientHandler player : gameRound) {
                                player.changeAction(); // switch to game round
                                player.start();
                            }

                            for(ClientHandler player : gameRound) {
                                player.join();
                                playerInfo.putAll(player.getPlayerInfo()); // copy player info from clientHandler to server
                                // TreeMap is naturally sorted
                            }

                            System.out.println("Final Ranking: ");
                            for(Map.Entry<Integer, String> entry : playerInfo.entrySet()) {
                                System.out.println(entry.getKey() + " "+ entry.getValue());
                            }

                            playerInfo.clear(); // clear TreeMap

                            for(int i=0; i<gameRound.size(); i++) {
                                // check if user input equal q or p
                                if(gameRound.get(i).getUserInput() != null) {
                                    if(gameRound.get(i).getUserInput().equals("p")) {
                                        lobby.add(gameRound.get(i));
                                    }
                                }
                            }
                            gameRound.removeAll(gameRound); // empty the game round

                            // move players from lobby to game round or otherwise end the game if there is no player in the lobby.
                            if(lobby.size() > 0 && lobby.size() <=3) {
                                gameRound.addAll(lobby); // move all players from lobby to game round
                                lobby.removeAll(lobby);
                            }
                            else if(lobby.size() > 3) {
                                for(int i=0; i< ALLOWED_NUMBER; i++) {
                                    gameRound.add(lobby.get(i)); // move first three players from lobby to game round
                                }
                                for(int i=0; i<ALLOWED_NUMBER; i++) {
                                    lobby.remove(); // remove first three elements inside lobby
                                }
                            }
                            break;
                        }
                        if(gameRound.size() ==0) {
                            break;
                        }
                    }
                     catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                     catch (NoSuchElementException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        catch (BindException e) {
            e.printStackTrace();
        }
        catch (IllegalThreadStateException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getRandomNum() {
        return randomNum;
    }

    public void setRandomNum(int randomNum) {
        this.randomNum = randomNum;
    }

    // generate random number
    public int generateRanInt (int min, int max){
        int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
        return randomNum;
    }

    public static void main(String[] args) {
        new Server();
    }
}
