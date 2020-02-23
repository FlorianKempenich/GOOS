package auctionsniper;

public class AuctionSniper implements AuctionEventListener {
    private final SniperListener listener;
    private final Auction auction;
    private SniperSnapshot snapshot;

    public AuctionSniper(String itemId, Auction auction, SniperListener listener) {
        this.listener = listener;
        this.auction = auction;
        updateSnapshot(SniperSnapshot.joining(itemId));
    }

    private void updateSnapshot(SniperSnapshot newSnapshot) {
        snapshot = newSnapshot;
        listener.sniperStateChanged(snapshot);
    }

    @Override
    public void auctionClosed() {
        updateSnapshot(snapshot.closed());
    }

    @Override
    public void currentPrice(int currentPrice, int minBidIncrement, PriceSource source) {
        switch (source) {
            case FromSniper:
                updateSnapshot(snapshot.winning(currentPrice));
                break;

            case FromOtherBidder:
                int bid = currentPrice + minBidIncrement;
                auction.bid(bid);
                updateSnapshot(snapshot.bidding(currentPrice, bid));
                break;
        }
    }
}
