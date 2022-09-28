package rembox.server.ui.main;

import rembox.server.ClientConnection;
import rembox.server.Server;
import rembox.server.ui.explorer.ExplorerUI;
import rembox.server.ui.manage.ManageClientUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class MainUI extends JFrame {
    private JPanel mainUIPanel;
    private JTextField portField;
    private JButton stopButton;
    private JButton startButton;
    private JLabel serverStatusBar;
    private JList<String> clientList;
    private static final JPopupMenu popupMenu = new JPopupMenu();
    private static int selectedIndex;

    public MainUI() {
        //Start UI
        setContentPane(mainUIPanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        setTitle("RemBox Server [v0.alpha]");
        setSize(1000, 800);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((d.width / 2) - (getWidth() / 2), (d.height / 2) - (getHeight() / 2));
        setVisible(true);

        addListeners();
        generateMenuItems();
    }

    //These methods are responsible for adding listeners
    private void addListeners() {
        addStartButtonListener();
        addStopButtonListener();
        addClientWindowListener();
        addPanelListener();
        addPortFieldListener();
    }
    private void addStartButtonListener() {
        startButton.addActionListener(e -> {
            int port;
            try {
                port = Integer.parseInt(portField.getText());
            } catch(NumberFormatException ex) {
                port = 32767;
            }
            Server.startServer(port);
            if(Server.isOnline()) {
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
                updateClients();
            }

            popupMenu.setVisible(false);

        });
    }
    private void addStopButtonListener() {
        stopButton.addActionListener(e -> {
            Server.stopServer();
            stopButton.setEnabled(false);
            startButton.setEnabled(true);
            popupMenu.setVisible(false);
        });
    }
    private void addClientWindowListener() {
        clientList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //Ensure the correct client is selected
                int row = clientList.locationToIndex(e.getPoint());
                clientList.setSelectedIndex(row);
                selectedIndex = row;

                //Open popup menu
                if(e.getButton() == MouseEvent.BUTTON3 && selectedIndex != -1) {
                    Point p = MouseInfo.getPointerInfo().getLocation();
                    popupMenu.setLocation((int)p.getX(), (int)p.getY());
                    popupMenu.setVisible(true);
                } else {
                    popupMenu.setVisible(false);
                }

            }
        });
    }
    private void addPanelListener() {
        mainUIPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                popupMenu.setVisible(false);
            }
        });
    }
    private void addPortFieldListener() {
        portField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                popupMenu.setVisible(false);
            }
        });
    }


    //These methods are used to generate the PopupMenu's items
    private void generateMenuItems() {
        generateManagerMenuItem();
        generateExplorerMenuItem();
    }
    private void generateManagerMenuItem() {
        JMenuItem manageItem = new JMenuItem("Manage this client");
        manageItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                MainUI.popupMenu.setVisible(false);
                ManageClientUI.create(Server.getClients().get(selectedIndex));
            }
        });
        popupMenu.add(manageItem);
    }
    private void generateExplorerMenuItem() {
        JMenuItem explorerItem = new JMenuItem("File Explorer");
        explorerItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                MainUI.popupMenu.setVisible(false);
                ExplorerUI.create(Server.getClients().get(selectedIndex));
            }
        });
        popupMenu.add(explorerItem);
    }

    //Called to update the client list
    public void updateClients() {
        if(Server.isOnline()) {
            serverStatusBar.setText("Server status: Online, " + Server.getClients().size() + " client" + (Server.getClients().size() != 1 ? "s" : "") + " connected");

            //Update client list
            ArrayList<ClientConnection> clients = Server.getClients();
            if(clients.size() > 0) {
                String[] clientNames = new String[clients.size()];
                for (int i = 0; i < clients.size(); i++) {
                    clientNames[i] = clients.get(i).getID();
                }
                clientList.setListData(clientNames);
            } else {
                clientList.setModel(new DefaultListModel());
            }
        } else {
            serverStatusBar.setText("Server status: Offline");

            //Clear client list
            clientList.setModel(new DefaultListModel());
        }
    }

}
