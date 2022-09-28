package rembox.packets.client;

import rembox.packets.ExecutablePacket;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Serializable;

public class GetNamePacket implements ExecutablePacket, Serializable {

    private String name = "[Client name unknown]";

    @Override
    public void execute() {
        try {
            name = new BufferedReader(new FileReader("data\\name.txt")).readLine();
        } catch(Exception ignored) {}
    }

    public String getName() {
        return name;
    }

}
