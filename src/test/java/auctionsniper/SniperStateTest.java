package auctionsniper;

import auctionsniper.util.Defect;
import org.junit.jupiter.api.Test;

import static auctionsniper.SniperState.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SniperStateTest {

    @Test
    void closeAuctionWhenJoining_Lost() {
        assertEquals(LOST, JOINING.whenAuctionClosed());
    }

    @Test
    void closeAuctionWhenBidding_Lost() {
        assertEquals(LOST, BIDDING.whenAuctionClosed());
    }

    @Test
    void closeAuctionWhenWinning_Won() {
        assertEquals(WON, WINNING.whenAuctionClosed());
    }

    @Test
    void closeAuctionWhenLost_alreadyClosed() {
        Defect exception = assertThrows(
                Defect.class,
                LOST::whenAuctionClosed
        );
        assertThat(exception.getMessage(), containsString("already closed"));
    }

    @Test
    void closeAuctionWhenWon_alreadyClosed() {
        Defect exception = assertThrows(
                Defect.class,
                WON::whenAuctionClosed
        );
        assertThat(exception.getMessage(), containsString("already closed"));
    }
}