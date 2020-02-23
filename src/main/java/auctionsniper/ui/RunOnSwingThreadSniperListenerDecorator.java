package auctionsniper.ui;

import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;

import javax.swing.*;

public class RunOnSwingThreadSniperListenerDecorator implements SniperListener {
    private final SniperListener decorated;

    public RunOnSwingThreadSniperListenerDecorator(SniperListener decorated) {this.decorated = decorated;}

    @Override
    public void sniperStateChanged(SniperSnapshot sniperSnapshot) {
        /* Why run on a special thread?

        Swing event handling code runs on a special thread known as the event dispatch thread.
        Most code that invokes Swing methods must also runs on this thread.
        This is necessary because most Swing object methods are not "thread safe": invoking them
        from multiple threads risks thread interference or memory consistency errors.

        -> https://docs.oracle.com/javase/tutorial/uiswing/concurrency/dispatch.html
         */
        SwingUtilities.invokeLater(() -> decorated.sniperStateChanged(sniperSnapshot));
    }
}
