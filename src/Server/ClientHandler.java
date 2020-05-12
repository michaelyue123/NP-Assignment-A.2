//package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;


public class ClientHandler extends Thread {

    private DataOutputStream out;
    private DataInputStream in;
    private static final int MAX_GUESS = 4;
    private int randomNum;
    private String name;
    LinkedList<String> playerList = new LinkedList<>();


    public ClientHandler(Socket connection, int randomNum) {
        try {
            // get the InputStream and OutputStream from the connection
            out = new DataOutputStream(connection.getOutputStream());
            in = new DataInputStream(connection.getInputStream());
            this.randomNum = randomNum;
        } catch (IOException e) {
            e.getMessage();
        }
    }

    @Override
    public void run() {
        int numOfChance = 1;
        int guessedNum;
//        Timer timer;

        try {
            while(true) {
                // send messages to client
                out.writeUTF("Welcome to guessing game! You have maximum four guesses and at each wrong guess" +
                        " you will receive a hint. At the end, server will announce the answer.\n");

                // receive client name input
                String playerName = in.readUTF();
                setPlayerName(playerName);
                playerList.add(playerName);
                System.out.println("* " + getPlayerName());

                out.writeUTF("Please wait a few seconds for other players to enter their name.");


                while (numOfChance <= MAX_GUESS) {
                    try{
                        String guessNum = in.readUTF();
                        // receive client guess number
                        guessedNum = Integer.parseInt(guessNum.trim()); // parse user input to integer
                        if (guessedNum == getRandomNum()) { // if randomly generated number equals to input, player wins
                            out.writeUTF("Congratulation! You got it!");
                            break;
                        } else if (guessedNum > getRandomNum()) {
                            out.writeUTF("Sorry, the number you guessed " + guessedNum + " is larger than the generated number!\n" +
                                    "You number of guess used is " + numOfChance);
                            numOfChance++;
                        } else {
                            out.writeUTF("Sorry, the number you guessed " + guessedNum + " is smaller than the generated number!\n" +
                                    "You number of guess used is " + numOfChance);
                            numOfChance++;
                        }

                        if (numOfChance == 0) {
                            out.writeUTF("Sorry, you've used up all you chance!\nThe correct answer is " +
                                    getRandomNum());
                            break;
                        }
                    }
                    catch (SocketException e) {
                        System.out.println(getPlayerList().get(0) + " has disconnected from the server!");
                    }
                }

                System.out.println("--------------------------");
                System.out.println(getPlayerList().get(0) + " ended the game. " + getPlayerList().get(0) + " " + (numOfChance-1));


                while (true) {
                    out.writeUTF("Choose p to play again or q to quit.");
                    String userInput = in.readUTF();
                    if (userInput.equals("q")) {
                        out.writeUTF("You have quit the game! GoodBye!");
                        return;
                    } else {
                        out.writeUTF("You will be taken to lobby first!");
                        break;
                    }
                }
            }
        }
        catch (EOFException e) {
            if(playerList.size() != 0) {
                System.out.println(getPlayerList().get(0) + " has disconnected from the server!");
            }else {
                System.out.println("One player has disconnected from the server!");
            }
            return;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if(in != null) in.close();
                if(out != null) out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getPlayerName() {
        return name;
    }

    public void setPlayerName(String playerName) {
        this.name = playerName;
    }

    public LinkedList<String> getPlayerList() {
        return playerList;
    }

    public int getRandomNum() {
        return randomNum;
    }
}
