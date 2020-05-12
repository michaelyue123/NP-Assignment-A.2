//package Server;

import java.io.IOException;
import java.net.*;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.concurrent.ThreadLocalRandom;

public class Server {

    private static final int PORT = 61246;
    private Socket connection;
    LinkedList<ClientHandler> gameRound;
    LinkedList<ClientHandler> lobby;
    LinkedList<String> playerList = new LinkedList<>();
    private final int MIN = 0;
    private final int MAX = 12;
    private int randomNum;
    private final int ALLOWED_NUMBER = 3;


    public Server() {
        ServerSocket server = null;

        // initialise two lists
        gameRound = new LinkedList<>();
        lobby = new LinkedList<>();

        try {
            // create a server socket
            server = new ServerSocket(PORT);
            System.out.println("Server starts");
            System.out.println("Waiting for new player loading for 10s");
            setRandomNum(generateRanInt(MIN,MAX));

            while (true) {
                while (true) {
                    try {
                        server.setSoTimeout(10000); // after 10s, server will not accept any connection
                        // accept connection from the client
                        connection = server.accept();

                        if (lobby.size() <= 6) { // Lobby holds maximum 6 players.
                            lobby.add(new ClientHandler(connection, randomNum));
                        } else {
                            System.out.println("Lobby can only hold maximum 6 players!");
                        }
                        System.out.println("One player has joined the game!");

                    } catch (SocketTimeoutException e) {
                        if (lobby.size() == 0) {
                            System.out.println("No player connects to the server! Server has stopped!");
                            System.exit(0);
                        } else {
                            // check if players in lobby are more than 3, if true, only allow first 3 players to play
                            // if false, all players inside lobby will be taken to game round
                            if (lobby.size() > 3) {
                                for (int i = 0; i <= ALLOWED_NUMBER; i++) {
                                    gameRound.add(lobby.get(i));
                                }
                            } else {
                                gameRound.addAll(lobby);
                            }

                            for (int i = 0; i < gameRound.size(); i++) {
                                gameRound.get(i).start(); // start the thread
                            }
                            System.out.println("Time's up! New players will not be accepted!");
                            break;
                        }
                    } catch (SocketException e) {
                        System.out.print("");
                    }
                }

                while (true) {
                    try {
                        server.setSoTimeout(300000); // the whole game will last 5 minutes;
                        System.out.println("---------------------------------------------");
                        System.out.println("Randomly generated number: " + getRandomNum());
                        System.out.println("Current players: ");

                        for (int i = 0; i < gameRound.size(); i++) {
                            gameRound.get(i).join(); // avoid race condition
                            playerList.addAll(gameRound.get(i).getPlayerList()); // store player name into a linkedlist on the server
                        }



                        break;

//                        for(int i=0; i<gameRound.size(); i++) {
//                            gameRound.remove(); // remove all elements inside game round
//                        }
//
//                        if(lobby.size() > 3) {
//                            // remove first three elements inside lobby
//                            for(int i=0; i<=ALLOWED_NUMBER; i++) {
//                                lobby.remove();
//                            }
//                        }else {
//                            // remove all elements inside lobby
//                            for(int i=0; i<=lobby.size(); i++) {
//                                lobby.remove();
//                            }
//                        }


                    } catch (SocketException e) {
                        return;
                    } catch (NoSuchElementException e) {
                        return;
                    }
                }
            }
        }
        catch (BindException e) {
            System.out.println("");
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (IllegalThreadStateException e) {
            System.out.println("Sorry, all players have disconnected from the server. Server has stopped!");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if(server != null) server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
