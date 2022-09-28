package rembox.packets.system;

import rembox.packets.ExecutablePacket;

import java.io.Serializable;

public class CloseClientPacket implements ExecutablePacket, Serializable {

    @Override
    public void execute() {
        System.exit(0);
    }

}
