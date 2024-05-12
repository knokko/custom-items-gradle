package nl.knokko.customrecipes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestIdHelper {

    @Test
    public void testHash() {
        String a = IdHelper.createHash("a");
        String b = IdHelper.createHash("b");
        assertNotEquals(a, b);
        assertEquals(a.length(), b.length());
    }
}
