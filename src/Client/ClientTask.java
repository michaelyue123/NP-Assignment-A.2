//package Client;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.TimerTask;

public class ClientTask extends TimerTask {

    private DataInputStream inputFromServer;

    public ClientTask (DataInputStream inputFromServer) {
        this.inputFromServer = inputFromServer;
    }

    @Override
    public void run() {
        try {
            // print the message from server
            inputFromServer.readUTF();
            System.out.println("Are you still there?");
        }
        catch (EOFException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
