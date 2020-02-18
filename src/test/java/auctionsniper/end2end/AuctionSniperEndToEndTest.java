package auctionsniper.end2end;

import auctionsniper.end2end.support.ApplicationRunner;
import auctionsniper.end2end.support.FakeAuctionServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static auctionsniper.end2end.support.ApplicationRunner.SNIPER_XMPP_ID;

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
    void sniperJoinsAuctionUntilAuctionCloses() throws Exception {
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
        application.showsSniperIsBidding();

        auctionServer.hasReceivedBidFrom(SNIPER_XMPP_ID, 1098);

        auctionServer.announceClosed();
        application.showsSniperHasLostAuction();
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
