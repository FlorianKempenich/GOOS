package test.end2end.support;

import auctionsniper.ui.MainWindow;
import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.JFrameDriver;
import com.objogate.wl.swing.driver.JTableDriver;
import com.objogate.wl.swing.driver.JTableHeaderDriver;
import com.objogate.wl.swing.gesture.GesturePerformer;
import org.hamcrest.Matcher;

import javax.swing.*;
import javax.swing.table.JTableHeader;

import static com.objogate.wl.swing.matcher.IterableComponentsMatcher.matching;
import static com.objogate.wl.swing.matcher.JLabelTextMatcher.withLabelText;

@SuppressWarnings("unchecked")
public class AuctionSniperDriver extends JFrameDriver {
    static {
        preventWarningFromWindowLicker();
    }

    public AuctionSniperDriver(int timeoutMillis) {
        super(
                new GesturePerformer(),
                JFrameDriver.topLevelFrame(
                        named(MainWindow.MAIN_WINDOW_NAME),
                        showingOnScreen()
                ),
                new AWTEventQueueProber(timeoutMillis, 100)
        );
    }

    private static void preventWarningFromWindowLicker() {
        System.setProperty("com.objogate.wl.keyboard", "Mac-GB");
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

    public void hasColumnTitles() {
        new JTableHeaderDriver(this, JTableHeader.class)
                .hasHeaders(
                        matching(
                                withLabelText("Item"),
                                withLabelText("Last Price"),
                                withLabelText("Last Bid"),
                                withLabelText("State")
                        )
                );
    }
}
