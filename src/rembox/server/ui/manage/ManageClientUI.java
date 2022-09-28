package rembox.server.ui.manage;

import rembox.packets.client.RenameClientPacket;
import rembox.server.ClientConnection;
import rembox.server.PacketPool;
import rembox.server.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class ManageClientUI extends JFrame {

    protected static ArrayList<ManageClientUI> existing = new ArrayList<>();
    protected ClientConnection client;
    private JPanel pane;
    private JButton closeClientButton;
    private JTextField renameClientField;
    private JButton renameButton;

    //Constructor
    private ManageClientUI(ClientConnection client) {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                close(client);
            }
        });
        this.client = client;

        addListeners();

        setContentPane(pane);
        setTitle("Manage client " + client.getID());
        setSize(800, 700);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((d.width / 2) - (getWidth() / 2), (d.height / 2) - (getHeight() / 2));
        setVisible(true);
    }

    //Methods for listeners
    private void addListeners() {
        addRenameButtonListener();
    }
    private void addRenameButtonListener() {
        renameButton.addActionListener(e -> {
            RenameClientPacket packet = (RenameClientPacket)PacketPool.awaitResponse(client, new RenameClientPacket(renameClientField.getText()));

            if(!packet.success()) {
                Server.box("Client could not save name change on its system, the name will not persist as a result.");
            }
        });
    }

    //Methods for ensuring only one window per client
    public static void create(ClientConnection client) {
        if(!exists(client)) {
            existing.add(new ManageClientUI(client));
        } else {
            for(ManageClientUI ui : existing) {
                if(ui.getClient().equals(client)) {
                    ui.setState(NORMAL);
                    ui.setAlwaysOnTop(true);
                    ui.setAlwaysOnTop(false);
                }
            }
        }
    }
    public ClientConnection getClient() {
        return client;
    }
    protected static boolean exists(ClientConnection client) {
        for(ManageClientUI ui : existing) {
            if(ui.getClient().equals(client)) {
                return true;
            }
        }
        return false;
    }
    public static void close(ClientConnection client) {
        existing.removeIf(ui -> ui.getClient().equals(client));
    }

}
