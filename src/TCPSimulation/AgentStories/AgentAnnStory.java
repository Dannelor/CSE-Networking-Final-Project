package TCPSimulation.AgentStories;

import TCPSimulation.Functional.RouterInfo;
import TCPSimulation.Packet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class AgentAnnStory extends AgentStory {

    HashMap<Integer,List<String>> data;
    HashMap<Integer,BufferedWriter> writers = new HashMap<>();
    public AgentAnnStory(RouterInfo router, HashMap<String, RouterInfo> world, List<RouterInfo> agents, HashMap<Integer, List<String>> data) throws IOException {
        super(router, world, agents);
        // Create a file writer for each agent
        for (RouterInfo a : agents) {
            File file = new File(router.getStringID() + "received" + a.getStringID() + ".txt");
            if(!file.exists())
                file.createNewFile();
            writers.put(a.getNumberID(), new BufferedWriter(new FileWriter(file)));
            writers.get(a.getNumberID()).append("Information received from agent " + a.getStringID() + "\n");
        }
        this.data = data;
    }

    boolean communicateWithJanURG = false;
    boolean shutDownChan = true;

    @Override
    void nextStoryPacket(Packet received){
        System.out.println("Received data from " + received.source);

        // put returns the previous value so increment by one
        int curSeqNO = seqNO.put(received.source,seqNO.get(received.source) + 1);

        if(curSeqNO >= data.get(received.source).size()) {
            // Shutdown Agent chan
            if(shutDownChan && received.source == 1) {
                System.out.println("Terminating CHAN");
                Packet out = new Packet(router.getNumberID(), 1, curSeqNO, 0);
                    out.RST = true;
                    out.TER = true;

                sendPacket(out);

                shutDownChan = false;
                return;
            }
            Mission3Receive(received);
            return;
        }

        if(curSeqNO == 5 && received.source == 1){
            communicateWithJanURG = true;
        }

        Packet out = new Packet(router.getNumberID(), received.source, curSeqNO, 0);
            out.setData(data.get(received.source).get(curSeqNO));
            if(communicateWithJanURG && received.destination == 100)
                out.URG = true;

        sendStoryPacket(out);
    }

    int missionstep = 0;
    private void Mission3Receive(Packet received) {
        int curSeqNO = seqNO.put(received.source,seqNO.get(received.source) + 1);

        Packet out = null;
        switch(missionstep) {
            case 0:
                out = new Packet(router.getNumberID(), 100, curSeqNO, 0);
                    out.setData("Execute.PEPPER THE PEPPER");
                    out.URG = true;
                missionstep++;
                break;
            case 1:
                out = new Packet(router.getNumberID(), 100, curSeqNO, 0);
                out.setData("(32.76”N, -97.07” W )");
                out.URG = true;
                missionstep++;
                break;
            case 2:
                out = new Packet(router.getNumberID(), 100, curSeqNO, 0);
                    out.FIN = true;
                    out.ACK = true;
        }

        if(out == null)
            return;

        sendStoryPacket(out);
        if(missionstep == 2) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.exit(0);
        }
    }

    @Override
    protected void receiveAgentStoryPacket(Packet incoming){
        if(incoming.getData() != null) {
            try {
                BufferedWriter writer = writers.get(incoming.source);
                writer.append(new String(incoming.getData()) + "\n");
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
