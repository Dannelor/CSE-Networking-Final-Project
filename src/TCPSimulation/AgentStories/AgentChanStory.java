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

public class AgentChanStory extends AgentStory{

    HashMap<Integer,List<String>> data;
    HashMap<Integer,BufferedWriter> writers = new HashMap<>();
    public AgentChanStory(RouterInfo router, HashMap<String, RouterInfo> world, List<RouterInfo> agents, HashMap<Integer, List<String>> data) throws IOException {
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

    @Override
    void nextStoryPacket(Packet received){
        System.out.println("Received data from " + received.source);

        // put returns the previous value so increment by one
        int curSeqNO = seqNO.put(received.source,seqNO.get(received.source) + 1);

        if(curSeqNO >= data.get(received.source).size())
            return;

        Packet out = new Packet(router.getNumberID(),received.source,curSeqNO,0);
        out.setData(data.get(received.source).get(curSeqNO));
        if(curSeqNO == data.get(received.source).size() - 1)
            out.FIN = true;
        sendStoryPacket(out);
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
