package rembox.packets.file;

import rembox.packets.ExecutablePacket;

import java.io.*;

public class FileWritePacket implements ExecutablePacket, Serializable {

    //Server generated
    private final String path;
    private final byte[] data;
    private final boolean append;

    //Client generated
    private boolean success;

    public FileWritePacket(String path, byte[] data, boolean append) {
        this.path = path;
        this.data = data;
        this.append = append;
    }

    @Override
    public void execute() {
        try {
            File file = new File(path);
            FileOutputStream ostream = new FileOutputStream(file, append);
            ostream.write(data);
            ostream.flush();
            ostream.close();
            success = true;
        } catch(IOException e) {
            success = false;
        }
    }

    public boolean isSuccess() {
        return success;
    }

}
