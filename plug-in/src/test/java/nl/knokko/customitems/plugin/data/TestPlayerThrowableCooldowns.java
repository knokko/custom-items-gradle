package nl.knokko.customitems.plugin.data;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.ByteArrayBitInput;
import nl.knokko.customitems.bithelper.ByteArrayBitOutput;
import nl.knokko.customitems.item.CustomThrowableValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestPlayerThrowableCooldowns {

    private final CustomThrowableValues rock = new CustomThrowableValues(true);
    private final CustomThrowableValues spear = new CustomThrowableValues(true);

    public TestPlayerThrowableCooldowns() {
        rock.setName("rock");
        rock.setCooldown(10);
        spear.setName("spear");
    }

    @Test
    public void testSaveAndLoad() throws UnknownEncodingException {
        PlayerThrowableCooldowns cooldowns = new PlayerThrowableCooldowns();
        cooldowns.setOnCooldown(rock, 30);

        ByteArrayBitOutput output = new ByteArrayBitOutput();
        cooldowns.save(output);
        output.addInt(123);

        BitInput input = new ByteArrayBitInput(output.getBytes());
        PlayerThrowableCooldowns loadedCooldowns = new PlayerThrowableCooldowns();
        loadedCooldowns.load(input);
        assertEquals(123, input.readInt());

        assertTrue(loadedCooldowns.isOnCooldown(rock, 31));
        assertFalse(loadedCooldowns.isOnCooldown(spear, 31));
    }

    @Test
    public void testBackwardCompatibility1() throws UnknownEncodingException {
        BitInput input = IOHelper.getResourceBitInput("data/throwableCooldowns/backward1.bin", 23);
        PlayerThrowableCooldowns loadedCooldowns = new PlayerThrowableCooldowns();
        loadedCooldowns.load(input);
        assertEquals(123, input.readInt());

        assertTrue(loadedCooldowns.isOnCooldown(rock, 31));
        assertFalse(loadedCooldowns.isOnCooldown(spear, 31));
    }

    @Test
    public void testCooldowns() {
        PlayerThrowableCooldowns cooldowns = new PlayerThrowableCooldowns();
        assertFalse(cooldowns.isOnCooldown(rock, 20));
        assertFalse(cooldowns.isOnCooldown(spear, 20));

        cooldowns.setOnCooldown(rock, 20);
        assertTrue(cooldowns.isOnCooldown(rock, 20));
        assertFalse(cooldowns.isOnCooldown(spear, 20));
        assertTrue(cooldowns.isOnCooldown(rock, 29));
        assertFalse(cooldowns.isOnCooldown(spear, 29));
        assertFalse(cooldowns.isOnCooldown(rock, 30));
    }

    @Test
    public void testClean() {
        PlayerThrowableCooldowns cooldowns = new PlayerThrowableCooldowns();
        assertTrue(cooldowns.clean(50));

        cooldowns.setOnCooldown(rock, 50);
        assertFalse(cooldowns.clean(50));
        assertFalse(cooldowns.clean(59));
        assertTrue(cooldowns.clean(60));
    }
}
