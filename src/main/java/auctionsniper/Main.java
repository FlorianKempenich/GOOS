package auctionsniper;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class Main {
    public static final String STATUS_JOINING = "Joining";
    public static final String STATUS_LOST = "Lost";
    private MainWindow ui;

    public Main() throws Exception {
        startUserInterface();
    }

    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait(() -> ui = new MainWindow());
    }

    public static void main(String xmppHostname, String sniperId, String sniperPassword, String itemId) throws Exception {
        Main main = new Main();
    }

    public static class MainWindow extends JFrame {
        public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
        public static final String SNIPER_STATUS_NAME = "sniper status";
        private final JLabel sniperStatus = createLabel(STATUS_JOINING);

        public MainWindow() throws HeadlessException {
            super("Auction Sniper");
            setName(MAIN_WINDOW_NAME);
            add(sniperStatus);
            pack();
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setVisible(true);
        }

        private JLabel createLabel(String initialText) {
            JLabel result = new JLabel(initialText);
            result.setName(SNIPER_STATUS_NAME);
            result.setBorder(new LineBorder(Color.BLACK));
            return result;
        }
    }
}
