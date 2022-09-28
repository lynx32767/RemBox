package rembox;

import rembox.client.Client;
import rembox.server.Server;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        //Set the UI style to the default windows style, if applicable
        try { UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel"); } catch(Exception ignored){}

        //Start the server
        new Server();
        new Client();
    }

}
