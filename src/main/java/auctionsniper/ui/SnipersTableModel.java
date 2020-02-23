package auctionsniper.ui;

import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;
import auctionsniper.util.Defect;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class SnipersTableModel extends AbstractTableModel implements SniperListener {
    private static String[] STATE_TEXT = {"Joining", "Bidding", "Winning", "Lost", "Won",};
    private SniperSnapshot sniperSnapshot = SniperSnapshot.nullObject();
    private List<SniperSnapshot> snapshots = new ArrayList<>();

    private static String textFor(SniperState state) {
        return STATE_TEXT[state.ordinal()];
    }

    @Override
    public int getRowCount() { return snapshots.size(); }

    @Override
    public int getColumnCount() { return Column.values().length; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        SniperSnapshot snapshotForRow = snapshots.get(rowIndex);
        return Column.at(columnIndex).valueIn(snapshotForRow);
    }

    public void addSniper(SniperSnapshot snapshot) {
        snapshots.add(snapshot);
        int rowInserted = snapshots.size() - 1;
        fireTableRowsInserted(rowInserted, rowInserted);
    }

    @Override
    public String getColumnName(int column) {
        return Column.at(column).name;
    }

    @Override
    public void sniperStateChanged(SniperSnapshot newSniperSnapshot) {
        int snapshotRow = rowWithSameItemIdAs(newSniperSnapshot);
        snapshots.set(snapshotRow, newSniperSnapshot);
        fireTableRowsUpdated(snapshotRow, snapshotRow);
    }

    private int rowWithSameItemIdAs(SniperSnapshot newSniperSnapshot) {
        String itemId = newSniperSnapshot.itemId;
        for (int i = 0; i < snapshots.size(); i++) {
            SniperSnapshot snapshot = snapshots.get(i);
            if (snapshot.isForSameItemAs(newSniperSnapshot)) {
                return i;
            }
        }

        throw new Defect("Row for item '" + itemId + "' was not found. Probably it was not added to the model. To do so, use method 'addSniper'");
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
