package rembox.server.ui.explorer;

import rembox.lib.RemoteFile;
import rembox.server.ClientConnection;
import rembox.server.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class EditFileUI extends JFrame {

    protected static ArrayList<EditFileUI> existing = new ArrayList<>();
    protected ClientConnection client;
    private RemoteFile file;
    private JButton saveButton;
    private JButton reloadButton;
    private JTextArea fileDisplayArea;
    private JPanel pane;

    public EditFileUI(ClientConnection client, RemoteFile file) {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                remove(client, file);
            }
        });
        this.client = client;
        this.file = file;

        refreshData();

        reloadButton.addActionListener(e -> {
            refreshData();
        });

        saveButton.addActionListener(e -> {
            saveData();
        });

        setTitle("Viewing file [" + file.getAbsolutePath() + "] on client [" + client.getID() + "]");
        setContentPane(pane);
        setSize(800, 700);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((d.width / 2) - (getWidth() / 2), (d.height / 2) - (getHeight() / 2));
        setVisible(true);
    }

    private void refreshData() {
        if(file.length() <= 1048576) {
            String data = new String(file.read(0, (int) file.length()));
            fileDisplayArea.setText(data);
        } else {
            Server.box("Error: The file became too big.");
            remove(client, file);
        }
    }

    private void saveData() {
        String data = fileDisplayArea.getText();
        if(data.length() <= 32768) {
            Server.box(file.write(data.getBytes(StandardCharsets.UTF_8), false) ? "File was successfully saved." : "An error occurred when trying to save the file");
        } else {
            Server.box("You are trying to save too much data. Download, edit and upload the file instead");
        }
    }

    //Methods for ensuring only one window per client
    public static void create(ClientConnection client, RemoteFile file_) {
        if(!exists(client, file_)) {
            if(file_.length() <= 32768) { //max 32kB
                existing.add(new EditFileUI(client, file_));
            } else {
                Server.box("The file you tried to view is too large (>32kB).\nPlease download it and open it on your machine instead.");
            }
        } else {
            for(EditFileUI ui : existing) {
                if(ui.getClient().equals(client) && ui.getFile().getAbsolutePath().equals(file_.getAbsolutePath())) {
                    ui.setState(NORMAL);
                    ui.setAlwaysOnTop(true);
                    ui.setAlwaysOnTop(false);
                }
            }
        }
    }

    public static void remove(ClientConnection client, RemoteFile file_) {
        existing.removeIf(ui -> ui.getClient().equals(client) && ui.getFile().equals(file_));
    }

    public ClientConnection getClient() {
        return client;
    }

    public RemoteFile getFile() {
        return file;
    }

    protected static boolean exists(ClientConnection client, RemoteFile file_) {
        for(EditFileUI ui : existing) {
            if(ui.getClient().equals(client) && ui.getFile().getAbsolutePath().equals(file_.getAbsolutePath())) {
                return true;
            }
        }
        return false;
    }

    public static void close(ClientConnection client) {
        existing.removeIf(ui -> ui.getClient().equals(client));
    }
}
