package rembox.server;

import rembox.packets.Packet;
import rembox.server.ui.explorer.ExplorerUI;
import rembox.server.ui.main.MainUI;
import rembox.server.ui.manage.ManageClientUI;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;

public class Server extends Thread {

    private static Server instance;
    private static ServerSocket socket;
    private static final ArrayList<ClientConnection> clients = new ArrayList<>();
    private static long packetIDCounter = -1;
    private volatile static boolean online = false;
    private static MainUI mainUI;

    //Set instance and start connection accepting loop
    public Server() {
        instance = this;
        mainUI = new MainUI();
        start();
    }

    //Starts the server on a certain port
    public static void startServer(int port) {
        port = port < 0 || port > 65536 ? 32767 : port;
        try {
            socket = new ServerSocket(port);
            online = true;
            box("The server is online on port " + socket.getLocalPort() + ".");
        } catch(IOException e) {
            if(port != 0) {
                startServer(0);
            } else {
                box("The server couldn't start: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    //Stops the server
    public static void stopServer() {
        //Close the socket
        online = false;
        try {
            for(ClientConnection c : clients) {
                //Cause an exception on the client to trigger a disconnect
                c.getSocket().getOutputStream().write(-1);
            }
            socket.close();
        } catch (IOException e) { e.printStackTrace(); }
        socket = null;

        //Reset saved data
        for(int i = 0; i < clients.size(); i++) {
            kick(clients.get(0));
        }
        PacketPool.reset();
        packetIDCounter = -1;
        mainUI.updateClients();
    }

    //Listens for new connections
    @Override
    public void run() {
        while(true) {
            if(online) {
                try {
                    ClientConnection c = new ClientConnection(socket.accept());
                    c.start();
                    clients.add(c);
                    mainUI.updateClients();
                } catch (SocketException | NullPointerException ignored) {
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //Sends a packet to a client
    public static void send(ClientConnection client, Packet packet) {
        client.send(packet);
    }

    //Kicks a client
    public static void kick(ClientConnection client) {
        clients.remove(client);
        ManageClientUI.close(client);
        ExplorerUI.close(client);
        mainUI.updateClients();
    }

    //Generates a unique PacketID
    public synchronized static long generateID() {
        packetIDCounter++;
        return packetIDCounter;
    }

    //Shows a MsgBox
    public static void box(String text) {
        JOptionPane.showMessageDialog(null, text);
    }

    //Getter methods
    public static ArrayList<ClientConnection> getClients() {
        return clients;
    }
    public static int getPort() {
        return socket.getLocalPort();
    }
    public static boolean isOnline() {
        return online;
    }
}
