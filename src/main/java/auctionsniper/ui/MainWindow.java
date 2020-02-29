package auctionsniper.ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainWindow extends JFrame {
    public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
    public static final String SNIPERS_TABLE_NAME = "sniper status";
    public static final String APPLICATION_TITLE = "Auction Sniper";
    public static final String NEW_ITEM_ID_NAME = "New Item Id Text Field";
    public static final String JOIN_AUCTION_BUTTON_NAME = "Join Auction Button";

    private final SnipersTableModel snipers;
    private List<UserRequestListener> userRequestListeners = new ArrayList<>();

    public MainWindow(SnipersTableModel snipers) throws HeadlessException {
        super(APPLICATION_TITLE);
        this.snipers = snipers;
        setName(MAIN_WINDOW_NAME);
        fillContentPane(makeSnipersTable(), makeControls());
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void fillContentPane(JTable snipersTable, JPanel controls) {
        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(controls, BorderLayout.NORTH);
        contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
    }

    private JTable makeSnipersTable() {
        final JTable snipersTable = new JTable(snipers);
        snipersTable.setName(SNIPERS_TABLE_NAME);
        return snipersTable;
    }

    private JPanel makeControls() {
        JPanel controls = new JPanel(new FlowLayout());

        final JTextField newItemIdField = new JTextField();
        newItemIdField.setColumns(25);
        newItemIdField.setName(NEW_ITEM_ID_NAME);

        JButton joinAuctionButton = new JButton("Join Auction");
        joinAuctionButton.setName(JOIN_AUCTION_BUTTON_NAME);
        joinAuctionButton.addActionListener(e ->
                this.userRequestListeners.forEach(
                        listener -> listener.joinAuction(newItemIdField.getText())
                )
        );

        controls.add(newItemIdField);
        controls.add(joinAuctionButton);
        return controls;
    }

    public void addUserRequestListener(UserRequestListener userRequestListener) {
        this.userRequestListeners.add(userRequestListener);
    }
}
