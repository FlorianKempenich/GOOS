package test;

import auctionsniper.AuctionEventListener;
import auctionsniper.AuctionMessageTranslator;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.packet.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.jxmpp.jid.EntityBareJid;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuctionMessageTranslatorTest {
    private static final Chat UNUSED_CHAT = null;
    private static final EntityBareJid UNUSED_JID = null;

    @Mock AuctionEventListener listener;
    @InjectMocks AuctionMessageTranslator translator;

    @Test
    void notifiesAuctionClosedWhenCloseMessageReceived() {
        Message message = new Message();
        message.setBody("SQLVersion: 1.1; Event: CLOSE");

        translator.newIncomingMessage(UNUSED_JID, message, UNUSED_CHAT);

        verify(listener).auctionClosed();
    }
}