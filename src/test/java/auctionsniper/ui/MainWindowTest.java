package auctionsniper.ui;

import com.objogate.wl.swing.probe.ValueMatcherProbe;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import test.end2end.support.AuctionSniperDriver;

import static org.hamcrest.CoreMatchers.equalTo;

@Tag("Integration")
class MainWindowTest {
    private final SnipersTableModel tableModel = new SnipersTableModel();
    private final MainWindow mainWindow = new MainWindow(tableModel);
    private AuctionSniperDriver driver = new AuctionSniperDriver(100);

    @AfterEach
    void closeDriver() { driver.dispose(); }

    @Test
    void makesUserRequestWhenJoinButtonClicked() {
        final ValueMatcherProbe<String> buttonProbe =
                new ValueMatcherProbe<>(equalTo("an item-id"), "join request");

        //noinspection Convert2Lambda,Anonymous2MethodRef - Keep as is for clarity of intent in the test
        mainWindow.addUserRequestListener(
                new UserRequestListener() {
                    public void joinAuction(String itemId) {
                        buttonProbe.setReceivedValue(itemId);
                    }
                }
        );

        driver.startBiddingFor("an item-id");
        driver.check(buttonProbe);
    }

    @Test
    void supportsMultipleListeners() {
        final ValueMatcherProbe<String> buttonProbe1 =
                new ValueMatcherProbe<>(equalTo("an item-id"), "join request");
        final ValueMatcherProbe<String> buttonProbe2 =
                new ValueMatcherProbe<>(equalTo("an item-id"), "join request");

        mainWindow.addUserRequestListener(buttonProbe1::setReceivedValue);
        mainWindow.addUserRequestListener(buttonProbe2::setReceivedValue);

        driver.startBiddingFor("an item-id");
        driver.check(buttonProbe1);
        driver.check(buttonProbe2);
    }

    @Test
    void clearsNewItemIdFieldWhenClickingOnJoinAuction() {
        driver.enterItemId("some item-id");
        driver.clickJoinButton();
        driver.clearsNewItemIdField();
    }
}