package auctionsniper;

import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jxmpp.jid.EntityBareJid;

import java.util.HashMap;
import java.util.Map;

public class AuctionMessageTranslator implements IncomingChatMessageListener {
    private final AuctionEventListener listener;

    public AuctionMessageTranslator(AuctionEventListener listener) {
        this.listener = listener;
    }

    @Override
    public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
        Map<String, String> event = parse(message);
        String eventType = event.get("Event");
        if (eventType.equals("PRICE")) {
            listener.currentPrice(
                    Integer.parseInt(event.get("CurrentPrice")),
                    Integer.parseInt(event.get("Increment"))
            );
        } else {
            listener.auctionClosed();
        }
    }

    private Map<String, String> parse(Message message) {
        Map<String, String> parsed = new HashMap<>();
        String body = message.getBody();

        for (String part : body.split(";")) {
            String[] keyVal = part.split(":");
            String key = keyVal[0].trim();
            String value = keyVal[1].trim();
            parsed.put(key, value);
        }

        return parsed;
    }
}
