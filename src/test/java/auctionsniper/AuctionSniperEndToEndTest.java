package auctionsniper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class AuctionSniperEndToEndTest {
    private final FakeAuctionServer auctionServer = new FakeAuctionServer("item-54321");
    private final ApplicationRunner application = new ApplicationRunner();

    public AuctionSniperEndToEndTest() throws Exception { }

    @Test
    void sniperJoinsAuctionUntilAuctionCloses() throws Exception {
        auctionServer.startSellingItem();
        application.startBiddingIn(auctionServer);
        auctionServer.hasReceivedJoinRequestFromSniper();
        auctionServer.announceClosed();
        application.showsSniperHasLostAuction();
    }
    /* Notes
    Before starting the auctionServer, 3 accounts on the XMPP server are created:
    - USERNAME:PASSWORD
    - sniper@localhost:sniper
    - auction-item-54321@localhost:auction
    - auction-item-65432@localhost:auction
     */

    @AfterEach
    void stopAuction() {
        auctionServer.stop();
    }

    @AfterEach
    void stopApplication() {
        application.stop();
    }
}
