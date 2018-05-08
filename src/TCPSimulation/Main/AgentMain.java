package TCPSimulation.Main;

import TCPSimulation.AgentStories.AgentAnnStory;
import TCPSimulation.AgentStories.AgentChanStory;
import TCPSimulation.AgentStories.AgentJanStory;
import TCPSimulation.Functional.RouterInfo;
import TCPSimulation.Utility.WorldReader;

import java.io.*;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class AgentMain {

    public static void main(String [] args) throws IOException {

        if(args.length == 0){
            System.out.println("Please specify the agent");
            return;
        }

        HashMap<String, RouterInfo> world = WorldReader.getWorldInfo();

        List<RouterInfo> agents;
        switch(args[0]){
            case "Ann":
                agents = world.values().stream().filter(info -> info.getStringID() .equals("Jan") || info.getStringID() .equals("Chan")).collect(Collectors.toList());
                new AgentAnnStory(world.get("Ann"),world,agents,getData("Ann",agents));
                break;
            case "Jan":
                agents = world.values().stream().filter(info -> info.getStringID() .equals("Ann") || info.getStringID() .equals("Chan")).collect(Collectors.toList());
                new AgentJanStory(world.get("Jan"),world,agents,getData("Jan",agents));
                break;
            case "Chan":
                agents = world.values().stream().filter(info -> info.getStringID() .equals("Jan") || info.getStringID() .equals("Ann")).collect(Collectors.toList());
                new AgentChanStory(world.get("Chan"),world,agents,getData("Chan",agents));
                break;
            default:
                System.out.println("No agent was found with the ID: " + args[0]);
        }
    }

    public static HashMap<Integer,List<String>> getData(String StringID,List<RouterInfo> agents) throws FileNotFoundException {
        HashMap<Integer,List<String>> data = new HashMap<>();
        File file = new File("SupplementalTextFiles/" + StringID + "/");
        for(File f : file.listFiles()){
            final String name = f.getName().split("-_")[1];
            RouterInfo info = agents.stream().filter(a -> a.getStringID().equals(name.substring(0, name.indexOf('.')))).findFirst().get();

            BufferedReader reader = new BufferedReader(new FileReader(f));
            data.put(info.getNumberID(),reader.lines().collect(Collectors.toList()));
        }
        return data;
    }
}
