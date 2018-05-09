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

    boolean janFinishedSending = false;
    boolean finishedSendingToJan = false;

    @Override
    void nextStoryPacket(Packet received){
        System.out.println("Received data from " + received.source);

        if(Mission3Started)
            HandleMission3Message(received);

        // put returns the previous value so increment by one
        int curSeqNO = seqNO.put(received.source,seqNO.get(received.source) + 1);

        if(finishedSendingToJan && janFinishedSending){
            this.Mission3Started = true;
        }

        if(curSeqNO >= data.get(received.source).size())
            return;

        Packet out = new Packet(router.getNumberID(), received.source, curSeqNO, 0);
            out.setData(data.get(received.source).get(curSeqNO));
            if(communicateWithJanURG && received.destination == 100) {
                out.URG = true;
                communicateWithJanURG = false;
            }
            if(curSeqNO == data.get(received.source).size() - 1) {
                finishedSendingToJan = true;
                out.FIN = true;
            }

            System.out.println("TEST");
        sendStoryPacket(out);
    }


    @Override
    protected void HandleMission3Message(Packet incoming) {
        int sequence = incoming.sequenceno;

        Packet out = null;
        switch(sequence){
            case 0:
                out = new Packet(router.getNumberID(),incoming.source,sequence,sequence);
                    out.ACK = true;
                    out.setData("Execute.PEPPER THE PEPPER");
            case 2:
                out = new Packet(router.getNumberID(),incoming.source,sequence + 1,sequence + 1);
                    out.ACK = true;
                    out.setData("(32.76”N, -97.07” W )");
            case 3:
                out = new Packet(router.getNumberID(),incoming.source,sequence + 1,sequence + 1);
                    out.ACK = true;
                    out.FIN = true;
        }

        if(out == null)
            return;

        sendStoryPacket(out);

        if(out.FIN && out.ACK)
            System.exit(0);
    }

    @Override
    protected void receiveAgentStoryPacket(Packet incoming){

        if(this.Mission3Started) {
            HandleMission3Message(incoming);
            return;
        }

        // Stop communication with chan
        // We are actively shutting them down, we are talking to them, and they have finished talking
        if(incoming.source == 1 && incoming.FIN) {
            System.out.println("Terminating CHAN");
            Packet out = new Packet(router.getNumberID(), 1, 0, 0);
            out.RST = true;
            out.TER = true;

            sendPacket(out);

            return;
        }

        if(incoming.FIN && incoming.source == 100){
            janFinishedSending = true;
            if(finishedSendingToJan){
                this.Mission3Started = true;
            }
        }

        System.out.println("Received packet from " + incoming.source);

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
