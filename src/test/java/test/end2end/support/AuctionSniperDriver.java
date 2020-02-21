package test.end2end.support;

import auctionsniper.Main;
import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.JFrameDriver;
import com.objogate.wl.swing.driver.JTableDriver;
import com.objogate.wl.swing.gesture.GesturePerformer;
import org.hamcrest.Matcher;

import javax.swing.*;

import static com.objogate.wl.swing.matcher.IterableComponentsMatcher.matching;
import static com.objogate.wl.swing.matcher.JLabelTextMatcher.withLabelText;
import static org.hamcrest.Matchers.any;

@SuppressWarnings("unchecked")
public class AuctionSniperDriver extends JFrameDriver {
    public AuctionSniperDriver(int timeoutMillis) {
        super(
                new GesturePerformer(),
                JFrameDriver.topLevelFrame(
                        named(Main.MainWindow.MAIN_WINDOW_NAME),
                        showingOnScreen()
                ),
                new AWTEventQueueProber(timeoutMillis, 100)
        );
    }

    public void showsSniperStatus(String itemId, Integer lastPrice, Integer lastBid, String statusText) {
        tableHasRowMatching(
                withLabelText(itemId),
                withLabelText(lastPrice.toString()),
                withLabelText(lastBid.toString()),
                withLabelText(statusText)
        );
    }

    private void tableHasRowMatching(Matcher<? extends JComponent> col1,
                                     Matcher<? extends JComponent> col2,
                                     Matcher<? extends JComponent> col3,
                                     Matcher<? extends JComponent> col4) {
        new JTableDriver(this).hasRow(matching(col1, col2, col3, col4));
    }

    public void showsSniperStatus(String itemId, String statusText) {
        tableHasRowMatching(
                withLabelText(itemId),
                any(JLabel.class),
                any(JLabel.class),
                withLabelText(statusText)
        );
    }

    @Deprecated
    public void showsSniperStatus(String statusText) {
        tableHasRowMatching(
                any(JLabel.class),
                any(JLabel.class),
                any(JLabel.class),
                withLabelText(statusText)
        );
    }
}
