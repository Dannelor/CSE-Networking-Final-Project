/* 
Mason Beckham 1001073976
Minh-Quan Nguyen 1001032212
*/

package TCPSimulation.AgentStories;

import TCPSimulation.Functional.RouterInfo;
import TCPSimulation.Packet;
import TCPSimulation.Router;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

public class HeadquartersStory extends Router {

    ObjectOutputStream jan;
    public HeadquartersStory(RouterInfo router, HashMap<String, RouterInfo> world) throws IOException {
        super(router, world);

        Socket s = new Socket("localhost",5011);
        jan = new ObjectOutputStream(s.getOutputStream());
    }

    @Override
    protected void receive(Packet p) {

        if(p.source != 100 && !p.getData().equals("(32° 43’ 22.77” N,97° 9’ 7.53” W ). Please eliminate target. PEPPER THE PEPPER"))
            return;

        Packet out = new Packet(router.getNumberID(),100,p.sequenceno + 1,p.sequenceno + 1);
            out.setData("Elimination successful");
            out.ACK = true;

        RouterInfo route = world.get("Jan");

        // No route could be found to the given destination
        if(route == null) {
            System.out.println("Could not find route to destination: " + p.destination);
            return;
        }

        try {
            jan.writeObject(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
