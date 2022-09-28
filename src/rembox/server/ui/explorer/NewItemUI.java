package rembox.server.ui.explorer;

import rembox.lib.RemoteFile;
import rembox.lib.StringHelper;
import rembox.server.ClientConnection;
import rembox.server.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NewItemUI extends JFrame {
    private JTextField nameField;
    private JCheckBox folderBox;
    private JButton createButton;
    private JPanel pane;

    public NewItemUI(ClientConnection client, String suggestedPath) {

        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RemoteFile file = new RemoteFile(client, StringHelper.appendPath(suggestedPath, nameField.getText()));
                if(!file.create(folderBox.isSelected())) {
                    Server.box("File / Folder could not be created");
                } else {
                    ExplorerUI.displayPath_(null);
                }
                setVisible(false);
            }
        });

        setContentPane(pane);
        pack();
        setSize(500, getHeight());
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((d.width / 2) - (getWidth() / 2), (d.height / 2) - (getHeight() / 2));
        setVisible(true);
    }
}
