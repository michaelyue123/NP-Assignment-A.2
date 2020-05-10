//package Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.Timer;

public class Client {

    private static final String HOST = "localhost";
    private static final int PORT = 61246;
    private Socket client;
    private Scanner sc;
    private DataInputStream in;
    private DataOutputStream out;


    public Client() {
        try {
            client = new Socket(HOST, PORT); // client socket with host and port

            // get the InputStream and OutputStream from the connection
            in = new DataInputStream(client.getInputStream());
            out = new DataOutputStream(client.getOutputStream());
            // user input
            sc = new Scanner(System.in);

            while(true) {
                try{
                    String serverMessage1 = in.readUTF();  // read the message from server
                    System.out.println(serverMessage1); // print out the welcome message

                    System.out.println("Please enter your name first: ");

//                    // create a timer to receive server message every 10s if client is not active
//                    Timer timer = new Timer("Client Timer");
//                    long delay = 10000;
//                    long interval = 10000;
//                    timer.scheduleAtFixedRate(new ClientTask(in), delay, interval);

                    String userInput = sc.nextLine();
//                    timer.cancel(); // stop timer once there is user input

                    while(true) {
                        if(userInput.matches("^[A-Za-z]+([ A-Za-z]+)*")) {
                            out.writeUTF(userInput); // send client message to server
                            break;
                        }else {
                            System.out.println("Only letters are allowed!");
                            userInput = sc.nextLine();
                        }
                    }

                    System.out.println("Game Starts! Please enter a number:");

                    while(true) {
                        userInput = sc.nextLine();
                        // input needs be number within 0 to 12 this range
                        if(userInput.matches("^([0-9]|1[012])$")) {
                            out.writeUTF(userInput);
                            String serverMessage2 = in.readUTF();
                            if(serverMessage2.startsWith("C")) {
                                System.out.println(serverMessage2);
                                break;
                            } else if(serverMessage2.contains("larger")) {
                                System.out.println(serverMessage2);
                                if(serverMessage2.contains("used")) {
                                    System.out.println(serverMessage2);
                                    break;
                                }
                            } else if(serverMessage2.contains("smaller")) {
                                System.out.println(serverMessage2);
                                if(serverMessage2.contains("used")) {
                                    System.out.println(serverMessage2);
                                    break;
                                }
                            }
                        }else {
                            System.out.println("Only numbers between 0 and 12 are allowed!");
                        }
                    }

                    while (true) {
                        String serverMessage3 = in.readUTF();
                        if(serverMessage3.contains("choose")) {
                            System.out.println(serverMessage3);
                            userInput = sc.nextLine();

                            if(!userInput.equals("p") || !userInput.equals("q")) {
                                continue;
                            }
                            else {
                                out.writeUTF(userInput);
                                if(serverMessage3.contains("GoodBye")) {
                                    System.out.println(serverMessage3);
                                    System.exit(0);
                                    break;
                                }
                                else if(serverMessage3.contains("New")) {
                                    System.out.println(serverMessage3);
                                    break;
                                }
                            }
                        }
                    }
                }
                catch (IOException e) {
                    e.getMessage();
                }
            }
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
        new Client();
    }
}
