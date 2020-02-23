package auctionsniper.ui;

import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;

import javax.swing.*;

public class SniperStateDisplayer implements SniperListener {
    private final MainWindow ui;

    public SniperStateDisplayer(MainWindow ui) {
        this.ui = ui;
    }

    @Override
    public void sniperStateChanged(SniperSnapshot snapshot) {
        /* Why run on a special thread?

        Swing event handling code runs on a special thread known as the event dispatch thread.
        Most code that invokes Swing methods must also runs on this thread.
        This is necessary because most Swing object methods are not "thread safe": invoking them
        from multiple threads risks thread interference or memory consistency errors.

        -> https://docs.oracle.com/javase/tutorial/uiswing/concurrency/dispatch.html
         */
        SwingUtilities.invokeLater(() -> ui.sniperStateChanged(snapshot));
    }
}
