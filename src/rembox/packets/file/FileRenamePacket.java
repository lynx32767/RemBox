package rembox.packets.file;

import rembox.packets.ExecutablePacket;

import java.io.File;
import java.io.Serializable;

public class FileRenamePacket implements ExecutablePacket, Serializable {

    private final String path;
    private final String newName;

    private boolean success;

    public FileRenamePacket(String path, String newName) {
        this.path = path;
        this.newName = newName;
    }

    @Override
    public void execute() {
        try {
            File file = new File(path);
            success = file.renameTo(new File(newName));
        } catch(Exception e) {
            success = false;
        }
    }

    public boolean success() {
        return success;
    }

}
