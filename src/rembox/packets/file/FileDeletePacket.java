package rembox.packets.file;

import rembox.packets.ExecutablePacket;

import java.io.File;
import java.io.Serializable;

public class FileDeletePacket implements ExecutablePacket, Serializable {

    private final String path;

    private boolean success;

    public FileDeletePacket(String path) {
        this.path = path;
    }

    @Override
    public void execute() {
        success = new File(path).delete();
    }

    public boolean success() {
        return success;
    }
}

