package rembox.packets;

import rembox.server.Server;

import java.io.Serializable;

public class Packet implements Serializable {

    private final ExecutablePacket packet;
    private final long id;

    public Packet(ExecutablePacket packet) {
        this.packet = packet;
        this.id = Server.generateID();
    }

    public ExecutablePacket getPacket() {
        return packet;
    }

    public long getID() {
        return id;
    }

}
