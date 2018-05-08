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
    void receive(Packet p){
        // Do validation
        receiveStoryPacket(p);
    }

    protected void receiveStoryPacket(Packet p){
        System.out.println(p.getData());
    }
}
