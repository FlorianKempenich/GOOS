package test;

import auctionsniper.Auction;
import auctionsniper.AuctionSniper;
import auctionsniper.SniperListener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuctionSniperTest {

    @Mock SniperListener listener;
    @Mock Auction auction;
    @InjectMocks AuctionSniper sniper;

    @Test
    void reportsLostWhenAuctionCloses() {
        sniper.auctionClosed();
        verify(listener).sniperLost();
    }

    @Test
    void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
        final int price = 1001;
        final int increment = 25;

        sniper.currentPrice(price, increment);

        verify(auction, times(1)).bid(price + increment);
        verify(listener).sniperBidding();
    }
}