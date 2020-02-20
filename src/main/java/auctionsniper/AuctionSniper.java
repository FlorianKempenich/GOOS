package auctionsniper;

import static auctionsniper.AuctionEventListener.PriceSource.FromSniper;

public class AuctionSniper implements AuctionEventListener {
    private final SniperListener listener;
    private final Auction auction;
    private boolean isWinning;

    public AuctionSniper(Auction auction, SniperListener listener) {
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
            listener.sniperWinning();
        } else {
            auction.bid(currentPrice + minBidIncrement);
            listener.sniperBidding();
        }
    }
}
