package rembox.packets.file;

import rembox.packets.ExecutablePacket;

import java.io.File;
import java.io.Serializable;

public class FileInfoPacket implements ExecutablePacket, Serializable {

    //Given by server
    private final String path;

    //Given by client
    private String name;
    private boolean exists;
    private boolean canRead;
    private boolean canWrite;
    private boolean isFolder;
    private String[] containedFiles;
    private long size;

    public FileInfoPacket(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    @Override
    public void execute() {

        if(!path.equals("Root")) {
            File file = new File(path);
            name = file.getName();
            exists = file.exists();
            canRead = file.canRead();
            canWrite = file.canWrite();
            isFolder = file.isDirectory();
            containedFiles = file.list();
            size = file.length();
        } else {
            name = "Root";
            exists = true;
            canRead = true;
            canWrite = true;
            isFolder = true;
            File[] roots = File.listRoots();
            String[] rootNames = new String[roots.length];
            for(int i = 0; i < roots.length; i++) {
                rootNames[i] = roots[i].getAbsolutePath();
            }
            containedFiles = rootNames;
            size = 0;
        }
    }

    public boolean exists() {
        return exists;
    }

    public boolean canRead() {
        return canRead;
    }

    public boolean canWrite() {
        return canWrite;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public String[] getContainedFiles() {
        return containedFiles;
    }

    public long getSize() {
        return size;
    }
}
