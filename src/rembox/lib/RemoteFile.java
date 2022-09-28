package rembox.lib;

import rembox.packets.file.FileCreatePacket;
import rembox.packets.file.FileInfoPacket;
import rembox.packets.file.FileReadPacket;
import rembox.packets.file.FileWritePacket;
import rembox.server.ClientConnection;
import rembox.server.PacketPool;

public class RemoteFile {

    private ClientConnection client;
    private String path;

    public RemoteFile(ClientConnection client, String path) {
        this.client = client;
        this.path = path;
    }

    public String getAbsolutePath() {
        return path;
    }

    //Standard java.io.File methods
    public String getName() {
        return StringHelper.pathToName(path);
    }

    public boolean exists() {
        FileInfoPacket packet = (FileInfoPacket)(PacketPool.awaitResponse(client, new FileInfoPacket(path)));
        return packet.exists();
    }

    public boolean canRead() {
        FileInfoPacket packet = (FileInfoPacket)(PacketPool.awaitResponse(client, new FileInfoPacket(path)));
        return packet.canRead();
    }

    public boolean canWrite() {
        FileInfoPacket packet = (FileInfoPacket)(PacketPool.awaitResponse(client, new FileInfoPacket(path)));
        return packet.canWrite();
    }

    public boolean isFolder() {
        FileInfoPacket packet = (FileInfoPacket)(PacketPool.awaitResponse(client, new FileInfoPacket(path)));
        return packet.isFolder();
    }

    public String[] getContainedFiles() {
        FileInfoPacket packet = (FileInfoPacket)(PacketPool.awaitResponse(client, new FileInfoPacket(path)));
        return packet.getContainedFiles();
    }

    public long length() {
        FileInfoPacket packet = (FileInfoPacket)(PacketPool.awaitResponse(client, new FileInfoPacket(path)));
        return packet.getSize();
    }

    //Extended methods for easy access
    public byte[] read(long offset, int length) {
        FileReadPacket packet = (FileReadPacket)(PacketPool.awaitResponse(client, new FileReadPacket(path, offset, length)));
        return packet.getData();
    }

    public boolean write(byte[] data, boolean append) {
        FileWritePacket packet = (FileWritePacket)(PacketPool.awaitResponse(client, new FileWritePacket(path, data, append)));
        return packet.isSuccess();
    }

    public boolean create(boolean folder) {
        FileCreatePacket packet = (FileCreatePacket)(PacketPool.awaitResponse(client, new FileCreatePacket(path, folder)));
        return packet.success();
    }

}
