package rembox.packets.client;

import rembox.packets.ExecutablePacket;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;

public class RenameClientPacket implements ExecutablePacket, Serializable {

    //Server generated
    private final String newName;

    //Client generated
    private boolean success = false;

    public RenameClientPacket(String newName) {
        this.newName = newName;
    }

    @Override
    public void execute() {
        try {
            if(new File("data").mkdir()) {
                File nameFile = new File("data\\name.txt");
                new PrintWriter(new FileOutputStream(nameFile, false)).print(newName);
                success = true;
            }
        } catch(Exception ignored) {}
    }

    public boolean success() {
        return success;
    }

}
