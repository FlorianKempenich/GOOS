package auctionsniper;

public class AuctionSniper implements AuctionEventListener {
    private final SniperListener listener;
    private final Auction auction;

    public AuctionSniper(SniperListener listener, Auction auction) {
        this.listener = listener;
        this.auction = auction;
    }

    @Override
    public void auctionClosed() {
        listener.sniperLost();
    }

    @Override
    public void currentPrice(int currentPrice, int minBidIncrement) {
        auction.bid(currentPrice + minBidIncrement);
        listener.sniperBidding();
    }
}
