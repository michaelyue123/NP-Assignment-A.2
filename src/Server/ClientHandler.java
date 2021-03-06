//package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;


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
    private static final long DELAY = 10000;
    private static final long INTERVAL = 270000;
    private static int rankPosition;
    private static Logger MyLogger = GameLogger.getLogger();
    private HashMap<Integer, String> playerInfo = new HashMap<>();


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

                    Timer nameInput_timer = new Timer("NameInput Timer");
                    nameInput_timer.scheduleAtFixedRate(new ServerTask(out), DELAY, INTERVAL);
                    // receive client name input
                    String playerName = in.readUTF();
                    nameInput_timer.cancel(); // once receive the user input, stop the timer
                    setPlayerName(playerName);
                }else {
                    if(hasChangeAction == true) {
                        gameRound();
                    }
                }
                break;
            }
        }
        catch (EOFException e) {
            System.out.println("\nOne player has disconnected from the server");
        }
        catch (SocketException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void gameRound() throws IOException, SocketException {
       try {
           out.writeUTF("Game Starts! Please enter a number: ");
           int numOfChance = 1;
           randomNum = server.getRandomNum(); // set the random number to auto-generated number from the server

           while (numOfChance <= MAX_GUESS) {
               try {
                   // create a timer and send message to client if client is not active
                   Timer numberInput_timer = new Timer("NumberInput Timer");
                   numberInput_timer.scheduleAtFixedRate(new ServerTask(out), DELAY, INTERVAL);
                   // receive client guess number
                   String guessNum = in.readUTF();
                   numberInput_timer.cancel(); // once receive user input, stop the timer
                   guessedNum = Integer.parseInt(guessNum.trim()); // parse user input to integer

                   // if randomly generated number equals to input, player wins
                   if (guessedNum == getRandomNum()) {
                       out.writeUTF("Congratulation! You got it!");
                       System.out.println("\n" + getPlayerName() + " got congratulated. " + getPlayerName() + " " + numOfChance + "\n");
                       MyLogger.log(Level.INFO, (getPlayerName() + " got congratulated. " + getPlayerName() + " " + numOfChance));
                       break;
                   //  if guessed number bigger than random number, server sends this message to client
                   } else if (guessedNum > getRandomNum()) {
                       out.writeUTF("Sorry, guessed number " + guessedNum + " is larger than the generated number!\n" +
                               "Number of guess used is " + numOfChance);
                       numOfChance++;
                   //  if guessed number smaller than random number, server sends this message to client
                   } else {
                       out.writeUTF("Sorry, guessed number " + guessedNum + " is smaller than the generated number!\n" +
                               "Number of guess used is " + numOfChance);
                       numOfChance++;
                   }

                   // if player has reached maximum four guesses, server announces the answer
                   if (numOfChance == MAX_GUESS+1) {
                       out.writeUTF("You've used up all you chance! The correct answer is " +
                               getRandomNum());
                       System.out.println("\n" + getPlayerName() + " has ended the game. " + getPlayerName() + " " + numOfChance + "(Loss)\n");
                       MyLogger.log(Level.INFO, (getPlayerName() + " has ended the game. " + getPlayerName() + " " + numOfChance + "(Loss)"));
                   }
               }
               catch (EOFException e) {
                   System.out.println(getPlayerName() + " has quit the game!");
                   break;
               }
           }
           // get the rank position of client
           rankPosition = numOfChance;
           // store name and ranking position into a hashmap
           playerInfo.put(getRankPosition(), getPlayerName());


           while (true) {
              try {
                  out.writeUTF("Choose p to play again or q to quit.");
                  // create a timer and send message to client if client is not active
                  Timer replayInput_timer = new Timer("ReplayInput Timer");
                  replayInput_timer.scheduleAtFixedRate(new ServerTask(out), DELAY, INTERVAL);
                  userInput = in.readUTF();
                  replayInput_timer.cancel(); // once receive user input, stop the timer

                  // if client message equals to q, end this thread
                  if ("q".equals(userInput)) {
                      System.out.println("\n" + getPlayerName() + " has quit the game!\n");
                      MyLogger.log(Level.INFO, (getPlayerName() + " has quit the game!"));
                      in.close();
                      out.close();
                  } else {
                  // if client message equals to p, re-add player to lobby
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

    public int getRankPosition() {
        return rankPosition;
    }

    public HashMap<Integer, String> getPlayerInfo() {
        return playerInfo;
    }
}
