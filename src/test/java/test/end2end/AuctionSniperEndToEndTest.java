package test.end2end;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import test.end2end.support.ApplicationRunner;
import test.end2end.support.FakeAuctionServer;

import static test.end2end.support.ApplicationRunner.SNIPER_XMPP_ID;

@Tag("E2E")
public class AuctionSniperEndToEndTest {
    private static final String ITEM_ID_1 = "item-54321";
    private static final String ITEM_ID_2 = "item-65432";
    private final FakeAuctionServer auctionServer1 = new FakeAuctionServer(ITEM_ID_1);
    private final FakeAuctionServer auctionServer2 = new FakeAuctionServer(ITEM_ID_2);
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
        auctionServer1.startSellingItem();
        application.startBiddingOn(ITEM_ID_1);
        auctionServer1.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID);
        auctionServer1.announceClosed();
        application.showsSniperHasLostAuction(ITEM_ID_1, 0, 0);
    }

    @Test
    void sniperMakesAHigherBiDButLoses() throws Exception {
        auctionServer1.startSellingItem();
        application.startBiddingOn(ITEM_ID_1);
        auctionServer1.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID);

        auctionServer1.reportPrice(1000, 98, "other bidder");
        application.showsSniperIsBidding(ITEM_ID_1, 1000, 1098);

        auctionServer1.hasReceivedBidFrom(SNIPER_XMPP_ID, 1098);

        auctionServer1.announceClosed();
        application.showsSniperHasLostAuction(ITEM_ID_1, 1000, 1098);
    }

    @Test
    void sniperWinsAnAuctionByBiddingHigher() throws Exception {
        auctionServer1.startSellingItem();
        application.startBiddingOn(ITEM_ID_1);
        auctionServer1.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID);

        auctionServer1.reportPrice(1000, 98, "other bidder");
        application.showsSniperIsBidding(ITEM_ID_1, 1000, 1098);

        auctionServer1.hasReceivedBidFrom(SNIPER_XMPP_ID, 1098);

        auctionServer1.reportPrice(1098, 103, SNIPER_XMPP_ID);
        application.showsSniperIsWinning(ITEM_ID_1, 1098);

        auctionServer1.announceClosed();
        application.showsSniperHasWonAuction(ITEM_ID_1, 1098);
    }

    @Test
    void sniperBidsForMultipleItems() throws Exception {
        auctionServer1.startSellingItem();
        auctionServer2.startSellingItem();

        application.startBiddingOn(ITEM_ID_1, ITEM_ID_2);
        auctionServer1.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID);
        auctionServer2.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID);

        auctionServer1.reportPrice(1000, 98, "other bidder");
        auctionServer1.hasReceivedBidFrom(SNIPER_XMPP_ID, 1098);

        auctionServer2.reportPrice(500, 21, "other bidder");
        auctionServer2.hasReceivedBidFrom(SNIPER_XMPP_ID, 521);

        auctionServer1.reportPrice(1098, 101, SNIPER_XMPP_ID);
        auctionServer2.reportPrice(521, 35, SNIPER_XMPP_ID);

        application.showsSniperIsWinning(ITEM_ID_1, 1098);
        application.showsSniperIsWinning(ITEM_ID_2, 521);

        auctionServer1.announceClosed();
        auctionServer2.announceClosed();

        application.showsSniperHasWonAuction(ITEM_ID_1, 1098);
        application.showsSniperHasWonAuction(ITEM_ID_2, 521);
    }

    @AfterEach
    void stopAuction() {
        auctionServer1.stop();
        auctionServer2.stop();
    }

    @AfterEach
    void stopApplication() {
        application.stop();
    }
}
