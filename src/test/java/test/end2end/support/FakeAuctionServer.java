package test.end2end.support;

import auctionsniper.Main;
import org.hamcrest.Matcher;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.sasl.SASLErrorException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;

public class FakeAuctionServer {
    public static final String XMPP_HOSTNAME = "localhost";
    public static final String ITEM_ID_AS_LOGIN = "auction-%s";
    public static final String AUCTION_RESOURCE = "Auction";
    private static final int TIMEOUT_SEC = 5;
    private static final String AUCTION_PASSWORD = "auction";

    private final SingleMessageListener messageListener = new SingleMessageListener();
    private final String itemId;
    private final AbstractXMPPConnection connection;
    private SaveChatAsInstanceVariable saveChatAsInstanceVariable = new SaveChatAsInstanceVariable();
    private ChatManager chatManager;
    private Chat oneAndOnlyChat;

    public FakeAuctionServer(String itemId) throws Exception {
        this.itemId = itemId;
        this.connection = buildConnection();
    }

    private static AbstractXMPPConnection buildConnection() throws XmppStringprepException {
        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                .setXmppDomain(XMPP_HOSTNAME)
                .setHost(XMPP_HOSTNAME)
                .build();

        return new XMPPTCPConnection(config);
    }

    public void startSellingItem() throws InterruptedException, IOException, SmackException, XMPPException {
        tryToConnect();
        tryToLogin();

        chatManager = ChatManager.getInstanceFor(connection);
        chatManager.addIncomingListener(saveChatAsInstanceVariable);
        chatManager.addIncomingListener(messageListener);
    }

    private void tryToConnect() throws InterruptedException, XMPPException, SmackException, IOException {
        try {
            connection.connect();
        } catch (SmackException.ConnectionException e) {
            printEjabberdInstructions("Failed to connect. Did you start 'ejabberd'? ");
            throw e;
        }
    }

    private void tryToLogin() throws IOException, InterruptedException, SmackException, XMPPException {
        try {
            connection.login(
                    format(ITEM_ID_AS_LOGIN, itemId),
                    AUCTION_PASSWORD,
                    Resourcepart.from(AUCTION_RESOURCE)
            );
        } catch (SASLErrorException e) {
            printEjabberdInstructions("Failed to login. Did you prepare 'ejabberd' for tests? ");
            throw e;
        }
    }

    private void printEjabberdInstructions(String reason) {
        System.err.println(reason);
        System.err.println("To start & prepare 'ejabberd':");
        System.err.println("  1. './ejabberd/start.sh'");
        System.err.println("  2. './ejabberd/prepare_for_end_to_end_test.sh'");
    }

    public void announceClosed() throws SmackException.NotConnectedException, InterruptedException {
        oneAndOnlyChat.send("SQLVersion: 1.1; Event: CLOSE");
    }

    public void reportPrice(int currentPrice, int nextBidIncrement, String bidder) throws SmackException.NotConnectedException, InterruptedException {
        oneAndOnlyChat.send(
                format(
                        "SQLVersion: 1.1; Event: PRICE; CurrentPrice: %d; Increment: %d; Bidder: %s;",
                        currentPrice,
                        nextBidIncrement,
                        bidder
                )
        );
    }

    public void hasReceivedJoinRequestFrom(String sniperId) throws InterruptedException, XmppStringprepException {
        messageListener.receivesAMessage(
                sniperId,
                equalTo(Main.JOIN_COMMAND_FORMAT)
        );
    }

    public void hasReceivedBidFrom(String sniperId, int amount) throws InterruptedException, XmppStringprepException {
        messageListener.receivesAMessage(
                sniperId,
                equalTo(format(Main.BID_COMMAND_FORMAT, amount))
        );
    }

    public void stop() {
        connection.disconnect();
    }

    private static class SingleMessageListener implements IncomingChatMessageListener {
        private final ArrayBlockingQueue<Message> messages = new ArrayBlockingQueue<>(1);

        @Override
        public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
            messages.add(message);
        }

        public void receivesAMessage(String expectedSenderId, Matcher<? super String> messageBodyMatcher) throws InterruptedException, XmppStringprepException {
            final Message message = messages.poll(TIMEOUT_SEC, SECONDS);

            EntityJid expectedSender = JidCreate.entityFrom(expectedSenderId);
            assertThat(message, hasProperty("body", messageBodyMatcher));
            assertThat(message, hasProperty("from", equalTo(expectedSender)));
        }
    }

    private class SaveChatAsInstanceVariable implements IncomingChatMessageListener {
        @Override
        public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
            FakeAuctionServer.this.oneAndOnlyChat = chat;
            FakeAuctionServer.this.chatManager.removeIncomingListener(this);
        }
    }
}
