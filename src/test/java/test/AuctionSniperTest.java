package test;

import auctionsniper.Auction;
import auctionsniper.AuctionSniper;
import auctionsniper.SniperListener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static auctionsniper.AuctionEventListener.PriceSource.FromOtherBidder;
import static auctionsniper.AuctionEventListener.PriceSource.FromSniper;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuctionSniperTest {

    @Mock SniperListener listener;
    @Mock Auction auction;
    @InjectMocks AuctionSniper sniper;

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

        sniper.currentPrice(price, increment, FromOtherBidder);

        verify(auction, times(1)).bid(price + increment);
        verify(listener).sniperBidding();
    }

    @Test
    void doesNotBidHigherAndReportWinningWhenNewPriceIsFromSniper() {
        sniper.currentPrice(456, 123, FromSniper);
        verify(auction, never()).bid(anyInt());
        verify(listener).sniperWinning();
    }
}