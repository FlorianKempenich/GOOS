package test.end2end.support;

import auctionsniper.Main;

import static auctionsniper.Main.MainWindow.*;
import static test.end2end.support.FakeAuctionServer.XMPP_HOSTNAME;

public class ApplicationRunner {
    public static final String SNIPER_ID = "sniper";
    public static final String SNIPER_XMPP_ID = "sniper@localhost/Auction";
    public static final String SNIPER_PASSWORD = "sniper";
    private AuctionSniperDriver driver;

    public void startBiddingIn(final FakeAuctionServer auctionServer) {
        startTestApplicationInSeparateThread(auctionServer);
        driver = new AuctionSniperDriver(1000);
        driver.showsSniperStatus(STATUS_JOINING);
    }

    private void startTestApplicationInSeparateThread(FakeAuctionServer auctionServer) {
        Thread runTestApplication = new Thread(() -> {
            try {
                runApplication(auctionServer.getItemId());
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

    public void showsSniperIsWinning() {
        driver.showsSniperStatus(STATUS_WINNING);
    }

    public void showsSniperHasWonAuction() {
        driver.showsSniperStatus(STATUS_WON);
    }

    public void showsSniperHasLostAuction() {
        driver.showsSniperStatus(STATUS_LOST);
    }

    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }

    public void showsSniperIsBidding() {
        driver.showsSniperStatus(STATUS_BIDDING);
    }
}
