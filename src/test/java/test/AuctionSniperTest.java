package test;

import auctionsniper.AuctionSniper;
import auctionsniper.SniperListener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuctionSniperTest {

    @Mock SniperListener listener;
    @InjectMocks AuctionSniper sniper;

    @Test
    void reportsLostWhenAuctionCloses() {
        sniper.auctionClosed();
        verify(listener).sniperLost();
    }
}