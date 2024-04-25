package nl.knokko.customrecipes.crafting;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static nl.knokko.customrecipes.crafting.ShapelessMatcher.match;
import static org.junit.jupiter.api.Assertions.*;

public class TestShapelessMatcher {

    @Test
    public void testMatchOneToOne() {
        boolean[][] accepts = { { true }};
        assertArrayEquals(new int[1], match(accepts));
    }

    @Test
    public void testMatchOneImpossibleSingle() {
        boolean[][] accepts = { { false }};
        assertNull(match(accepts));
    }

    @Test
    public void testMatchTwoToOne() {
        boolean[][] accepts = {
                { true, false },
                { true, false }
        };
        assertNull(match(accepts));
    }

    @Test
    public void testMatchOneToTwo() {
        boolean[][] accepts = {
                { true, true },
                { false, false }
        };
        assertNull(match(accepts));
    }

    @Test
    public void testMatchTwoToTwo() {
        boolean[][] accepts = {
                { true, true },
                { true, true }
        };
        int[] result = match(accepts);
        assertEquals(2, result.length);
        assertTrue(result[0] == 0 || result[1] == 1);
        assertEquals(1 - result[0], result[1]);
    }

    @Test
    public void testAsymmetricFailure() {
        boolean[][] accepts = {
                { false, false, true },
                { true, true, true },
                { false, false, true }
        };
        assertNull(match(accepts));
    }

    @Test
    public void testWildcard9() {
        boolean[][] accepts = new boolean[9][9];
        for (int index = 0; index < 9; index++) Arrays.fill(accepts[index], true);

        int[] result = match(accepts);
        for (int index = 0; index < 9; index++) {
            int rememberIndex = index;
            assertTrue(Arrays.stream(result).anyMatch(candidate -> rememberIndex == candidate));
        }
    }

    @Test
    public void testSimpleMatch9() {
        boolean[][] accepts = new boolean[9][9];
        accepts[0][5] = true;
        accepts[1][2] = true;
        accepts[2][1] = true;
        accepts[3][6] = true;
        accepts[4][0] = true;
        accepts[5][7] = true;
        accepts[6][8] = true;
        accepts[7][4] = true;
        accepts[8][3] = true;

        int[] solution = { 5, 2, 1, 6, 0, 7, 8, 4, 3 };
        assertArrayEquals(solution, match(accepts));
    }

    private boolean[][] deepCopy(boolean[][] original) {
        boolean[][] copy = new boolean[original.length][original[0].length];
        for (int index = 0; index < copy.length; index++) copy[index] = Arrays.copyOf(original[index], copy.length);
        return copy;
    }

    @Test
    public void testBarelyPossibleMatch() {
        boolean[][] accepts = new boolean[9][9];
        accepts[0][1] = true;
        accepts[0][3] = true;
        accepts[0][5] = true; // solution
        accepts[0][6] = true;
        accepts[0][7] = true;
        accepts[1][2] = true; // solution
        accepts[1][5] = true;
        accepts[1][6] = true;
        accepts[1][8] = true;
        accepts[2][1] = true; // solution
        accepts[2][7] = true;
        accepts[3][0] = true;
        accepts[3][4] = true;
        accepts[3][6] = true; // solution
        accepts[4][0] = true; // solution
        accepts[5][3] = true;
        accepts[5][7] = true; // solution
        accepts[5][8] = true;
        accepts[6][0] = true;
        accepts[6][8] = true; // solution
        accepts[7][0] = true;
        accepts[7][4] = true;
        accepts[7][7] = true; // solution
        accepts[7][8] = true;
        accepts[8][0] = true;
        accepts[8][3] = true; // solution
        accepts[8][8] = true;

        int[] solution = { 5, 2, 1, 6, 0, 7, 8, 4, 3 };
        for (int index = 0; index < solution.length; index++) {
            assertTrue(accepts[index][solution[index]]);

            accepts[index][solution[index]] = false;
            assertNull(match(deepCopy(accepts)));
            accepts[index][solution[index]] = true;

            assertArrayEquals(solution, match(deepCopy(accepts)));
        }
    }

    @Test
    public void testBigImpossibleMatch() {
        boolean[][] accepts = new boolean[9][9];
        for (int source = 0; source < 9; source++) {
            Arrays.fill(accepts[source], true);
            if (source != 4 && source != 6) {
                accepts[source][4] = false;
                accepts[source][5] = false;
                accepts[source][6] = false;
            }
        }

        assertNull(match(accepts));
    }
}
