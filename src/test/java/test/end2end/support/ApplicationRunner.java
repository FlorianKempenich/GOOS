package test.end2end.support;

import auctionsniper.Main;
import auctionsniper.ui.MainWindow;

import static test.end2end.support.FakeAuctionServer.XMPP_HOSTNAME;

public class ApplicationRunner {
    public static final String SNIPER_ID = "sniper";
    public static final String SNIPER_XMPP_ID = "sniper@localhost/Auction";
    public static final String SNIPER_PASSWORD = "sniper";
    private AuctionSniperDriver driver;

    public void startBiddingIn(final FakeAuctionServer auctionServer) {
        String itemId = auctionServer.getItemId();
        startTestApplicationInSeparateThread(auctionServer);
        driver = new AuctionSniperDriver(1500);
        driver.hasTitle(MainWindow.APPLICATION_TITLE);
        driver.hasColumnTitles();
        driver.showsSniperStatus(itemId, 0, 0, "Joining");
    }

    private void startTestApplicationInSeparateThread(FakeAuctionServer auctionServer) {
        Thread runTestApplication = new Thread(() -> {
            try {
                runApplication(auctionServer.getItemId()); //TODO: Remove and use instance var
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "Test Application");
        runTestApplication.setDaemon(true);
        runTestApplication.start();
    }

    private void runApplication(String itemId) throws Exception {
        Main.main(
                new String[]{
                        XMPP_HOSTNAME,
                        SNIPER_ID,
                        SNIPER_PASSWORD,
                        itemId
                }
        );
    }

    public void showsSniperIsWinning(FakeAuctionServer auction, int lastPrice) {
        driver.showsSniperStatus(
                auction.getItemId(),
                lastPrice,
                lastPrice,
                "Winning"
        );
    }

    public void showsSniperHasWonAuction(FakeAuctionServer auction, int lastPrice) {
        driver.showsSniperStatus(
                auction.getItemId(),
                lastPrice,
                lastPrice,
                "Won"
        );
    }

    public void showsSniperHasLostAuction(FakeAuctionServer auction, int lastPrice, int lastBid) {
        driver.showsSniperStatus(
                auction.getItemId(),
                lastPrice,
                lastBid,
                "Lost"
        );
    }

    public void showsSniperIsBidding(FakeAuctionServer auction, int lastPrice, int lastBid) {
        driver.showsSniperStatus(
                auction.getItemId(),
                lastPrice,
                lastBid,
                "Bidding"
        );
    }

    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }
}
