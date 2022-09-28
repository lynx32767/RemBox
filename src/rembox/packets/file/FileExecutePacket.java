package rembox.packets.file;

import rembox.packets.ExecutablePacket;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class FileExecutePacket implements ExecutablePacket, Serializable {

    private final String path;

    private boolean success;

    public FileExecutePacket(String path) {
        this.path = path;
    }

    @Override
    public void execute() {
        try {
            Desktop.getDesktop().open(new File(path));
            success = true;
        } catch (Exception e) {
            success = false;
        }
    }

    public boolean success() {
        return success;
    }

}
