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
        String userInput;
        try{
            while (true) {
                String serverMessage = in.readUTF();  // read the message from server
                System.out.println(serverMessage); // print out the welcome message

                System.out.println("Please enter your name first: ");

                while (true) {
                    userInput = sc.nextLine();
                    if(userInput.matches("^[A-Za-z]+([ A-Za-z]+)*")) {
                        out.writeUTF(userInput); // send client message to server
                        break;
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

                String alertMessage = in.readUTF();
                if(alertMessage.contains("wait")) {
                    System.out.println(alertMessage);
                }

                sleep(10000); // sleep current thread for 10s

                System.out.println("Game Starts! Please enter a number:");

                while(true) {
                    userInput = sc.nextLine();

                    // input needs be number within 0 to 12 this range
                    if(userInput.matches("^([0-9]|1[012])$")) {
                        out.writeUTF(userInput);
                        String hintMessage = in.readUTF();
                        if(hintMessage.startsWith("C")) {
                            System.out.println(hintMessage);
                            break;
                        } else if(hintMessage.contains("larger")) {
                            System.out.println(hintMessage);
                        } else if(hintMessage.contains("smaller")) {
                            System.out.println(hintMessage);
                        }
//                        String[] arr = hintMessage.split(" ");
//                        if(arr[arr.length-1] == "0") {
//                            System.out.println(in.readUTF());
//                            break;
//                        }
                    }else {
                        System.out.println("Only number between 0 and 12 is allowed!");
                    }
                }

                serverMessage = in.readUTF();
                System.out.println(serverMessage);

                while (true) {
                    userInput = sc.nextLine();

                    if(userInput.equals("p") || userInput.equals("q")) {
                        out.writeUTF(userInput);
                        if(serverMessage.contains("GoodBye")) {
                            System.out.println(serverMessage);
                            System.exit(0);
                            break;
                        }
                        else if(serverMessage.contains("lobby")) {
                            System.out.println(serverMessage);
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
