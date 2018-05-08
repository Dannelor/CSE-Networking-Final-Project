package TCPSimulation;

import java.io.Serializable;

public class Packet implements Serializable {
    // 16-bit ints
    public int source = -1;
    public int destination = -1;
    int receiverwindow = -1;
    int checksum = -1;
    private int urgdatapointer = -1;

    // 32-bit ints
    public int sequenceno;
    public int acknowledgementno;


    // Header length 160-bits
    int headerlength = 160;

    // Header flags
    public boolean DRP = false;
    public boolean TER = false;
    public boolean ACK = false;
    public boolean RST = false;
    public boolean SYN = false;
    public boolean FIN = false;
    private boolean URG;

    // Data
    private byte [] data;

    public Packet(int source, int destination, int sequenceno, int acknowledgementno) {
        this.source = source;
        this.destination = destination;
        this.sequenceno = sequenceno;
        this.acknowledgementno = acknowledgementno;
        this.receiverwindow = 0;
    }

    // -------------------------------- Set Getters and Setters -------------------------------- //
    // --- Getters
    public int getChecksum() {
        return this.checksum;
    }

    void setChecksum(int checksum) {
        this.checksum = checksum;
    }

    void setData(byte [] data) {
        this.data = data;
    }

    public byte[] getData(){
        return this.data;
    }

    public void setData(String data) {
        setData(data.getBytes());
    }

    void setUrgent(int urgdatapointer){
        URG = true;
        this.urgdatapointer = urgdatapointer;
    }
}
