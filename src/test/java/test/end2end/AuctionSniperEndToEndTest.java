package test.end2end;

import org.junit.jupiter.api.Disabled;
import test.end2end.support.ApplicationRunner;
import test.end2end.support.FakeAuctionServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static test.end2end.support.ApplicationRunner.SNIPER_XMPP_ID;

@Tag("E2E")
public class AuctionSniperEndToEndTest {
    private final FakeAuctionServer auctionServer = new FakeAuctionServer("item-54321");
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
        auctionServer.startSellingItem();
        application.startBiddingIn(auctionServer);
        auctionServer.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID);
        auctionServer.announceClosed();
        application.showsSniperHasLostAuction();
    }

    @Test
    void sniperMakesAHigherBiDButLoses() throws Exception {
        auctionServer.startSellingItem();
        application.startBiddingIn(auctionServer);
        auctionServer.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID);

        auctionServer.reportPrice(1000, 98, "other bidder");
        application.showsSniperIsBidding(1000, 1098);

        auctionServer.hasReceivedBidFrom(SNIPER_XMPP_ID, 1098);

        auctionServer.announceClosed();
        application.showsSniperHasLostAuction();
    }

    @Test
    void sniperWinsAnAuctionByBiddingHigher() throws Exception {
        auctionServer.startSellingItem();
        application.startBiddingIn(auctionServer);
        auctionServer.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID);

        auctionServer.reportPrice(1000, 98, "other bidder");
        application.showsSniperIsBidding(1000, 1098);

        auctionServer.hasReceivedBidFrom(SNIPER_XMPP_ID, 1098);

        auctionServer.reportPrice(1098, 103, SNIPER_XMPP_ID);
//        application.showsSniperIsWinning(1098);
        application.showsSniperIsWinning();

        auctionServer.announceClosed();
//        application.showsSniperHasWonAuction(1098);
        application.showsSniperHasWonAuction();
    }

    @AfterEach
    void stopAuction() {
        auctionServer.stop();
    }

    @AfterEach
    void stopApplication() {
        application.stop();
    }
}
