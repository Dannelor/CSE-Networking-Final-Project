/* 
Mason Beckham 1001073976
Minh-Quan Nguyen 1001032212
*/

package TCPSimulation.AgentStories;

import TCPSimulation.Functional.RouterInfo;
import TCPSimulation.Packet;
import TCPSimulation.Router;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class HeadquartersStory{

    ObjectOutputStream jan;
    public HeadquartersStory() throws IOException, ClassNotFoundException, InterruptedException {

        ServerSocket jan = new ServerSocket(6000);
        Socket connection = jan.accept();

        ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(connection.getInputStream());

        System.out.println("Getting packet from JAN");
        Packet p = (Packet)in.readObject();

        Packet outPacket = new Packet(2,100,p.sequenceno + 1,p.sequenceno + 1);
            outPacket.setData("Target Eliminated");

            System.out.println("Returning packet to JAN");
        out.writeObject(outPacket);

        while(true){}
    }
}
