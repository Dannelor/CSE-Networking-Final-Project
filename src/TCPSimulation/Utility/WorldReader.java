package TCPSimulation.Utility;

import TCPSimulation.Functional.RouterInfo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;

public class WorldReader {

    public static HashMap<String, RouterInfo> getWorldInfo() throws FileNotFoundException {

        HashMap<String,RouterInfo> world = new HashMap<>();

        BufferedReader IDPortMapReader = new BufferedReader(new FileReader("IDPortMap.data"));
        IDPortMapReader.lines().forEach(line ->{
            String [] values = line.split(" ");
            world.put(values[0],new RouterInfo(Integer.parseInt(values[1]),values[0],Integer.parseInt(values[2])));
        });

        BufferedReader AdjacencyReader = new BufferedReader(new FileReader("Adjacency.data"));
        AdjacencyReader.lines().forEach(line -> {
            String [] values = line.split(" ");
            // Get the RouterInfo from the first element in the line
            RouterInfo router = world.get(values[0]);

            // For each pair of ID and Distance loop through the line
            for(int i = 1;i < values.length;i = i + 2){
                // Get the corresponding RouterInfo to the ID and add it to adjacency with distance
                router.addAdjacent(world.get(values[i]),Integer.parseInt(values[i+1]));
            }
        });

        return world;
    }
}
