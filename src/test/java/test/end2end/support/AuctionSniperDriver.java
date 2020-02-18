package test.end2end.support;

import auctionsniper.Main;
import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.JFrameDriver;
import com.objogate.wl.swing.driver.JLabelDriver;
import com.objogate.wl.swing.gesture.GesturePerformer;

import static org.hamcrest.CoreMatchers.equalTo;

public class AuctionSniperDriver extends JFrameDriver {
    public AuctionSniperDriver(int timeoutMillis) {
        //noinspection unchecked
        super(
                new GesturePerformer(),
                JFrameDriver.topLevelFrame(
                        named(Main.MainWindow.MAIN_WINDOW_NAME),
                        showingOnScreen()
                ),
                new AWTEventQueueProber(timeoutMillis, 100)
        );
    }

    public void showsSniperStatus(String statusText) {
        // noinspection unchecked
        new JLabelDriver(
                this,
                named(Main.MainWindow.SNIPER_STATUS_NAME)
        ).hasText(equalTo(statusText));
    }
}
