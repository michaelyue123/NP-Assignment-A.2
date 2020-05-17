//package Server;

import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;
import java.util.TimerTask;

public class ServerTask extends TimerTask {

    private DataOutputStream outPutToClient;

    public ServerTask (DataOutputStream outPutToClient) {
        this.outPutToClient = outPutToClient;
    }

    @Override
    public void run() {
        try {
            // send message to Client
            outPutToClient.writeUTF("Are you still there?");
        }
        catch (SocketException e) {
            System.out.println("");
        }
        catch (EOFException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
