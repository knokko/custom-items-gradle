package nl.knokko.customitems.itemset;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestItemSet {

    @Test
    public void testManagerFields() {
        assertTrue(ItemSet.MANAGER_FIELDS.length > 10);
    }
}
