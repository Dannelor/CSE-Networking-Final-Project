/* 
Mason Beckham 1001073976
Minh-Quan Nguyen 1001032212
*/

package TCPSimulation;

import java.io.Serializable;

public class Packet implements Serializable {
    // 16-bit ints
    public int source = -1;
    public int destination = -1;
    int receiverwindow = -1;
    long checksum = -1;
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
    public boolean URG = false;

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
    public long getChecksum() {
        return this.checksum;
    }

    void setChecksum(int checksum) {
        this.checksum = checksum;
    }

    void setData(byte [] data) {
        this.checksum = calculateChecksum(data);
        this.data = data;
    }

    public byte[] getData(){
        return this.data;
    }

    public void setData(String data) {
        setData(data.getBytes());
    }

    public long calculateChecksum(byte [] data){
        int i = 0;
        long sum = 0;
        int length = data.length;
        while (length > 0) {
            sum += (data[i++]&0xff) << 8;
            if ((--length)==0) break;
            sum += (data[i++]&0xff);
            --length;
        }

        return (~((sum & 0xFFFF)+(sum >> 16)))&0xFFFF;
    }

    public boolean verifyChecksum(){

        return checksum == -1 ? true : checksum == calculateChecksum(data);
    }
}
