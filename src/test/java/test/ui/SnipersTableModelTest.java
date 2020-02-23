package test.ui;

import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;
import auctionsniper.ui.SnipersTableModel;
import auctionsniper.ui.SnipersTableModel.Column;
import auctionsniper.util.Defect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import static javax.swing.event.TableModelEvent.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SnipersTableModelTest {
    @Mock TableModelListener listener;
    @InjectMocks SnipersTableModel model;

    @BeforeEach
    void setUp() {
        model.addTableModelListener(listener);
    }

    @Test
    void hasEnoughColumns() {
        assertThat(model.getColumnCount(), equalTo(Column.values().length));
    }

    @Test
    void itNotifiesListenerWhenSniperStatusChanged() {
        String itemId = "item-123456";
        model.addSniper(SniperSnapshot.joining("some-other-item-1"));
        model.addSniper(SniperSnapshot.joining(itemId));
        model.addSniper(SniperSnapshot.joining("some-other-item-2"));

        model.sniperStateChanged(new SniperSnapshot(itemId, 5555, 6666, SniperState.JOINING));

        verify(listener).tableChanged(argThat(matchesRowUpdatedEvent(1)));
    }

    private ArgumentMatcher<TableModelEvent> matchesRowUpdatedEvent(int rowIndex) {
        return tableModelEvent ->
                tableModelEvent.getType() == UPDATE &&
                        tableModelEvent.getFirstRow() == rowIndex &&
                        tableModelEvent.getLastRow() == rowIndex &&
                        tableModelEvent.getColumn() == ALL_COLUMNS;
    }

    @Test
    void itUpdatesTheModelWhenSniperStatusChanged() {
        String itemId = "item-123456";
        model.addSniper(SniperSnapshot.joining("some-other-item-1"));
        model.addSniper(SniperSnapshot.joining(itemId));
        model.addSniper(SniperSnapshot.joining("some-other-item-2"));

        SniperSnapshot newSnapshot = new SniperSnapshot(itemId, 5555, 6666, SniperState.BIDDING);
        model.sniperStateChanged(newSnapshot);

        assertRowMatchesSnapshot(1, newSnapshot);
    }

    private void assertRowMatchesSnapshot(int rowIndex, SniperSnapshot snapshot) {
        for (Column column : Column.values()) {
            assertEquals(column.valueIn(snapshot), model.getValueAt(rowIndex, column.ordinal()));
        }
    }

    @Test
    void itThrowsExceptionIfTryingToUpdateSnapshotForASniperThatWasNotAdded() {
        String itemId = "item-id";
        model.addSniper(SniperSnapshot.joining("not-the-expected-item"));

        Defect error = assertThrows(
                Defect.class,
                () -> model.sniperStateChanged(new SniperSnapshot(itemId, 1, 1, SniperState.BIDDING))
        );
        assertThat(error.getMessage(), containsString("was not added"));
    }

    @ParameterizedTest(name = "Column {0}")
    @EnumSource(Column.class)
    void itSetsUpColumnHeaderBasedOnColumnEnumNames(Column column) {
        assertEquals(column.name, model.getColumnName(column.ordinal()));
    }

    @Test
    void itNotifiesListenersWhenAddingASniper() {
        SniperSnapshot joining = SniperSnapshot.joining("item123");
        model.addSniper(joining);
        verify(listener).tableChanged(argThat(matchesRowInsertedEvent(0)));
    }

    private ArgumentMatcher<TableModelEvent> matchesRowInsertedEvent(int rowIndex) {
        return tableModelEvent ->
                tableModelEvent.getType() == INSERT &&
                        tableModelEvent.getFirstRow() == rowIndex &&
                        tableModelEvent.getLastRow() == rowIndex &&
                        tableModelEvent.getColumn() == ALL_COLUMNS;
    }

    @Test
    void itNotifiesListenersWhenAddingAMultipleSnipers() {
        model.addSniper(SniperSnapshot.joining("item0"));
        model.addSniper(SniperSnapshot.joining("item1"));
        model.addSniper(SniperSnapshot.joining("item2"));

        model.addSniper(SniperSnapshot.joining("item3"));

        verify(listener).tableChanged(argThat(matchesRowInsertedEvent(3)));
    }

    @Test
    void itReportsAsManyRowsAsManySnipersWereAdded() {
        model.addSniper(SniperSnapshot.joining("item0"));
        model.addSniper(SniperSnapshot.joining("item1"));
        model.addSniper(SniperSnapshot.joining("item2"));

        assertEquals(3, model.getRowCount());
    }

    @Test
    void itHoldsSnipersInAdditionOrder() {
        model.addSniper(SniperSnapshot.joining("item0"));
        model.addSniper(SniperSnapshot.joining("item1"));

        assertEquals("item0", cellValue(0, Column.ITEM_IDENTIFIER));
        assertEquals("item1", cellValue(1, Column.ITEM_IDENTIFIER));
    }

    private String cellValue(int row, Column column) {
        return (String) model.getValueAt(row, column.ordinal());
    }
}