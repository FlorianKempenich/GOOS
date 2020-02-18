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
        AuctionEvent event = AuctionEvent.from(message.getBody());
        String eventType = event.type();

        if ("CLOSE".equals(eventType)) {
            listener.auctionClosed();
        }

        if ("PRICE".equals(eventType)) {
            listener.currentPrice(
                    event.currentPrice(),
                    event.increment()
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

        private static String[] fieldsIn(String eventMsg) {
            return eventMsg.split(";");
        }

        private void addField(String field) {
            String[] keyVal = field.split(":");
            String key = keyVal[0].trim();
            String value = keyVal[1].trim();
            fields.put(key, value);
        }

        public int increment() { return getInt("Increment"); }

        private int getInt(String fieldName) { return Integer.parseInt(get(fieldName)); }

        private String get(String fieldName) { return fields.get(fieldName); }

        public String type() { return get("Event"); }

        public int currentPrice() { return getInt("CurrentPrice"); }
    }
}
