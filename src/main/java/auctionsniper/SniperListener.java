package auctionsniper;

public interface SniperListener {
    void sniperLost();
    void sniperBidding(SniperState state);
    void sniperWon();
    void sniperWinning();
}
