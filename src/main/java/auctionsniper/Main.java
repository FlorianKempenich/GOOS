package auctionsniper;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class Main {
    public static final String STATUS_JOINING = "Joining";
    public static final String STATUS_LOST = "Lost";

    public static final String AUCTION_RESOURCE = "Auction";
    public static final String ITEM_ID_AS_LOGIN = "auction-%s";
    public static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;

    private MainWindow ui;

    public Main() throws Exception {
        startUserInterface();
    }

    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait(() -> ui = new MainWindow());
    }

    public static void main(String xmppHostname, String sniperId, String sniperPassword, String itemId) throws Exception {
        Main main = new Main();

        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                .setXmppDomain(xmppHostname)
                .setHost(xmppHostname)
                .build();
        AbstractXMPPConnection connection = new XMPPTCPConnection(config);
        connection.connect();
        connection.login(sniperId, sniperPassword);

        ChatManager chatManager = ChatManager.getInstanceFor(connection);
        Chat chat = chatManager.chatWith(auctionId(itemId, connection));
        chat.send("fake join message");
    }

    private static EntityBareJid auctionId(String itemId, AbstractXMPPConnection connection) throws XmppStringprepException {
        String auctionId = String.format(
                AUCTION_ID_FORMAT,
                itemId,
                connection.getXMPPServiceDomain()
        );
        return JidCreate.entityBareFrom(auctionId);
    }

    public static class MainWindow extends JFrame {
        public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
        public static final String SNIPER_STATUS_NAME = "sniper status";
        private final JLabel sniperStatus = createLabel(STATUS_JOINING);

        public MainWindow() throws HeadlessException {
            super("Auction Sniper");
            setName(MAIN_WINDOW_NAME);
            add(sniperStatus);
            pack();
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setVisible(true);
        }

        private JLabel createLabel(String initialText) {
            JLabel result = new JLabel(initialText);
            result.setName(SNIPER_STATUS_NAME);
            result.setBorder(new LineBorder(Color.BLACK));
            return result;
        }
    }
}
