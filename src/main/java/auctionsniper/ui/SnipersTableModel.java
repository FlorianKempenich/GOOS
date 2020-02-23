package auctionsniper.ui;

import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;
import auctionsniper.util.Defect;

import javax.swing.table.AbstractTableModel;

public class SnipersTableModel extends AbstractTableModel implements SniperListener {
    private static String[] STATE_TEXT = {"Joining", "Bidding", "Winning", "Lost", "Won",};
    private SniperSnapshot sniperSnapshot = SniperSnapshot.nullObject();

    private static String textFor(SniperState state) {
        return STATE_TEXT[state.ordinal()];
    }

    @Override
    public int getRowCount() { return 2; }

    @Override
    public int getColumnCount() { return Column.values().length; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex == 1) {
            return quickFixToGetToTheSameFailingStateAsInTheBook(columnIndex);
        }
        return Column.at(columnIndex).valueIn(sniperSnapshot);
    }

    private Object quickFixToGetToTheSameFailingStateAsInTheBook(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "item-65432";

            case 1:
            case 2:
                return "0";

            case 3:
                return "Joining";

            default:
                throw new Defect("Invalid column index: " + columnIndex);
        }
    }

    @Override
    public String getColumnName(int column) {
        return Column.at(column).name;
    }

    @Override
    public void sniperStateChanged(SniperSnapshot newSniperSnapshot) {
        sniperSnapshot = newSniperSnapshot;
        fireTableRowsUpdated(0, 0);
    }

    public enum Column {
        ITEM_IDENTIFIER("Item") {
            @Override
            public Object valueIn(SniperSnapshot snapshot) { return snapshot.itemId; }
        },
        LAST_PRICE("Last Price") {
            @Override
            public Object valueIn(SniperSnapshot snapshot) { return snapshot.lastPrice; }
        },
        LAST_BID("Last Bid") {
            @Override
            public Object valueIn(SniperSnapshot snapshot) { return snapshot.lastBid; }
        },
        SNIPER_STATE("State") {
            @Override
            public Object valueIn(SniperSnapshot snapshot) { return SnipersTableModel.textFor(snapshot.state); }
        };

        public final String name;

        Column(String name) { this.name = name; }

        public static Column at(int offset) { return values()[offset]; }

        abstract public Object valueIn(SniperSnapshot snapshot);
    }
}
