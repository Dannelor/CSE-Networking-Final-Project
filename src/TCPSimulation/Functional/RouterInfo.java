package TCPSimulation.Functional;

import java.util.HashMap;
import java.util.Map;

public class RouterInfo {

    Integer numberID;
    String stringID;
    Integer port;

    // List of all routers that are directly adjacent to this router
    // RouterInfo and Distance
    public Map<RouterInfo,Integer> adjacent = new HashMap<>();

    public RouterInfo(Integer numberID, String stringID,int port){
        this.numberID = numberID;
        this.stringID = stringID;
        this.port = port;
    }

    public Integer getNumberID(){
        return numberID;
    }

    public String getStringID(){
        return stringID;
    }
    
    public Integer getPort(){
        return port;
    }

    public void addAdjacent(RouterInfo router,int distance){
        adjacent.put(router,distance);
    }

    public boolean equals(Object o){
        if(o == this)
            return true;

        if(!(o instanceof RouterInfo))
            return false;

        RouterInfo r = (RouterInfo) o;
        return r.numberID == numberID && r.stringID.equals(stringID) && r.port == port && r.adjacent.keySet().equals(adjacent.keySet()) && r.adjacent.values().equals(adjacent.values());
    }

    public int hashCode() {
        int result = 17;
        result = 31 * result + numberID;
        return result;
    }

}
