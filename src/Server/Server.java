//package Server;

import java.io.EOFException;
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
    private static final int MIN = 0;
    private static final int MAX = 12;
    private static int randomNum;
    private static final int ALLOWED_NUMBER = 3;


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

            while (true) {
                while (true) {
                    try {
                        server.setSoTimeout(10000); // after 10s, server will not accept any connection
                        // accept connection from the client
                        connection = server.accept();

                        if (lobby.size() <= 6) { // Lobby holds maximum 6 players.
                            ClientHandler clientHandler = new ClientHandler(connection, this, null);
                            lobby.add(clientHandler);
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
                                    lobby.remove(); // remove first three elements inside lobby
                                }
                            } else {
                                gameRound.addAll(lobby);
                                for(int i=0; i<=lobby.size(); i++) {
                                    lobby.remove(); // remove all elements inside lobby
                                }
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
                        System.out.println("---------------------------------------------");

                        for (ClientHandler player : gameRound) {
                            player.start();
                        }

                        int n = 0;
                        System.out.print("Current Player: ");
                        for(ClientHandler player : gameRound) {
                            player.join(); // wait for all players entering their name

                            System.out.print(player.getPlayerName() + " ");
                            // reset game round with newly updated players
                            ClientHandler readyPlayer = new ClientHandler(player.getConnection(), this, player.getPlayerName());
                            gameRound.set(n, readyPlayer);
                            n++;
                        }

//                        server.setSoTimeout(300000); // the whole game will last 5 minutes;
                        setRandomNum(generateRanInt(MIN,MAX));
                        System.out.println("\nRandomly generated number: " + getRandomNum());

                        for(ClientHandler player : gameRound) {
                            player.startGameRound();
                            player.join(); // wait for all players finishing their game round
                        }

                        
                        break;
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
            System.out.println("");
        }
        catch (IllegalThreadStateException e) {
            e.printStackTrace();
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
