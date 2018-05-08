package TCPSimulation.AgentStories;

import TCPSimulation.Agent;
import TCPSimulation.Functional.RouterInfo;
import TCPSimulation.Packet;

import java.io.IOException;
import java.util.*;

public class AgentStory extends Agent {

    // Mapping of all currently running handshakes
    // NumberID, Task
    HashMap<Integer,TimerTask> activeHandshakes = new HashMap<>();

    // Store the current SEQ# for each connection
    HashMap<Integer,Integer> seqNO = new HashMap<>();

    public AgentStory(RouterInfo router, HashMap<String, RouterInfo> world,List<RouterInfo> agents) throws IOException {
        super(router, world);
        handshake(agents);
    }

    Timer timer = new Timer();
    // All current outgoing packets
    // Destination, Task
    HashMap<Integer,TimerTask> tasks = new HashMap<>();

    @Override
    protected void receiveStoryPacket(Packet p){
        //System.out.println("Received a packet from: " + p.source);
        // Someone is attempting a handshake
        if(p.SYN){
            // Someone is replying to our handshake
            if(p.ACK){
                TimerTask task = activeHandshakes.get(p.source);
                task.cancel();
                timer.purge();

                // Finish handshake and start story
                Packet returnHandshake = new Packet(router.getNumberID(), p.source, 0, 0);
                    returnHandshake.ACK = true;

                    seqNO.put(p.source,p.sequenceno);

                    sendPacket(returnHandshake);
                return;
            }else{ // Reply to another handshake request
                System.out.println("Received handshake request from " + p.source);
                Packet returnHandshake = new Packet(router.getNumberID(), p.source, 0, 0);
                    returnHandshake.SYN = true;
                    returnHandshake.ACK = true;

                sendPacket(returnHandshake);
                return;
            }
        }

        if(p.ACK){
            seqNO.putIfAbsent(p.source,p.acknowledgementno);

            System.out.println("Received ACK " + seqNO.get(p.source).intValue() + " : " + p.acknowledgementno);

            if(tasks.containsKey(p.source)) {
                tasks.get(p.source).cancel();
                tasks.put(p.source, null);
            }

            timer.purge();
            nextStoryPacket(p);
            return;
        }

        handleIncomingStoryPacket(p);
    }

    protected void handleIncomingStoryPacket(Packet incoming) {
        // Send response ack to the sending router
        Packet p = new Packet(incoming.destination,incoming.source,0,incoming.sequenceno + 1);
            p.ACK = true;

        sendPacket(p);

        receiveAgentStoryPacket(incoming);
    }

    protected void receiveAgentStoryPacket(Packet incoming){}

    void nextStoryPacket(Packet received){
        System.out.println(received.getData());
    }

    void sendStoryPacket(Packet send){

        // We are already trying to send a packet
        if(tasks.get(send.destination) != null)
            return;

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                sendPacket(send);
            }
        };

        tasks.put(send.destination,task);
        timer.scheduleAtFixedRate(task,0,1000);
    }

    void handshake(List<RouterInfo> agents){
        for(RouterInfo agent : agents) {
            // Send a new handshake packet to the destination
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    Packet p = new Packet(router.getNumberID(), agent.getNumberID(), 0, 0);
                        p.SYN = true;

                    sendPacket(p);
                }
            };
            timer.scheduleAtFixedRate(task,0,1000);
            activeHandshakes.put(agent.getNumberID(),task);
        }
    }
}
