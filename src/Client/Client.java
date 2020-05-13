//package Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.Timer;

public class Client extends Thread {

    private static final String HOST = "localhost";
    private static final int PORT = 61246;
    private Socket client;
    private Scanner sc;
    private DataInputStream in;
    private DataOutputStream out;
    private static final int MAX_GUESS = 4;
    private String userInput;


    public Client() {
        try {
            client = new Socket(HOST, PORT); // client socket with host and port

            // get the InputStream and OutputStream from the connection
            in = new DataInputStream(client.getInputStream());
            out = new DataOutputStream(client.getOutputStream());
            // user input
            sc = new Scanner(System.in);
        }
        catch (IOException e) {
            e.getMessage();
        }
    }

    public void sendNameInput() throws IOException {
        while (true) {
            userInput = sc.nextLine();
            if(userInput.matches("^[A-Za-z]+([ A-Za-z]+)*")) {
                out.writeUTF(userInput); // send client message to server
                break;
            }else {
                System.out.println("Only letters are allowed!");
            }
        }
    }

    @Override
    public void run() {
        int numOfChance = 0;
        try{
            while (true) {
                String welcomeMessage = in.readUTF();  // read the message from server
                System.out.println(welcomeMessage); // print out the welcome message

                System.out.println("Please enter your name first: ");

                sendNameInput();


//                    // create a timer to receive server message every 10s if client is not active
//                    Timer timer = new Timer("Client Timer");
//                    long delay = 10000;
//                    long interval = 10000;
//                    timer.scheduleAtFixedRate(new ClientTask(in), delay, interval);

//                    timer.cancel(); // stop timer once there is user input

//                serverMessage = in.readUTF();

                String alertMessage = in.readUTF();
                if(alertMessage.contains("wait")) {
                    System.out.println(alertMessage);
                }

                sleep(5000); // sleep current thread for 10s

                System.out.println("Game Starts! Please enter a number:");

                while(numOfChance < MAX_GUESS) {
                    userInput = sc.nextLine();

                    // input needs be number within 0 to 12 this range
                    if(userInput.matches("^([0-9]|1[012])$")) {
                        out.writeUTF(userInput);
                        String hintMessage = in.readUTF();
                    // if server message starts with C, it then congratulates player and player leaves the game round.
                        if(hintMessage.startsWith("C")) {
                            System.out.println(hintMessage);
                            break;
                        } else if(hintMessage.contains("larger")) {
                    // if server message contains larger, it then prints out the message and player leaves the game round.
                            System.out.println(hintMessage);
                            numOfChance++;
                            // if player has reached maximum number of guess, the game will end
                            if(numOfChance == 4) {
                                System.out.println(in.readUTF());
                                break;
                            }
                        } else if(hintMessage.contains("smaller")) {
                    // if server message contains smaller, it then prints out the message and player tries again.
                            System.out.println(hintMessage);
                            numOfChance++;
                    // if player has reached maximum number of guess, server will force player out of game round
                            if(numOfChance == 4) {
                                System.out.println(in.readUTF());
                                break;
                            }
                        }
                    }else {
                        System.out.println("Only number between 0 and 12 is allowed!");
                    }
                }

                // print out server message to ask whether player wants to play again or not
                String replayMessage = in.readUTF();
                System.out.println(replayMessage);

                while (true) {
                    userInput = sc.nextLine();

                    // only p or q is allowed
                    if(userInput.equals("p") || userInput.equals("q")) {
                        out.writeUTF(userInput);
                        if(replayMessage.contains("GoodBye")) {
                            // if user input is q, the game will be completely stopped
                            System.out.println(replayMessage);
                            System.exit(0);
                            break;
                        }
                        // if user input is p, player will be re-add to the lobby and wait for new round to start
                        else if(replayMessage.contains("lobby")) {
                            System.out.println(replayMessage);
                            break;
                        }
                    }else {
                        System.out.println("Only p or q is allowed!");
                    }
                }
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.getMessage();
        }
        finally {
            try {
                if(client != null) client.close();
                if(in != null) in.close();
                if(out != null) out.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new Client().start();
    }
}
