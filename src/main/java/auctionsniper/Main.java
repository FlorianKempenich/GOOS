package auctionsniper;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import static java.lang.String.format;

public class Main {
    public static final String AUCTION_RESOURCE = "Auction";
    public static final String ITEM_ID_AS_LOGIN = "auction-%s";
    public static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;
    public static final String JOIN_COMMAND_FORMAT = "TODO JOIN";
    public static final String BID_COMMAND_FORMAT = "SQLVersion: 1.1; Command: BID; Price: %d;";

    public static final int XMPP_HOSTNAME_ARG = 0;
    public static final int SNIPER_ID_ARG = 1;
    public static final int SNIPER_PASSWORD_ARG = 2;
    public static final int ITEM_ID_ARG = 3;

    private MainWindow ui;

    public Main() throws Exception {
        startUserInterface();
    }

    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait(() -> ui = new MainWindow());
    }

    public static void main(String[] args) throws Exception {
        String xmppHostname = args[XMPP_HOSTNAME_ARG];
        String sniperId = args[SNIPER_ID_ARG];
        String sniperPassword = args[SNIPER_PASSWORD_ARG];
        String itemId = args[ITEM_ID_ARG];

        Main main = new Main();
        main.joinAuction(
                connectAndLogin(xmppHostname, sniperId, sniperPassword),
                itemId
        );
    }

    private void joinAuction(AbstractXMPPConnection connection, String itemId) throws XmppStringprepException {
        disconnectWhenUICloses(connection);
        ChatManager chatManager = ChatManager.getInstanceFor(connection);
        Chat chatWithItem = chatManager.chatWith(auctionId(itemId, connection));

        Auction auction = new XMPPAuction(chatWithItem);
        chatManager.addIncomingListener(
                new AuctionMessageTranslator(
                        connection.getUser().toString(),
                        new AuctionSniper(
                                auction,
                                new SniperStateDisplayer()
                        )
                )
        );
        auction.join();
    }

    private static AbstractXMPPConnection connectAndLogin(String xmppHostname, String username, String sniperPassword) throws SmackException, IOException, XMPPException, InterruptedException {
        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                .setXmppDomain(xmppHostname)
                .setHost(xmppHostname)
                .build();
        AbstractXMPPConnection connection = new XMPPTCPConnection(config);
        connection.connect();
        connection.login(username, sniperPassword, Resourcepart.from(AUCTION_RESOURCE));
        return connection;
    }

    private void disconnectWhenUICloses(AbstractXMPPConnection connection) {
        WindowAdapter disconnectWhenWindowCloses = new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                connection.disconnect();
            }
        };
        ui.addWindowListener(disconnectWhenWindowCloses);
    }

    private static EntityBareJid auctionId(String itemId, AbstractXMPPConnection connection) throws XmppStringprepException {
        String auctionId = format(
                AUCTION_ID_FORMAT,
                itemId,
                connection.getXMPPServiceDomain()
        );
        return JidCreate.entityBareFrom(auctionId);
    }

    public static class XMPPAuction implements Auction {
        private final Chat chatWithAuctionItem;

        public XMPPAuction(Chat chatWithAuctionItem) {
            this.chatWithAuctionItem = chatWithAuctionItem;
        }

        @Override
        public void bid(int amount) {
            sendMessageToItem(format(BID_COMMAND_FORMAT, amount));
        }

        @Override
        public void join() {
            sendMessageToItem(JOIN_COMMAND_FORMAT);
        }

        private void sendMessageToItem(String message) {
            try {
                chatWithAuctionItem.send(message);
            } catch (SmackException.NotConnectedException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static class MainWindow extends JFrame {
        public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
        public static final String SNIPER_STATUS_NAME = "sniper status";

        public static final String STATUS_JOINING = "Joining";
        public static final String STATUS_LOST = "Lost";
        public static final String STATUS_BIDDING = "Bidding";
        public static final String STATUS_WINNING = "Winning";
        public static final String STATUS_WON = "Won";

        private final JLabel sniperStatus = createLabel(STATUS_JOINING);

        public MainWindow() throws HeadlessException {
            super("Auction Sniper");
            setName(MAIN_WINDOW_NAME);
            add(sniperStatus);
            pack();
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setVisible(true);
        }

        public void showStatus(String status) {
            sniperStatus.setText(status);
        }

        private JLabel createLabel(String initialText) {
            JLabel result = new JLabel(initialText);
            result.setName(SNIPER_STATUS_NAME);
            result.setBorder(new LineBorder(Color.BLACK));
            return result;
        }
    }

    public class SniperStateDisplayer implements SniperListener {
        @Override
        public void sniperLost() {
            showStatus(MainWindow.STATUS_LOST);
        }

        @Override
        public void sniperBidding() {
            showStatus(MainWindow.STATUS_BIDDING);
        }

        @Override
        public void sniperWon() {
            showStatus(MainWindow.STATUS_WON);
        }

        @Override
        public void sniperWinning() {
            showStatus(MainWindow.STATUS_WINNING);
        }

        private void showStatus(String status) {
            SwingUtilities.invokeLater(() -> ui.showStatus(status));
        }
    }
}
