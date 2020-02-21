package auctionsniper;

import java.util.Objects;

public class SniperSnapshot {
    public final String itemId;
    public final int lastPrice;
    public final int lastBid;

    public SniperSnapshot(String itemId, int lastPrice, int lastBid) {
        this.itemId = itemId;
        this.lastPrice = lastPrice;
        this.lastBid = lastBid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, lastPrice, lastBid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SniperSnapshot that = (SniperSnapshot) o;
        return lastPrice == that.lastPrice &&
                lastBid == that.lastBid &&
                itemId.equals(that.itemId);
    }
}

