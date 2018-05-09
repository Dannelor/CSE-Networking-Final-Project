/* 
Mason Beckham 1001073976
Minh-Quan Nguyen 1001032212
*/

package TCPSimulation;

import TCPSimulation.Functional.RouterInfo;

import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;

public class ConnectionRunnable implements Runnable {

    // TCPSimulation.Router that is attempting the connection
    Router host;
    // The new router we are attempting to connect to
    RouterInfo connecting;

    public ConnectionRunnable(Router host, RouterInfo connecting) {
        this.host = host;
        this.connecting = connecting;
    }

    @Override
    public void run() {
        int tries = 0;
        while(tries <= 5){
            try {
                System.out.println("Attempting connection to: " + connecting.getStringID());
                Socket s = new Socket("localhost",connecting.getPort());
                System.out.println("Successfully established connection to: " + connecting.getStringID());

                try {
                    host.connections.put(connecting, new RouterConnection(s, host,connecting));
                    return;
                }catch(EOFException |SocketException e){
                    System.out.println("Connection failed on " + connecting.getStringID());
                    host.connections.remove(connecting);
                }
            } catch(ConnectException e){
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                tries++;
            } catch (IOException e) {
                e.printStackTrace();
            }
            tries++;
        }
    }
}
