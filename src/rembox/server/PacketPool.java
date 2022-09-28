package rembox.server;

import rembox.packets.ExecutablePacket;
import rembox.packets.Packet;

import java.util.ArrayList;

public class PacketPool {

    private static volatile ArrayList<Packet> pool = new ArrayList<>();
    private static volatile ArrayList<Long> blockedIDs = new ArrayList<>();

    //Add a packet to the pool
    public static void addPacket(Packet packet) {
        //Cancel adding the packet if its ID was blocked
        if(blockedIDs.contains(packet.getID())) {
            blockedIDs.remove(packet.getID());
            return;
        }

        //Add packet
        if(!pool.contains(packet)) {
            pool.add(packet);
        }
    }

    //Send a packet and return it once executed on the remote machine
    public static ExecutablePacket awaitResponse(ClientConnection client, ExecutablePacket packet) {
        Packet wrapper = new Packet(packet);
        Server.send(client, wrapper);
        long startTime = System.currentTimeMillis();

        //For maximum 30 seconds, wait for the desired packet to arrive in the pool
        while(System.currentTimeMillis() - startTime < 5000) {
            for(int i = 0; i < pool.size(); i++) {
                Packet p = pool.get(i);
                if(p != null && p.getID() == wrapper.getID()) {
                    pool.remove(p);
                    return p.getPacket();
                }
            }
        }

        //If the 30 sec timeout happens we throw this exception and prevent the packet from arriving afterwards
        //to avoid packets or threads cluttering up the program
        blockedIDs.add(wrapper.getID());
        throw new NullPointerException("Packet doesn't exist");
    }

    //Called on server stop
    public static void reset() {
        pool.clear();
        blockedIDs.clear();
    }

}
