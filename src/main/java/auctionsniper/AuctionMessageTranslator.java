package auctionsniper;

import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jxmpp.jid.EntityBareJid;

import java.util.HashMap;
import java.util.Map;

import static auctionsniper.AuctionEventListener.PriceSource.FromOtherBidder;
import static auctionsniper.AuctionEventListener.PriceSource.FromSniper;

public class AuctionMessageTranslator implements IncomingChatMessageListener {
    private final AuctionEventListener listener;
    private final String sniperId;

    public AuctionMessageTranslator(String sniperId, AuctionEventListener listener) {
        this.sniperId = sniperId;
        this.listener = listener;
    }

    @Override
    public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
        // TODO: For each auction translator, check the 'from' field and ensure only dealing with messages for expected item (not doing now to follow the book)
        AuctionEvent event = AuctionEvent.from(message.getBody());
        String eventType = event.type();

        if ("CLOSE".equals(eventType)) {
            listener.auctionClosed();
        }

        if ("PRICE".equals(eventType)) {
            listener.currentPrice(
                    event.currentPrice(),
                    event.increment(),
                    event.priceSource(sniperId)
            );
        }
    }

    private static class AuctionEvent {
        private Map<String, String> fields;

        private AuctionEvent() { this.fields = new HashMap<>(); }

        public static AuctionEvent from(String eventMsg) {
            AuctionEvent event = new AuctionEvent();
            for (String field : fieldsIn(eventMsg)) {
                event.addField(field);
            }
            return event;
        }

        private static String[] fieldsIn(String eventMsg) { return eventMsg.split(";"); }

        private void addField(String field) {
            String[] keyVal = field.split(":");
            String key = keyVal[0].trim();
            String value = keyVal[1].trim();
            fields.put(key, value);
        }

        public AuctionEventListener.PriceSource priceSource(String sniperId) {
            return sniperId.equals(bidder()) ?
                    FromSniper :
                    FromOtherBidder;
        }

        private String bidder() { return get("Bidder"); }

        private String get(String fieldName) { return fields.get(fieldName); }

        public int increment() { return getInt("Increment"); }

        private int getInt(String fieldName) { return Integer.parseInt(get(fieldName)); }

        public String type() { return get("Event"); }

        public int currentPrice() { return getInt("CurrentPrice"); }
    }
}
