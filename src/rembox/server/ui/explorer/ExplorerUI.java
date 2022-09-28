package rembox.server.ui.explorer;

import rembox.lib.RemoteFile;
import rembox.lib.StringHelper;
import rembox.packets.file.*;
import rembox.server.ClientConnection;
import rembox.server.PacketPool;
import rembox.server.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class ExplorerUI extends JFrame {

    protected static ArrayList<ExplorerUI> existing = new ArrayList<>();
    protected static ClientConnection client;
    private JPanel pane;
    private JTextField searchBar;
    private JList<String> explorerList;
    private JList<String> quickAccessList;
    private static String lastValidFolder = "Root";
    private static ExplorerUI instance;
    private final JPopupMenu popupMenu = new JPopupMenu();
    private static JMenuItem openInternally;
    private static JMenuItem download;
    private static JMenuItem rename;
    private static JMenuItem run;
    private static JMenuItem delete;
    private static JMenuItem properties;

    protected ExplorerUI(ClientConnection client) {
        instance = this;
        ExplorerUI.client = client;

        //Prepare UI
        setContentPane(pane);
        setTitle("Remote File Explorer [" + client.getID() + "]");
        setSize(800, 700);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((d.width / 2) - (getWidth() / 2), (d.height / 2) - (getHeight() / 2));
        setVisible(true);
        displayPath("Root");
        addListeners();
        generateMenuItems();

    }



    //Sets the explorer view to path, or opens the file interaction ui
    private void displayPath(String _path) {
        String path = _path;
        if(path.equals("C:")) {
            path = "C:\\";
        }

        FileInfoPacket file = (FileInfoPacket) (PacketPool.awaitResponse(client, new FileInfoPacket(path)));
        if(!file.exists()) {
            searchBar.setText(lastValidFolder);
            Server.box("The folder you are trying to view either does not exist, or the client doesn't have the required permissions to open it.");
            return;
        }

        if(file.isFolder()) {
            searchBar.setText(path);
            lastValidFolder = searchBar.getText();

            String[] data = file.getContainedFiles();
            if(data != null && data.length > 0) {
                explorerList.setListData(data);
            } else {
                explorerList.setModel(new DefaultListModel<>());
            }
        }

    }
    public static void displayPath_(String path_) {
        if(path_ != null) {
            instance.displayPath(path_);
        } else {
            instance.displayPath(lastValidFolder);
        }
    }


    //These methods are responsible for generating listeners of UI elements
    private void addListeners() {
        addExplorerViewListeners();
        addPaneListeners();
        addQuickAccessListeners();
        addSearchBarListeners();
        addWindowListeners();
    }
    private void addExplorerViewListeners() {
        explorerList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //Left double click
                if(e.getClickCount() % 2 == 0 && e.getButton() == MouseEvent.BUTTON1) {
                    int index = explorerList.locationToIndex(MouseInfo.getPointerInfo().getLocation());
                    if(index != -1) {
                        if(searchBar.getText().equals("Root")) {
                            displayPath(explorerList.getSelectedValue());
                        } else {
                            displayPath(StringHelper.appendPath(searchBar.getText(), explorerList.getSelectedValue()));
                        }
                    }
                    popupMenu.setVisible(false);
                } else if(e.getButton() == MouseEvent.BUTTON2) {
                    //Go up one level
                    displayPath(StringHelper.parentFile(searchBar.getText()));
                    popupMenu.setVisible(false);
                } else if(e.getButton() == MouseEvent.BUTTON3) {
                    //Bring up the context menu
                    int index = explorerList.locationToIndex(e.getPoint());
                    explorerList.setSelectedIndex(index);

                    RemoteFile file = new RemoteFile(client, StringHelper.appendPath(searchBar.getText(), explorerList.getSelectedValue()));
                    boolean valid = !searchBar.getText().equals("Root") && !file.isFolder();
                    boolean indexExists = explorerList.getSelectedIndex() != -1;
                    openInternally.setEnabled(valid && indexExists);
                    download.setEnabled(valid && indexExists);
                    rename.setEnabled(indexExists);
                    run.setEnabled(indexExists);
                    delete.setEnabled(indexExists);

                    Point p = MouseInfo.getPointerInfo().getLocation();
                    popupMenu.setLocation((int)p.getX(), (int)p.getY());
                    popupMenu.setVisible(true);
                } else {
                    popupMenu.setVisible(false);
                }


            }
        });
    }
    private void addPaneListeners() {
        pane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                popupMenu.setVisible(false);
            }
        });
    }
    private void addQuickAccessListeners() {
        quickAccessList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                popupMenu.setVisible(false);
            }
        });
    }
    private void addSearchBarListeners() {
        searchBar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchBar.setFocusable(false);
                    searchBar.setFocusable(true);
                    displayPath(searchBar.getText());
                }
            }
        });
    }
    private void addWindowListeners() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                close(client);
            }
        });
    }


    //These methods are responsible for generating the PopupMenu's components
    private void generateMenuItems() {
        generateOpenMenuItem();
        generateNewFileMenuItem();
        generateDownloadMenuItem();
        generateUploadMenuItem();
        generateExecuteMenuItem();
        generateRenameMenuItem();
        generateDeleteMenuItem();
        generatePropertiesMenuItem();
    }
    private void generateOpenMenuItem() {
        openInternally = new JMenuItem("Open in internal editor");
        openInternally.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                popupMenu.setVisible(false);
                if(openInternally.isEnabled()) {
                    EditFileUI.create(client, new RemoteFile(client, StringHelper.appendPath(lastValidFolder, explorerList.getSelectedValue())));
                }
            }
        });
        popupMenu.add(openInternally);
    }
    private void generateNewFileMenuItem() {
        JMenuItem newItem = new JMenuItem("New");
        newItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                popupMenu.setVisible(false);
                new NewItemUI(client, lastValidFolder);
            }
        });
        popupMenu.add(newItem);
    }
    private void generateDownloadMenuItem() {
        download = new JMenuItem("Download");
        download.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                popupMenu.setVisible(false);
                if(download.isEnabled()) {
                    //Ensure download folder exists
                    File downloadFolder = new File("download\\" + client.getID());
                    if(!downloadFolder.exists() || downloadFolder.isFile()) {
                        downloadFolder.mkdirs();
                    }

                    RemoteFile file = new RemoteFile(client, StringHelper.appendPath(lastValidFolder, explorerList.getSelectedValue()));

                    try {
                        File downFile = new File("download\\" + client.getID() + "\\" + file.getName());
                        FileOutputStream ostream = new FileOutputStream(downFile, false);
                        for(long bytesLeft = file.length(); bytesLeft > 0;) {
                            int bytesToDownload;
                            if(bytesLeft >= 32768) {
                                bytesToDownload = 32768;
                            } else {
                                bytesToDownload = (int)bytesLeft;
                            }
                            ostream.write(file.read(file.length() - bytesLeft, bytesToDownload));
                            bytesLeft -= bytesToDownload;
                            System.out.println("Bytes left: " + bytesLeft);
                            ostream.flush();
                        }
                        ostream.close();
                        Server.box("Download complete!");
                    } catch(IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        popupMenu.add(download);
    }
    private void generateUploadMenuItem() {
        JMenuItem upload = new JMenuItem("Upload");
        upload.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                popupMenu.setVisible(false);
                JFileChooser chooser = new JFileChooser();

                if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    try {
                        File f = chooser.getSelectedFile();

                        if(f.isFile()) {
                            FileInputStream instream = new FileInputStream(f);
                            RemoteFile rfile = new RemoteFile(client, StringHelper.appendPath(lastValidFolder, f.getName()));

                            byte[] bytes;
                            for(long bytesLeft = f.length(); bytesLeft > 0; ) {
                                int bytesToDownload;
                                if(bytesLeft >= 32768) {
                                    bytesToDownload = 32768;
                                } else {
                                    bytesToDownload = (int)bytesLeft;
                                }
                                bytes = new byte[bytesToDownload];
                                instream.read(bytes);

                                rfile.write(bytes, bytesLeft != f.length());

                                bytesLeft -= bytesToDownload;
                                System.out.println("Bytes left: " + bytesLeft);
                            }
                        } else {
                            Server.box("You can't upload folders (yet)");
                        }
                        displayPath_(null);
                    } catch(IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        popupMenu.add(upload);
    }
    private void generateExecuteMenuItem() {
        run = new JMenuItem("Run remotely");
        run.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                popupMenu.setVisible(false);
                if(run.isEnabled()) {
                    FileExecutePacket packet = (FileExecutePacket) PacketPool.awaitResponse(client, new FileExecutePacket(StringHelper.appendPath(lastValidFolder, explorerList.getSelectedValue())));
                    Server.box(packet.success() ? "Remote execution successful" : "Remote execution failed");
                }
            }
        });
        popupMenu.add(run);
    }
    private void generateRenameMenuItem() {
        rename = new JMenuItem("Rename");
        rename.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                popupMenu.setVisible(false);
                if(rename.isEnabled()) {
                    new RenameFileUI(client, StringHelper.appendPath(lastValidFolder, explorerList.getSelectedValue()));
                }
            }
        });
        popupMenu.add(rename);
    }
    private void generateDeleteMenuItem() {
        delete = new JMenuItem("Delete");
        delete.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                popupMenu.setVisible(false);
                FileDeletePacket packet = (FileDeletePacket)PacketPool.awaitResponse(client, new FileDeletePacket(StringHelper.appendPath(lastValidFolder, explorerList.getSelectedValue())));
                displayPath_(null);
                Server.box(packet.success() ? "Deleted file successfully" : "Couldn't delete file");
            }
        });
        popupMenu.add(delete);
    }
    private void generatePropertiesMenuItem() {
        properties = new JMenuItem("Properties");
        properties.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //Coming soon
            }
        });
    }


    //Methods for ensuring only one window per client
    private static boolean exists(ClientConnection client) {
        for(ExplorerUI ui : existing) {
            if(ui.getClient().equals(client)) {
                return true;
            }
        }
        return false;
    }
    public static void create(ClientConnection client) {
        if(!exists(client)) {
            existing.add(new ExplorerUI(client));
        } else {
            for(ExplorerUI ui : existing) {
                if(ui.getClient().equals(client)) {
                    ui.setState(NORMAL);
                    ui.setAlwaysOnTop(true);
                    ui.setAlwaysOnTop(false);
                }
            }
        }
    }
    public static void close(ClientConnection client) {
        existing.removeIf(ui -> ui.getClient().equals(client));
    }
    private ClientConnection getClient() {
        return client;
    }

}
