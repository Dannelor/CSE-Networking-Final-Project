package TCPSimulation;

import TCPSimulation.Functional.RouterInfo;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Dannelor on 5/7/2018.
 */
public class Agent extends Router {

    public Agent(RouterInfo router, HashMap<String, RouterInfo> world) throws IOException {
        super(router, world);
    }

    // Agents behave differently when receiving new packets
    // Validates all information about the packet
    @Override
    protected void receive(Packet p){
        p.verifyChecksum();
        // Agent has been forcibly removed from connection
        if(p.RST && p.TER) {
            System.out.println("TERMINATED");
            connections.forEach((l,r) -> {
                try {
                    r.close();
                    routerInputs.forEach(t -> t.interrupt());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            System.exit(0);
        }

        receiveStoryPacket(p);
    }

    protected void receiveStoryPacket(Packet p){
        System.out.println(p.getData());
    }
}
