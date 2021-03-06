package test;

import auctionsniper.*;
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
        verify(listener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 0, 0, SniperState.LOST));
    }

    @Test
    void reportsLostWhenAuctionClosesLastBidderWasNotSniper() {
        final int price = 1001;
        final int increment = 25;
        final int bid = price + increment;
        sniper.currentPrice(price, increment, FromOtherBidder);

        sniper.auctionClosed();

        verify(listener).sniperStateChanged(new SniperSnapshot(ITEM_ID, price, bid, SniperState.LOST));
    }

    @Test
    void reportsWinWhenAuctionClosesWhenLastBidderWasSniper() {
        int lastPrice = 100;
        sniper.currentPrice(100, 5, FromSniper);

        sniper.auctionClosed();

        verify(listener).sniperStateChanged(new SniperSnapshot(ITEM_ID, lastPrice, lastPrice, SniperState.WON));
    }

    @Test
    void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
        final int price = 1001;
        final int increment = 25;
        final int bid = price + increment;

        sniper.currentPrice(price, increment, FromOtherBidder);

        verify(auction, times(1)).bid(bid);
        verify(listener).sniperStateChanged(new SniperSnapshot(ITEM_ID, price, bid, SniperState.BIDDING));
    }

    @Test
    void doesNotBidHigherAndReportWinningWhenNewPriceIsFromSniper() {
        final int price = 456;
        final int increment = 123;

        sniper.currentPrice(price, increment, FromSniper);

        verify(auction, never()).bid(anyInt());
        verify(listener).sniperStateChanged(new SniperSnapshot(ITEM_ID, price, price, SniperState.WINNING));
    }
}