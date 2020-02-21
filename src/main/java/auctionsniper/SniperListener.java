package auctionsniper;

public interface SniperListener {
    void sniperLost();
    void sniperBidding(SniperSnapshot state);
    void sniperWon();
    void sniperWinning();
}
