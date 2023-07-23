package nl.knokko.customitems.plugin.util;

import org.junit.Test;

import java.util.Arrays;

import static nl.knokko.customitems.plugin.util.CommandHelper.escapeArgs;
import static org.junit.Assert.assertArrayEquals;

public class TestCommandHelper {

    @Test
    public void testEscapeArgsNothing() {
        String[][] testCases = {
                {},
                { "" },
                { "kci" },
                { "kci", "test" }
        };
        for (String[] testCase : testCases) {
            assertArrayEquals(testCase, escapeArgs(testCase));
        }
    }

    @Test
    public void testEscapeArgsShort() {
        assertArrayEquals(new String[] { "player name" }, escapeArgs("'player", "name'"));
        assertArrayEquals(new String[] { "ignore", "the first" }, escapeArgs("ignore", "'the", "first'"));
        assertArrayEquals(new String[] { "ignore the", "last" }, escapeArgs("'ignore", "the'", "last"));
        assertArrayEquals(new String[] { "ignore", "first and", "last" }, escapeArgs("ignore", "'first", "and'", "last"));

        assertArrayEquals(new String[] {
                "this case", "is", "a", "bit more", "complicated"
        }, escapeArgs("'this", "case'", "is", "a", "'bit", "more'", "complicated"));
    }

    @Test
    public void testEscapeArgsLong() {
        assertArrayEquals(new String[] { "1 long, very long, arg" }, escapeArgs("'1", "long,", "very", "long,", "arg'"));
        assertArrayEquals(new String[] { "lets", "exclude the first arg" }, escapeArgs("lets", "'exclude", "the", "first", "arg'"));
        assertArrayEquals(new String[] { "or the last", "arg" }, escapeArgs("'or", "the", "last'", "arg"));
    }

    @Test
    public void testInvalidSyntax() {
        String[][] testCases = {
                { "''" },
                { "'", "test'", "test'" },
                { "let's", "'make", "this", "a", "bit", "longer'" },
                { "this", "case", "is", "'just'", "stupid" }
        };
        for (String[] testCase : testCases) {
            assertArrayEquals(testCase, escapeArgs(testCase));
        }
    }
}
