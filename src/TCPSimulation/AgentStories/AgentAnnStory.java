package TCPSimulation.AgentStories;

import TCPSimulation.Functional.RouterInfo;
import TCPSimulation.Packet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class AgentAnnStory extends AgentStory {

    HashMap<Integer,List<String>> data;
    public AgentAnnStory(RouterInfo router, HashMap<String, RouterInfo> world, List<RouterInfo> agents, HashMap<Integer, List<String>> data) throws IOException {
        super(router, world, agents);
        this.data = data;
    }

    @Override
    void nextStoryPacket(Packet received){
        System.out.println("Received data from " + received.source);
        if(received.getData() != null)
            System.out.println(new String(received.getData()));
        // put returns the previous value so increment by one
        int curSeqNO = seqNO.put(received.source,seqNO.get(received.source) + 1);

        if(curSeqNO >= data.get(received.source).size())
            return;

        Packet out = new Packet(router.getNumberID(),received.source,curSeqNO,0);
        out.setData(data.get(received.source).get(curSeqNO));

        sendStoryPacket(out);
    }
}
