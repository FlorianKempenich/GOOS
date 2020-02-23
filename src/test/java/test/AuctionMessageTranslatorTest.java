package test;

import auctionsniper.AuctionEventListener;
import auctionsniper.AuctionMessageTranslator;
import auctionsniper.util.Defect;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.packet.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static auctionsniper.AuctionEventListener.PriceSource.FromOtherBidder;
import static auctionsniper.AuctionEventListener.PriceSource.FromSniper;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuctionMessageTranslatorTest {
    private static final Chat UNUSED_CHAT = null;
    private static final EntityBareJid AUCTION_XMPP_ID = xmppId("auction-xmpp-id@domain");
    private static final String SNIPER_ID = "SomeSniperId";

    @Mock AuctionEventListener listener;
    private AuctionMessageTranslator translator;

    @BeforeEach
    void setUp() {
        translator = new AuctionMessageTranslator(SNIPER_ID, AUCTION_XMPP_ID, listener);
    }

    @Test
    void notifiesAuctionClosedWhenCloseMessageReceived() {
        Message message = new Message();
        message.setBody("SQLVersion: 1.1; Event: CLOSE;");

        translator.newIncomingMessage(AUCTION_XMPP_ID, message, UNUSED_CHAT);

        verify(listener).auctionClosed();
    }

    @Test
    void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromOtherBidder() {
        Message message = new Message();
        message.setBody("SQLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: " + SNIPER_ID + ";");

        translator.newIncomingMessage(AUCTION_XMPP_ID, message, UNUSED_CHAT);

        verify(listener).currentPrice(192, 7, FromSniper);
    }

    @Test
    void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromSniper() {
        Message message = new Message();
        message.setBody("SQLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: Someone else;");

        translator.newIncomingMessage(AUCTION_XMPP_ID, message, UNUSED_CHAT);

        verify(listener).currentPrice(192, 7, FromOtherBidder);
    }

    @Test
    void ignoresMessageWhenSenderIsNotCorrectAuction() {
        Message message = new Message();
        message.setBody("SQLVersion: 1.1; Event: CLOSE;");
        EntityBareJid someOtherAuction = xmppId("some-other-auction@domain");

        translator.newIncomingMessage(someOtherAuction, message, UNUSED_CHAT);

        verify(listener, never()).auctionClosed();
    }

    private static EntityBareJid xmppId(String xmppId) {
        try {
            return JidCreate.entityBareFrom(xmppId);
        } catch (XmppStringprepException e) {
            throw new Defect("XMPP Id creation failed");
        }
    }
}