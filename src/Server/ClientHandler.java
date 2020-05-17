//package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;


public class ClientHandler extends Thread {

    private DataOutputStream out;
    private DataInputStream in;
    private static final int MAX_GUESS = 4;
    private static int randomNum = 0;
    private Server server;
    private String name = null;
    private Socket connection;
    private static int guessedNum = 0;
    private String userInput;
    private boolean hasChangeAction = false;

    public ClientHandler(Socket connection, Server server, String name) {
        try {
            // get the InputStream and OutputStream from the connection
            out = new DataOutputStream(connection.getOutputStream());
            in = new DataInputStream(connection.getInputStream());
            this.server = server;
            this.name = name;
            this.connection = connection;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
//        Timer timer;

        try {
            while (true) {
                // check if players have entered their names
                if(this.name == null) {
                    // send messages to client
                    out.writeUTF("Welcome to guessing game! You have maximum four guesses and at each wrong guess" +
                            " you will receive a hint. At the end, server will announce the answer.\n");

                    inputPlayerName();
                }else {
                    if(hasChangeAction == true) {
                        gameRound();
                    }
                }
                break;
            }
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }
        catch (SocketException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // client sends name and wait
    public void inputPlayerName() throws IOException {
        // receive client name input
        String playerName = in.readUTF();
        setPlayerName(playerName);
    }

    public void gameRound() throws IOException, SocketException {
       try {
           out.writeUTF("Game Starts! Please enter a number: ");
           int numOfChance = 0;
           randomNum = server.getRandomNum(); // set the random number to auto-generated number from the server
           while (numOfChance < MAX_GUESS) {
               try {
                   // receive client guess number
                   String guessNum = in.readUTF();
                   guessedNum = Integer.parseInt(guessNum.trim()); // parse user input to integer
                   if (guessedNum == getRandomNum()) { // if randomly generated number equals to input, player wins
                       out.writeUTF("Congratulation! You got it!");
                       System.out.println(getPlayerName() + " has ended the game. " + getPlayerName() + " " + (numOfChance + 1));
                       break;
                   } else if (guessedNum > getRandomNum()) {
                       out.writeUTF("Sorry, guessed number " + guessedNum + " is larger than the generated number!\n" +
                               "Number of guess used is " + (numOfChance + 1));
                       numOfChance++;
                   } else {
                       out.writeUTF("Sorry, guessed number " + guessedNum + " is smaller than the generated number!\n" +
                               "Number of guess used is " + (numOfChance + 1));
                       numOfChance++;
                   }

                   if (numOfChance == MAX_GUESS) {
                       out.writeUTF("You've used up all you chance! The correct answer is " +
                               getRandomNum());
                       System.out.println(getPlayerName() + " has ended the game. " + getPlayerName() + " " + numOfChance + "(Loss)");
                   }
               }
               catch (EOFException e) {
                   System.out.println(getPlayerName() + " has quit the game!");
                   break;
               }
           }


           while (true) {
              try {
                  out.writeUTF("Choose p to play again or q to quit.");
                  userInput = in.readUTF();
                  if ("q".equals(userInput)) {
                      System.out.println(getPlayerName() + " has quit the game!");
                  } else {
                      out.writeUTF("Re-add you to lobby! Please wait...");
                  }
                  break;
              }
              catch (EOFException e) {
                  System.out.println(getPlayerName() + " has quit the game!");
                  break;
              }
           }
       }
       catch (SocketException e) {
           System.out.println("\n" + getPlayerName() + " has disconnected from the server!");
           return;
       }
    }
    public void changeAction() {
        this.hasChangeAction = true;
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