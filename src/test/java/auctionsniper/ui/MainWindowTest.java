package auctionsniper.ui;

import com.objogate.wl.swing.probe.ValueMatcherProbe;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import test.end2end.support.AuctionSniperDriver;

import static org.hamcrest.CoreMatchers.equalTo;

@Tag("Integration")
class MainWindowTest {
    private final SnipersTableModel tableModel = new SnipersTableModel();
    private final MainWindow mainWindow = new MainWindow(tableModel);
    private final AuctionSniperDriver driver = new AuctionSniperDriver(100);

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
}