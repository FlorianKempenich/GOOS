package auctionsniper;

import static auctionsniper.AuctionEventListener.PriceSource.FromSniper;
import static auctionsniper.SniperState.BIDDING;
import static auctionsniper.SniperState.WINNING;

public class AuctionSniper implements AuctionEventListener {
    private final SniperListener listener;
    private final Auction auction;
    private final String itemId;
    private boolean isWinning;

    public AuctionSniper(String itemId, Auction auction, SniperListener listener) {
        this.itemId = itemId;
        this.listener = listener;
        this.auction = auction;
    }

    @Override
    public void auctionClosed() {
        if (isWinning) {
            listener.sniperWon();
        } else {
            listener.sniperLost();
        }
    }

    @Override
    public void currentPrice(int currentPrice, int minBidIncrement, PriceSource source) {
        isWinning = source == FromSniper;
        if (isWinning) {
            listener.sniperStateChanged(new SniperSnapshot(itemId, currentPrice, currentPrice, WINNING));
        } else {
            int bid = currentPrice + minBidIncrement;
            auction.bid(bid);
            listener.sniperStateChanged(new SniperSnapshot(itemId, currentPrice, bid, BIDDING));
        }
    }
}
