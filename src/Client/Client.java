//package Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
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
    private String name;


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

    @Override
    public void run() {
        try{
            // read the message from server
            System.out.println(in.readUTF()); // print out the welcome message

            while (true) {
                if(this.name == null) {
                    System.out.println("Please enter your name first: ");
                    userInput = sc.nextLine();
                    if(userInput.matches("^[A-Za-z]+([ A-Za-z]+)*")) {
                        setPlayerName(userInput);
                        out.writeUTF(userInput); // send client message to server
                    }else {
                        System.out.println("Only letters are allowed!");
                    }
                }


//                    // create a timer to receive server message every 10s if client is not active
//                    Timer timer = new Timer("Client Timer");
//                    long delay = 10000;
//                    long interval = 10000;
//                    timer.scheduleAtFixedRate(new ClientTask(in), delay, interval);

//                    timer.cancel(); // stop timer once there is user input

//                serverMessage = in.readUTF();


                // Game start message received from the server
                System.out.println(in.readUTF());

                int numOfChance = 0;

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
                        }
                        else if(hintMessage.contains("larger")) {
                    // if server message contains larger, it then prints out the message and player leaves the game round.
                            System.out.println(hintMessage);
                            numOfChance++;
                        }
                        else if(hintMessage.contains("smaller")) {
                    // if server message contains smaller, it then prints out the message and player tries again.
                            System.out.println(hintMessage);
                            numOfChance++;
                        }
                    }else {
                        System.out.println("Only number between 0 and 12 is allowed!");
                    }
                    // if player has reached maximum number of guess, the game will end
                    if(numOfChance == MAX_GUESS) {
                        System.out.println(in.readUTF());
                    }
                }


                // print out server message to ask whether player wants to play again or not
                String replayMessage = in.readUTF();
                System.out.println(replayMessage);

                while (true) {
                    userInput = sc.nextLine();

                    // only p or q is allowed
                    if("p".equals(userInput)) {
                        out.writeUTF(userInput);
                        System.out.println(in.readUTF());
                        break;
                    }
                    else if("q".equals(userInput)) {
                        out.writeUTF(userInput);
                        System.out.println("You have quit the game! GoodBye!");
                        in.close();
                        out.close();
                        sc.close();
                        System.exit(0);
                        break;
                    }
                    else {
                        System.out.println("Only p or q is allowed!");
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPlayerName(String name) {
        this.name = name;
    }

    public static void main(String[] args) {
        new Client().start();
    }
}
