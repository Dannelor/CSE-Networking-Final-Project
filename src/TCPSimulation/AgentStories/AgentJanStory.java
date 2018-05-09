/* 
Mason Beckham 1001073976
Minh-Quan Nguyen 1001032212
*/

package TCPSimulation.AgentStories;

import TCPSimulation.Functional.RouterInfo;
import TCPSimulation.Packet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class AgentJanStory extends AgentStory{

    private HashMap<Integer,List<String>> data;
    private HashMap<Integer,BufferedWriter> writers = new HashMap<>();
    public AgentJanStory(RouterInfo router, HashMap<String, RouterInfo> world, List<RouterInfo> agents, HashMap<Integer, List<String>> data) throws IOException {
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

    boolean finishedSendingToAnn = false;
    boolean annFinishedSending = false;

    @Override
    void nextStoryPacket(Packet received){
        System.out.println("Received data from " + received.source);

        if(finishedSendingToAnn && annFinishedSending){
            this.Mission3Started = true;
            StartMission3();
        }

        // put returns the previous value so increment by one
        int curSeqNO = seqNO.put(received.source,seqNO.get(received.source) + 1);

        if(curSeqNO >= data.get(received.source).size())
            return;


        Packet out = new Packet(router.getNumberID(),received.source,curSeqNO,0);
        out.setData(data.get(received.source).get(curSeqNO));

        if(curSeqNO == data.get(received.source).size() - 1) {
            finishedSendingToAnn = true;
            out.FIN = true;
        }

        sendStoryPacket(out);
    }

    private void StartMission3() {
        Packet p = new Packet(router.getNumberID(),111,0,0);
            p.URG = true;
            p.setData("(32° 43’ 22.77” N,97° 9’ 7.53” W )");

            sendStoryPacket(p);
    }

    @Override
    protected void HandleMission3Message(Packet p){
        int sequence = p.sequenceno;

        Packet out = null;
        switch(sequence){
            case 0:
                out = new Packet(router.getNumberID(),2,p.sequenceno,p.sequenceno);
                    out.setData("(32° 43’ 22.77” N,97° 9’ 7.53” W ). Please eliminate target. PEPPER THE PEPPER");
                    out.URG = true;

            case 1:
                out = new Packet(router.getNumberID(),111,p.sequenceno + 1,p.sequenceno + 1);
                    out.setData("CONGRATULATIONS WE FRIED DRY GREEN LEAVE");
                    out.URG = true;
                    out.ACK = true;
            case 3:
                out = new Packet(router.getNumberID(),111,p.sequenceno + 1,p.sequenceno + 1);
                    out.FIN = true;
            case 4:
                System.exit(0);
        }

        if(out == null)
            return;

        sendStoryPacket(out);
    }
    
    @Override
    protected void receiveAgentStoryPacket(Packet incoming){

        if(this.Mission3Started && incoming.source == 111) {
            System.out.println("Mission 3");
            HandleMission3Message(incoming);
            return;
        }

        if(incoming.FIN && incoming.source == 111){
            if(finishedSendingToAnn) {
                this.Mission3Started = true;
                StartMission3();
            }
            annFinishedSending = true;
        }

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
