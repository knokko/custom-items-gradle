package nl.knokko.customitems.util;

import nl.knokko.customitems.editor.util.ItemSetBackups;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestItemSetBackups {

    private void checkGetAll(Collection<ItemSetBackups> actual, ItemSetBackups... expected) {
        Set<ItemSetBackups> expectedSet = new HashSet<>(expected.length);
        Collections.addAll(expectedSet, expected);
        assertEquals(expectedSet, new HashSet<>(actual));
    }

    private List<Long> listOf(Long... values) {
        List<Long> list = new ArrayList<>(values.length);
        Collections.addAll(list, values);
        return list;
    }

    @Test
    public void testGetAll() {
        assertTrue(ItemSetBackups.getAll(new String[0]).isEmpty());

        checkGetAll(ItemSetBackups.getAll(new String[] {
                "ignore.txt",
                "test.cisb",
                "single 123.cisb",
                "no number.cisb",
                "triple 762.cisb",
                "no-extension",
                "triple 12.cisb",
                "triple 52937.cisb"
        }), new ItemSetBackups("single", listOf(123L)), new ItemSetBackups("triple", listOf(
                762L, 12L, 52937L
        )));
    }

    @Test
    public void testGetSaveTimes() {
        assertTrue(new ItemSetBackups("empty", new ArrayList<>()).getSaveTimes().isEmpty());

        assertEquals(listOf(400L, 300L, 200L, 100L), new ItemSetBackups("use4", listOf(
                200L, 400L, 300L, 100L
        )).getSaveTimes());
    }

    @Test
    public void testCleanOldBackups() {
        assertTrue(new ItemSetBackups("empty", new ArrayList<>()).cleanOldBackups(1234L).isEmpty());

        long hourDuration = 1000 * 3600;
        long dayDuration = hourDuration * 24;
        long monthDuration = 30 * dayDuration;

        long dummyTime = 20 * monthDuration;

        assertEquals(listOf(
                // The oldest 2 saves on day 2 should be removed
                dummyTime - dayDuration - hourDuration,
                dummyTime - dayDuration - 20 * hourDuration,

                // The oldest save on day 27 should be removed
                dummyTime - 26 * dayDuration - 18 * hourDuration,

                // The oldest 2 saves of month 2 should be removed
                dummyTime - monthDuration - 7 * hourDuration,
                dummyTime - monthDuration - 3 * dayDuration,

                // The oldest 4 saves of month 6 should be removed
                dummyTime - 5 * monthDuration - 3 * dayDuration - hourDuration,
                dummyTime - 5 * monthDuration - 3 * dayDuration - 2 * hourDuration,
                dummyTime - 5 * monthDuration - 5 * dayDuration - hourDuration,
                dummyTime - 5 * monthDuration - 5 * dayDuration - 4 * hourDuration
        ), new ItemSetBackups("the complex test case", listOf(
                // Future saves and present saves should never be removed
                dummyTime + 10,
                dummyTime,

                // Saves less than 1 day old should never be removed
                dummyTime - hourDuration,
                dummyTime - 10 * hourDuration,
                dummyTime - 23 * hourDuration,

                // The oldest 2 saves on day 2 should be removed
                dummyTime - dayDuration,
                dummyTime - dayDuration - hourDuration,
                dummyTime - dayDuration - 20 * hourDuration,

                // We shouldn't remove the only save on day 15
                dummyTime - 14 * dayDuration - 12 * hourDuration,

                // The oldest save on day 27 should be removed
                dummyTime - 26 * dayDuration - 10 * hourDuration,
                dummyTime - 26 * dayDuration - 18 * hourDuration,

                // Only the newest save in month 2 should be spared
                dummyTime - monthDuration,
                dummyTime - monthDuration - 7 * hourDuration,
                dummyTime - monthDuration - 3 * dayDuration,

                // The only save of month 3 should be spared
                dummyTime - 2 * monthDuration - 2 * dayDuration,

                // Only the newest save in month 6 should be spared
                dummyTime - 5 * monthDuration - 3 * dayDuration,
                dummyTime - 5 * monthDuration - 3 * dayDuration - hourDuration,
                dummyTime - 5 * monthDuration - 3 * dayDuration - 2 * hourDuration,
                dummyTime - 5 * monthDuration - 5 * dayDuration - hourDuration,
                dummyTime - 5 * monthDuration - 5 * dayDuration - 4 * hourDuration
        )).cleanOldBackups(dummyTime));
    }
}
