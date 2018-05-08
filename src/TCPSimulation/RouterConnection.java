package TCPSimulation;

import TCPSimulation.Functional.RouterInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingDeque;

/*
    Handles the input and output of a socket between two routers
 */
public class RouterConnection {

    // Parent router connections are from
    Router router;

    // Object Streams for connection
    ObjectOutputStream output;

    // Any packets traveling between routers must be buffered to prevent corruption
    LinkedBlockingDeque<Packet> packetBuffer = new LinkedBlockingDeque<>();

    RouterConnection(Socket connection, Router router,RouterInfo end) throws IOException {
        this.router = router;

        setOutputConnection(connection);

        // For each connection create a thread that will empty the packetBuffer
        new Thread(() ->{
            while(true) {
                try {
                    // take will block until there is a new packet to send
                    output.writeObject(packetBuffer.take());
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // Creates a new input thread for a given socket
    static Thread newInputThread(Router r,Socket s) throws IOException {
        // Build Output stream to stop any blocking
        new ObjectOutputStream(s.getOutputStream());
        ObjectInputStream input = new ObjectInputStream(s.getInputStream());

        // Read all data coming into this socket connection
        Thread inputThread = new Thread(() -> {
            while (true){
                try {
                    r.receive((Packet) input.readObject());
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        inputThread.start();

        return inputThread;
    }

    void setOutputConnection(Socket s) throws IOException {
        output = new ObjectOutputStream(s.getOutputStream());
    }

    void send(Packet p){
        packetBuffer.add(p);
    }

    void close() throws IOException {
        output.close();
    }
}
