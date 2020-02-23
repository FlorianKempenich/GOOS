package test.end2end.support;

import auctionsniper.Main;
import auctionsniper.ui.MainWindow;

import static java.lang.System.arraycopy;
import static test.end2end.support.FakeAuctionServer.XMPP_HOSTNAME;

public class ApplicationRunner {
    public static final String SNIPER_ID = "sniper";
    public static final String SNIPER_XMPP_ID = "sniper@localhost/Auction";
    public static final String SNIPER_PASSWORD = "sniper";
    private AuctionSniperDriver driver;

    public void startBiddingOn(String... itemIds) {
        startTestApplicationInSeparateThread(arguments(itemIds));
        driver = new AuctionSniperDriver(1500);
        driver.hasTitle(MainWindow.APPLICATION_TITLE);
        driver.hasColumnTitles();
        for (String itemId : itemIds) {
            driver.showsSniperStatus(itemId, 0, 0, "Joining");
        }
    }

    private void startTestApplicationInSeparateThread(String[] args) {
        Thread runTestApplication = new Thread(() -> {
            try {
                Main.main(args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "Test Application");
        runTestApplication.setDaemon(true);
        runTestApplication.start();
    }

    private String[] arguments(String... itemIds) {
        String[] args = new String[itemIds.length + 3];
        args[0] = XMPP_HOSTNAME;
        args[1] = SNIPER_ID;
        args[2] = SNIPER_PASSWORD;
        arraycopy(itemIds, 0, args, 3, itemIds.length);
        return args;
    }

    public void showsSniperIsWinning(String itemId, int lastPrice) {
        driver.showsSniperStatus(
                itemId,
                lastPrice,
                lastPrice,
                "Winning"
        );
    }

    public void showsSniperHasWonAuction(String itemId, int lastPrice) {
        driver.showsSniperStatus(
                itemId,
                lastPrice,
                lastPrice,
                "Won"
        );
    }

    public void showsSniperHasLostAuction(String itemId, int lastPrice, int lastBid) {
        driver.showsSniperStatus(
                itemId,
                lastPrice,
                lastBid,
                "Lost"
        );
    }

    public void showsSniperIsBidding(String itemId, int lastPrice, int lastBid) {
        driver.showsSniperStatus(
                itemId,
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
