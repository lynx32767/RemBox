package rembox.server;

import rembox.packets.Packet;
import rembox.packets.client.GetNamePacket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientConnection extends Thread {

    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private long lastExceptionTime = 0;

    public ClientConnection(Socket socket) {
        try {
            //Init socket and streams
            this.socket = socket;
            this.outputStream = new ObjectOutputStream(socket.getOutputStream());
            this.inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream.flush();

            //Start listening loop
        } catch(IOException e) {
            //If stream init fails, kick this client and retry
            invalidate();
            e.printStackTrace(); //debug
        }
    }

    //Sends a packet to the client
    public void send(Packet packet) {
        try {
            outputStream.writeObject(packet);
        } catch(Exception e) {
            checkExceptions();
            e.printStackTrace();
        }
    }

    //Listens for packets
    @Override
    public void run() {
        while(true) {
            try {
                PacketPool.addPacket((Packet)inputStream.readObject());
            } catch(NullPointerException e){
                e.printStackTrace();
                break;
            } catch(Exception e) {
                checkExceptions();
                e.printStackTrace();
            }
        }
    }

    //Terminates the connection from the server side
    public void invalidate() {
        System.out.println("invalidate");
        outputStream = null;
        inputStream = null;
        Server.kick(this);
    }

    //Used for checking if the streams cause repeated exceptions
    private void checkExceptions() {
        if(System.currentTimeMillis() - lastExceptionTime < 250) {
            //If the stream generates exceptions faster than every 250ms, it's probably broken so we kick the client
            invalidate();
        }
        lastExceptionTime = System.currentTimeMillis();
    }

    //Getter
    public String getID() {
        return "Dev";
    }
    public Socket getSocket() {
        return socket;
    }

}
