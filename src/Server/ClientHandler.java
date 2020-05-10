//package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;


public class ClientHandler extends Thread {

    private DataOutputStream out;
    private DataInputStream in;
    private static final int MAX_GUESS = 4;
    private int randomNum;
    private String name;


    public ClientHandler(Socket connection, int randomNum) {
        this.randomNum = randomNum;

        try {
            // get the InputStream and OutputStream from the connection
            out = new DataOutputStream(connection.getOutputStream());
            in = new DataInputStream(connection.getInputStream());
        } catch (IOException e) {
            e.getMessage();
        }
    }

    @Override
    public void run() {
        int numOfChance = MAX_GUESS;
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
                System.out.print(getPlayerName() + " ");

                while (numOfChance > 0) {
                    String guessNum = in.readUTF();
                    // receive client guess number
                    guessedNum = Integer.parseInt(guessNum.trim()); // parse user input to integer
                    if (guessedNum == getRandomNum()) { // if randomly generated number equals to input, player wins
                        out.writeUTF("Congratulation! You got it!");
                        break;
                    } else if (guessedNum > getRandomNum()) {
                        numOfChance--;
                        out.writeUTF("Sorry, the number you guessed " + guessedNum + " is larger than the generated number!\n" +
                                "You number of guess left is " + numOfChance);
                        if (numOfChance == 0) {
                            out.writeUTF("Sorry, you've used up all you chance!\nThe correct answer is " +
                                    getRandomNum());
                            break;
                        }
                    } else {
                        numOfChance--;
                        out.writeUTF("Sorry, the number you guessed " + guessedNum + " is smaller than the generated number!\n" +
                                "You number of guess left is " + numOfChance);
                        if (numOfChance == 0) {
                            out.writeUTF("Sorry, you've used up all you chance!\nThe correct answer is " +
                                    getRandomNum());
                            break;
                        }
                    }
                }

                out.writeUTF("Choose p to play again or q to quit.");
                String userInput = in.readUTF();
                if(userInput.equals("q")) {
                    out.writeUTF("You have quit the game! GoodBye!");
                    System.exit(0);
                    break;
                } else {
                    out.writeUTF("New round will start!");
                    continue;
                }
            }
        }
        catch (SocketException e) {
            e.printStackTrace();
        }
        catch (EOFException e) {
            System.out.println("\nOne player has disconnected from the server!");
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

    public void setPlayerName(String name) {
        this.name = name;
    }

    public int getRandomNum() {
        return randomNum;
    }
}
