package TCPSimulation;

import TCPSimulation.Functional.RouterInfo;
import javafx.util.Pair;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Router {

    // TCPSimulation.Router we are connected to and the TCPSimulation.RouterConnection between them
    public ConcurrentHashMap<RouterInfo,RouterConnection> connections = new ConcurrentHashMap<>();
    // Info about the current router
    protected RouterInfo router;
    // All routers within the world
    // StringID, RouterInfo
    protected HashMap<String,RouterInfo> world;
    // List of all current router input threads
    protected List<Thread> routerInputs = new ArrayList<>();

    // Cache of all the destinations and RouterInfo's
    // TCPSimulation.Agent Number ID, TCPSimulation.Router to forward
    private Map<Integer,RouterInfo> destinationCache = new HashMap<>();

    public Router(RouterInfo router,HashMap<String,RouterInfo> world) throws IOException {
        this.router = router;
        this.world = world;

        // Accept connections from other routers
        ServerSocket serverSocket = new ServerSocket(router.getPort());
        new Thread(() ->{
            Socket s = null;
            while(!Thread.currentThread().isInterrupted()) {
                try {
                    // Accept any incoming connections from outside routers
                    s = serverSocket.accept();

                    // Create a new thread for router inputs
                    routerInputs.add(RouterConnection.newInputThread(this, s));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(s != null) {
                try {
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        ExecutorService executorService = Executors.newCachedThreadPool();
        for(RouterInfo adjacent : router.adjacent.keySet()) {
            executorService.submit(new ConnectionRunnable(this,adjacent));
        }
        executorService.shutdown();
    }

    void receive(Packet p){
        System.out.println("Received packet");

        if(p == null)
            return;

        if(p.DRP)
            return;

        // Send the packet to the next router
        sendPacket(p);
    }

    protected void sendPacket(Packet p){

        // Source and Destination are the same
        if(p.source == p.destination)
            return;

        // No destination set for packet
        if(p.destination == -1)
            return;

        RouterInfo route = findRoute(p.destination);

        // No route could be found to the given destination
        if(route == null) {
            System.out.println("Could not find route to destination: " + p.destination);
            return;
        }

        // Connection has not yet been established with this router
        if(!connections.containsKey(route)) {
            System.out.println("Connection doesn't exist for router " + route.getStringID() + " sending failed");
            return;
        }

        connections.get(route).send(p);
    }

    private RouterInfo findRoute(int destination){

        // We've already calculated this path once
        if(destinationCache.containsKey(destination))
            return destinationCache.get(destination);

        List<RouterInfo> visited = new ArrayList<>();
        // Compares the total distance to each router
        // Current fringe router, (Starting adjacent TCPSimulation.Router, Total Distance)
        PriorityQueue<Pair<RouterInfo,Pair<RouterInfo,Integer>>> fringe = new PriorityQueue<>(12, Comparator.comparingInt(l -> l.getValue().getValue()));

        for(Map.Entry<RouterInfo,Integer> adjacent : router.adjacent.entrySet()){
            // The adjacent node is the destination, it is already shortest path
            if(adjacent.getKey().getNumberID() == destination)
                return adjacent.getKey();

            // -1 indicates this node can only be sent to as a destination
            // So no need to use it in the route finding
            if(adjacent.getValue() == -1)
                continue;

            fringe.add(new Pair<>(adjacent.getKey(),new Pair<>(adjacent.getKey(),adjacent.getValue())));
            visited.add(adjacent.getKey());
        }

        while(fringe.size() > 0){
            // Get the closest current node to the router
            Pair<RouterInfo,Pair<RouterInfo,Integer>> closest = fringe.poll();
            // We've found the fastest path to the destination
            // Add it to the cache and return the RouterInfo
            assert closest != null;
            if(closest.getKey().getNumberID() == destination){
                destinationCache.put(destination,closest.getValue().getKey());
                return closest.getValue().getKey();
            }

            for(Map.Entry<RouterInfo,Integer> adjacent : closest.getKey().adjacent.entrySet()){


                // If we find the destination while adding new Routers to fringe
                // Since destination cost is -1 we have found fastest route
                if(adjacent.getKey().getNumberID() == destination){
                    destinationCache.put(destination,closest.getValue().getKey());
                    return closest.getValue().getKey();
                }

                // We've already been to this adjacent node
                if(visited.contains(adjacent.getKey()))
                    continue;

                // Add a new TCPSimulation.Router to the fringe
                // The RouterInfo of the adjacent TCPSimulation.Router, (The original adjacent RouterInfo,The new total distance to the TCPSimulation.Router)
                fringe.add(new Pair<>(adjacent.getKey(),new Pair<>(closest.getValue().getKey(),closest.getValue().getValue() + adjacent.getValue())));
            }
            visited.add(closest.getKey());
        }

        // No path could be found to this destination
        return null;
    }
}
