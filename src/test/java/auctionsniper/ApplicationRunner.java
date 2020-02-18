package auctionsniper;

import static auctionsniper.FakeAuctionServer.XMPP_HOSTNAME;

public class ApplicationRunner {
    public static final String SNIPER_ID = "sniper";
    public static final String SNIPER_PASSWORD = "sniper";
    private AuctionSniperDriver driver;

    public void startBiddingIn(final FakeAuctionServer auctionServer) {
        startTestApplicationInSeparateThread(auctionServer);
        driver = new AuctionSniperDriver(1000);
        driver.showsSniperStatus(Main.STATUS_JOINING);
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

    private void runApplication(String itemId) {
        Main.main(
                XMPP_HOSTNAME,
                SNIPER_ID,
                SNIPER_PASSWORD,
                itemId
        );
    }

    public void showsSniperHasLostAuction() {
        driver.showsSniperStatus(Main.STATUS_LOST);
    }

    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }
}
