package nl.knokko.customitems.plugin.tasks.updater;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static nl.knokko.customitems.plugin.tasks.updater.LoreUpdater.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestLoreUpdater {

    @Test
    public void testParseDurabilityLine() {
        assertNull(parseDurabilityLine(0, ""));
        assertNull(parseDurabilityLine(1, ""));
        assertNull(parseDurabilityLine(2, "test1234"));
        assertNull(parseDurabilityLine(3, "test 1 / 2;"));
        assertNull(parseDurabilityLine(4, "check 1/ 2"));

        assertEquals(
                new LoreUpdater.DurabilityLine(0, "Durability:", 70, 80),
                parseDurabilityLine(0, "Durability: 70 / 80")
        );
        assertEquals(
                new LoreUpdater.DurabilityLine(1, "Test durability", 1, 8),
                parseDurabilityLine(1, "Test durability 1 / 8")
        );
        assertEquals(
                new LoreUpdater.DurabilityLine(2, "Complex 1 / 2 ", 3, 4),
                parseDurabilityLine(2, "Complex 1 / 2  3 / 4")
        );
    }

    private static List<String> lore(String... lines) {
        List<String> result = new ArrayList<>(lines.length);
        Collections.addAll(result, lines);
        return result;
    }

    @Test
    public void testFindDurabilityLineIndex() {
        assertNull(findDurabilityLineIndex(new ArrayList<>(), "Durability", 1L, 2L));
        assertNull(findDurabilityLineIndex(lore(
                ""
        ), "Durability", 3L, 4L));
        assertNull(findDurabilityLineIndex(lore(
                "test1234",
                "",
                "hello"
        ), "Durability", 5L, 6L));

        assertEquals((Integer) 2, findDurabilityLineIndex(lore(
                "Just some random lore",
                "",
                "Durability 5 / 6",
                ""
        ), "Durability", 7L, 8L));

        assertEquals((Integer) 3, findDurabilityLineIndex(lore(
                "This case contains multiple candidates, but only 1 has the right prefix",
                "Energy 5 / 6",
                "",
                "Durability 8 / 9",
                "",
                "Mana 123 / 123"
        ), "Durability", 5L, 6L));

        assertEquals((Integer) 0, findDurabilityLineIndex(lore(
                "Durability: 100 / 200",
                "This case contains multiple candidates, but only 1 is an exact match",
                "Durability: 100 / 2000",
                "Energy: 100 / 200"
        ), "Durability:", 100L, 200L));

        assertEquals((Integer) 2, findDurabilityLineIndex(lore(
                "There is no exact match, so the best candidate should be picked",
                "Durability: 150 / 160",
                "Durability: 140 / 170",
                "Energy: 150 / 170"
        ), "Durability:", 150L, 170L));
    }

    @Test
    public void testFindLoreFragmentIndex() {
        assertNull(findLoreFragmentIndex(lore(
                "Nothing here", "really"
        ), lore("Nothing")));
        assertNull(findLoreFragmentIndex(lore(), lore("test")));
        assertEquals((Integer) 0, findLoreFragmentIndex(lore(), lore()));
        assertEquals((Integer) 0, findLoreFragmentIndex(lore("The", "entire", "lore"), lore("The", "entire", "lore")));
        assertNull(findLoreFragmentIndex(lore("A", "Tiny", "difference"), lore("A", "tiny", "difference")));
        assertEquals((Integer) 2, findLoreFragmentIndex(lore("last", "last", "last"), lore("last")));

        assertEquals((Integer) 2, findLoreFragmentIndex(lore(
                "Durability: 100 / 100",
                "",
                "This is a rather common case",
                "with some custom enchantments",
                "",
                "Enchantment 1",
                "Enchantment 2"
        ), lore("This is a rather common case", "with some custom enchantments")));

        assertNull(findLoreFragmentIndex(lore(
                "Mostly good",
                "but missing a line",
                "so no exact match"
        ), lore("Mostly good", "so no exact match")));

        assertEquals((Integer) 2, findLoreFragmentIndex(lore("Durability 20 / 25", ""), lore()));
    }

    @Test
    public void testReplaceLoreFragment() {
        assertEquals(lore(
                "Appended", "the new lore"
        ), replaceLoreFragment(lore(), 0, 0, lore(
                "Appended", "the new lore"
        )));

        assertEquals(lore(
                "Energy 100 / 200",
                "",
                "This is the",
                "new base lore",
                "",
                "Enchantment 1"
        ), replaceLoreFragment(lore(
                "Energy 100 / 200",
                "",
                "This is the",
                "old base lore",
                "",
                "Enchantment 1"
        ), 2, 2, lore("This is the", "new base lore")));

        assertEquals(lore(
                "Prepend the base lore",
                "",
                "Durability 100 / 100"
        ), replaceLoreFragment(
                lore("", "Durability 100 / 100"), 0, 0, lore("Prepend the base lore")
        ));

        assertEquals(lore(
                "Durability 100 / 100",
                "",
                "Append the base lore"
        ), replaceLoreFragment(
                lore("Durability 100 / 100", ""), 2, 0, lore("Append the base lore")
        ));

        assertEquals(lore(
                "Enchantments...",
                "The new lore",
                "is slightly",
                "longer",
                "Durability 500 / 500"
        ), replaceLoreFragment(lore(
                "Enchantments...",
                "The old lore is short",
                "Durability 500 / 500"
        ), 1, 1, lore("The new lore", "is slightly", "longer")));

        assertEquals(lore(
                "The new lore is shorter"
        ), replaceLoreFragment(lore(
                "The old lore",
                "is rather long",
                "and boring"
        ), 0, 3, lore("The new lore is shorter")));
    }
}
