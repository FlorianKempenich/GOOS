package test;

import auctionsniper.Auction;
import auctionsniper.AuctionSniper;
import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static auctionsniper.AuctionEventListener.PriceSource.FromOtherBidder;
import static auctionsniper.AuctionEventListener.PriceSource.FromSniper;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuctionSniperTest {
    private static final String ITEM_ID = "some-item-12345";

    @Mock SniperListener listener;
    @Mock Auction auction;
    private AuctionSniper sniper;

    @BeforeEach
    void setUp() {
        sniper = new AuctionSniper(ITEM_ID, auction, listener);
    }

    @Test
    void reportsLostWhenAuctionClosesWhenNoPriceWasAnnounced() {
        sniper.auctionClosed();
        verify(listener).sniperLost();
    }

    @Test
    void reportsLostWhenAuctionClosesLastBidderWasNotSniper() {
        sniper.currentPrice(100, 5, FromOtherBidder);
        sniper.auctionClosed();
        verify(listener).sniperLost();
    }

    @Test
    void reportsWinWhenAuctionClosesWhenLastBidderWasSniper() {
        sniper.currentPrice(100, 5, FromSniper);
        sniper.auctionClosed();
        verify(listener).sniperWon();
    }

    @Test
    void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
        final int price = 1001;
        final int increment = 25;
        final int bid = price + increment;

        sniper.currentPrice(price, increment, FromOtherBidder);

        verify(auction, times(1)).bid(bid);
        verify(listener).sniperBidding(new SniperSnapshot(ITEM_ID, price, bid));
    }

    @Test
    void doesNotBidHigherAndReportWinningWhenNewPriceIsFromSniper() {
        sniper.currentPrice(456, 123, FromSniper);
        verify(auction, never()).bid(anyInt());
        verify(listener).sniperWinning();
    }
}