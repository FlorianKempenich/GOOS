package auctionsniper;

import static auctionsniper.AuctionEventListener.PriceSource.FromSniper;

public class AuctionSniper implements AuctionEventListener {
    private final SniperListener listener;
    private final Auction auction;
    private boolean isWinning;
    private SniperSnapshot snapshot;

    public AuctionSniper(String itemId, Auction auction, SniperListener listener) {
        this.listener = listener;
        this.auction = auction;
        this.snapshot = SniperSnapshot.joining(itemId);
    }

    @Override
    public void auctionClosed() {
        if (isWinning) {
            snapshot = new SniperSnapshot(snapshot.itemId, snapshot.lastPrice, snapshot.lastBid, SniperState.WON);
            listener.sniperStateChanged(snapshot);
        } else {
            listener.sniperLost();
        }
    }

    @Override
    public void currentPrice(int currentPrice, int minBidIncrement, PriceSource source) {
        isWinning = source == FromSniper;
        if (isWinning) {
            snapshot = snapshot.winning(currentPrice);
        } else {
            int bid = currentPrice + minBidIncrement;
            auction.bid(bid);
            snapshot = snapshot.bidding(currentPrice, bid);
        }
        listener.sniperStateChanged(snapshot);
    }
}
