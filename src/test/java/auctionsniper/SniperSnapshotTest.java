package auctionsniper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SniperSnapshotTest {

    @Test
    void isForSameItemWhenSameItemId() {
        SniperSnapshot snapshot = SniperSnapshot.joining("id1");
        SniperSnapshot snapshotForSameItem = SniperSnapshot.joining("id1");
        assertTrue(snapshot.isForSameItemAs(snapshotForSameItem));
    }

    @Test
    void isNotForSameItemWhenNotSameItemId() {
        SniperSnapshot snapshot = SniperSnapshot.joining("id1");
        SniperSnapshot snapshotForDifferentItem = SniperSnapshot.joining("different-item");
        assertFalse(snapshot.isForSameItemAs(snapshotForDifferentItem));
    }
}