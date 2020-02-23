package test.end2end;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import test.end2end.support.ApplicationRunner;
import test.end2end.support.FakeAuctionServer;

import static test.end2end.support.ApplicationRunner.SNIPER_XMPP_ID;

@Tag("E2E")
public class AuctionSniperEndToEndTest {
    private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
    private final ApplicationRunner application = new ApplicationRunner();

    /* Notes
    Before starting the auctionServer, 3 accounts on the XMPP server are created:
    - USERNAME:PASSWORD
    - sniper@localhost:sniper
    - auction-item-54321@localhost:auction
    - auction-item-65432@localhost:auction
     */
    public AuctionSniperEndToEndTest() throws Exception { }

    @Test
    void sniperJoinsAuctionAndAuctionClosesImmediately() throws Exception {
        auction.startSellingItem();
        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID);
        auction.announceClosed();
        application.showsSniperHasLostAuction(auction, 0, 0);
    }

    @Test
    void sniperMakesAHigherBiDButLoses() throws Exception {
        auction.startSellingItem();
        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID);

        auction.reportPrice(1000, 98, "other bidder");
        application.showsSniperIsBidding(auction, 1000, 1098);

        auction.hasReceivedBidFrom(SNIPER_XMPP_ID, 1098);

        auction.announceClosed();
        application.showsSniperHasLostAuction(auction, 1000, 1098);
    }

    @Test
    void sniperWinsAnAuctionByBiddingHigher() throws Exception {
        auction.startSellingItem();
        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID);

        auction.reportPrice(1000, 98, "other bidder");
        application.showsSniperIsBidding(auction, 1000, 1098);

        auction.hasReceivedBidFrom(SNIPER_XMPP_ID, 1098);

        auction.reportPrice(1098, 103, SNIPER_XMPP_ID);
        application.showsSniperIsWinning(auction, 1098);

        auction.announceClosed();
        application.showsSniperHasWonAuction(auction, 1098);
    }

    @AfterEach
    void stopAuction() {
        auction.stop();
    }

    @AfterEach
    void stopApplication() {
        application.stop();
    }
}
