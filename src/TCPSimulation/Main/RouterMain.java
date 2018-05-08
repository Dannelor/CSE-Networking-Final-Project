package TCPSimulation.Main;

import TCPSimulation.Functional.RouterInfo;
import TCPSimulation.Utility.WorldReader;

import java.io.IOException;
import java.util.HashMap;

public class RouterMain {

    public static void main(String [] args) throws IOException {

        if(args.length < 1){
            System.out.println("No router ID was specified");
            return;
        }

        String routerID = args[0];

        HashMap<String,RouterInfo> world = WorldReader.getWorldInfo();

        // Create a new router object and give it itself and the world
        new TCPSimulation.Router(world.get(routerID),world);
    }

}
