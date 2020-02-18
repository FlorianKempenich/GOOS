package auctionsniper.sandbox;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.parts.Resourcepart;
import org.mockito.InjectMocks;

public class SmackSandbox {
    public static final String XMPP_HOSTNAME = "localhost";
    public static final String ITEM_ID_AS_LOGIN = "auction-%s";
    public static final String AUCTION_RESOURCE = "Auction";
    private static final String AUCTION_PASSWORD = "auction";

    private final String itemId = "item-54321";
    private Chat theOneAndOnlyChat;

    @Test
    @Disabled("Sandbox")
    void tryToConnect() throws Exception {
        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                .setXmppDomain(XMPP_HOSTNAME)
                .setHost(XMPP_HOSTNAME)
                .build();

        AbstractXMPPConnection connection = new XMPPTCPConnection(config);

        connection.connect();
        connection.login(
                String.format(ITEM_ID_AS_LOGIN, itemId),
                AUCTION_PASSWORD,
                Resourcepart.from(AUCTION_RESOURCE)
        );

        ChatManager chatManager = ChatManager.getInstanceFor(connection);
        chatManager.addIncomingListener(this::newIncomingMessage);

        sleep(20);

        connection.disconnect();
    }

    private void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
        theOneAndOnlyChat = chat;
        System.out.println("New message from " + from + ": " + message.getBody());
        try {
            chat.send("Yo, Message received!! :D");
        } catch (SmackException.NotConnectedException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sleep(int durationSeconds) {
        try {
            Thread.sleep(durationSeconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void yoasdf(AbstractXMPPConnection connection) {
    }
}
