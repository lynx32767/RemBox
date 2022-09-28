package rembox.packets.file;

import rembox.packets.ExecutablePacket;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;

public class FileReadPacket implements ExecutablePacket, Serializable {

    //Server generated
    private final String path;
    private final long offset;
    private final int length;

    //Client generated
    private byte[] data = null;

    public FileReadPacket(String path, long offset, int length) {
        this.path = path;
        this.offset = offset;
        this.length = length;
    }

    @Override
    public void execute() {
        try {
            File file = new File(path);
            if(file.length() >= offset + length) {
                FileInputStream instream = new FileInputStream(file);
                data = new byte[length];
                instream.skip(offset);
                instream.read(data);
                instream.close();
            }
        } catch(IOException e) {
            data = null;
        }
    }

    public byte[] getData() {
        return data;
    }

}
