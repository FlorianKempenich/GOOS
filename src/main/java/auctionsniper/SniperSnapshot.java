package auctionsniper;

import com.google.common.base.MoreObjects;

import java.util.Objects;

public class SniperSnapshot {
    public final String itemId;
    public final int lastPrice;
    public final int lastBid;
    public final SniperState state;

    public SniperSnapshot(String itemId, int lastPrice, int lastBid, SniperState state) {
        this.itemId = itemId;
        this.lastPrice = lastPrice;
        this.lastBid = lastBid;
        this.state = state;
    }

    public static SniperSnapshot joining(String itemId) {
        return new SniperSnapshot(itemId, 0, 0, SniperState.JOINING);
    }

    public SniperSnapshot winning(int price) {
        return new SniperSnapshot(itemId, price, price, SniperState.WINNING);
    }

    public SniperSnapshot bidding(int lastPrice, int lastBid) {
        return new SniperSnapshot(itemId, lastPrice, lastBid, SniperState.BIDDING);
    }

    public SniperSnapshot closed() {
        SniperState newState;
        switch (state) {
            case WINNING:
                newState = SniperState.WON;
                break;
            case BIDDING:
            case JOINING:
                newState = SniperState.LOST;
                break;
            default:
                throw new IllegalStateException("Can't close in given current SniperState: " + state);
        }

        return new SniperSnapshot(itemId, lastPrice, lastBid, newState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, lastPrice, lastBid, state);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SniperSnapshot that = (SniperSnapshot) o;
        return lastPrice == that.lastPrice &&
                lastBid == that.lastBid &&
                Objects.equals(itemId, that.itemId) &&
                state == that.state;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("itemId", itemId)
                .add("lastPrice", lastPrice)
                .add("lastBid", lastBid)
                .add("state", state)
                .toString();
    }
}

