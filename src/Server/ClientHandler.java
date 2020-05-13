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
    private static int randomNum;
    private Server server;
    private String name;
    private Socket connection;
    private static int numOfChance = 1;
    private static int guessedNum = 0;
    private String userInput;

    public ClientHandler(Socket connection, Server server, String name) {
        try {
            // get the InputStream and OutputStream from the connection
            out = new DataOutputStream(connection.getOutputStream());
            in = new DataInputStream(connection.getInputStream());
            this.server = server;
            this.name = name;
            this.connection = connection;
        } catch (IOException e) {
            e.getMessage();
        }
    }


    @Override
    public void run() {
//        Timer timer;

        try {
            while (true) {
                try {
                    // send messages to client
                    out.writeUTF("Welcome to guessing game! You have maximum four guesses and at each wrong guess" +
                            " you will receive a hint. At the end, server will announce the answer.\n");
                    // check if players have entered their names
                    if(this.name == null) {
                        inputPlayerName();
                    } else {
                        gameRound();
                    }
                    break;
                }
                catch (NullPointerException e) {
                    continue;
                }
            }
        }
        catch (SocketException e) {
            e.printStackTrace();
        }
        catch (EOFException e) {
            if(this.name == null) {
                System.out.println("One player has disconnected from the server!");
            }
            else {
                System.out.println(getPlayerName() + " has disconnected from the server!");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(in != null) in.close();
                if(out != null) out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // client sends name and wait
    public void inputPlayerName() throws IOException {
        // receive client name input
        String playerName = in.readUTF();
        setPlayerName(playerName);
        out.writeUTF("Please wait for other players entering their name.");
    }

    public void gameRound() throws IOException {
        while (numOfChance <= MAX_GUESS) {
            try {
                randomNum = server.getRandomNum(); // set the random number to auto-generated number from the server
                System.out.println(getRandomNum());

                String guessNum = in.readUTF();
                // receive client guess number
                guessedNum = Integer.parseInt(guessNum.trim()); // parse user input to integer
                if (guessedNum == getRandomNum()) { // if randomly generated number equals to input, player wins
                    out.writeUTF("Congratulation! You got it!");
                    break;
                } else if (guessedNum > getRandomNum()) {
                    out.writeUTF("Sorry, guessed number " + guessedNum + " is larger than the generated number!\n" +
                            "Number of guess used is " + numOfChance);
                    numOfChance++;
                } else {
                    out.writeUTF("Sorry, guessed number " + guessedNum + " is smaller than the generated number!\n" +
                            "Number of guess used is " + numOfChance);
                    numOfChance++;
                }
                if (numOfChance == 5) {
                    out.writeUTF("You've used up all you chance! The correct answer is " +
                            getRandomNum());
                }
            } catch (SocketException e) {
                System.out.println(getPlayerName() + " has disconnected from the server!");
            }
        }

//        System.out.println("--------------------------");
        System.out.println(getPlayerName() + " ended the game. " + getPlayerName() + " " + (numOfChance - 1));

        while (true) {
            out.writeUTF("Choose p to play again or q to quit.");
            userInput = in.readUTF();
            if (userInput.equals("q")) {
                out.writeUTF("You have quit the game! GoodBye!");
                System.exit(0);
            } else {
                out.writeUTF("Re-add you to lobby! Please wait...");
            }
            break;
        }

    }

    public Socket getConnection() {
        return connection;
    }

    public String getPlayerName() {
        return name;
    }

    public void setPlayerName(String playerName) {
        this.name = playerName;
    }

    public int getRandomNum() {
        return randomNum;
    }

    public String getUserInput() {
        return userInput;
    }
}
