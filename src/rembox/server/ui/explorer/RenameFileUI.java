package rembox.server.ui.explorer;

import rembox.packets.file.FileRenamePacket;
import rembox.server.ClientConnection;
import rembox.server.PacketPool;
import rembox.server.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RenameFileUI extends JFrame {
    private JTextField sourceField;
    private JTextField targetField;
    private JButton renameButton;
    private JPanel pane;

    public RenameFileUI(ClientConnection client, String start) {
        sourceField.setText(start);
        targetField.setText(start);

        renameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileRenamePacket packet = (FileRenamePacket)PacketPool.awaitResponse(client, new FileRenamePacket(start, targetField.getText()));
                setVisible(false);
                ExplorerUI.displayPath_(null);
                Server.box(packet.success() ? "Rename successful" : "Rename unsuccessful");
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
