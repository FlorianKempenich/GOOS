package auctionsniper;

public interface AuctionEventListener {
    void auctionClosed();

    void currentPrice(int currentPrice, int minBidIncrement, PriceSource source);

    enum PriceSource {FromSniper, FromOtherBidder}
}
