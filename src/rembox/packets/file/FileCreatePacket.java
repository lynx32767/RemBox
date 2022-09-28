package rembox.packets.file;

import rembox.packets.ExecutablePacket;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class FileCreatePacket implements ExecutablePacket, Serializable {

    //Server generated
    private final String path;
    private final boolean folder;

    //Client generated
    private boolean success;

    public FileCreatePacket(String path, boolean folder) {
        this.path = path;
        this.folder = folder;
    }

    @Override
    public void execute() {
        File file = new File(path);
        try {
            success = folder ? file.mkdirs() : file.createNewFile();
        } catch(IOException e) {
            success = false;
        }

    }

    public boolean success() {
        return success;
    }

}
