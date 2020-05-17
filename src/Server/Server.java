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
                        server.setSoTimeout(5000); // after 10s, server will not accept any connection
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
                            System.out.println("No player is in the lobby! Game has stopped!");
                            server.close();
                            System.exit(0);
                        } else {
                            // check if players in lobby are more than 3, if true, only allow first 3 players to play
                            // if false, all players inside lobby will be taken to game round
                            if (lobby.size() > 3) {
                                for (int i = 0; i < ALLOWED_NUMBER; i++) {
                                    gameRound.add(lobby.get(i));
                                    lobby.remove(); // remove first three elements inside lobby
                                }
                            } else {
                                gameRound.addAll(lobby);
                                lobby.removeAll(lobby);
                            }
                            System.out.println("Time's up! New players will not be accepted!");
                            break;
                        }
                    } catch (SocketException e) {
                        System.out.print("");
                    }
                }

                Long startTime = System.currentTimeMillis();

                while (true) {
                    try {
                        Long endTime = System.currentTimeMillis();
                        if((endTime - startTime)/1000 == 3000) {
                            System.out.println("5-minute mark has reached. Game will stop!");
                            System.exit(0);
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
                            System.out.print("Current Player: ");

                            for(ClientHandler player : gameRound) {
                                player.join(); // wait for all players entering their name
                                System.out.print(player.getPlayerName() + " ");
                            }

                            // start the game round
                            System.out.println("\n---------------------------------------------");
                            setRandomNum(generateRanInt(MIN,MAX));
                            System.out.println("Randomly generated number: " + getRandomNum());
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
                                // check if user input equal q or p
                                if(player.getUserInput() != null) {
                                    if(player.getUserInput().equals("p")) {
                                        lobby.add(player);
                                    }
                                }
                                else {
                                    System.out.println("No user input is found!");
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
