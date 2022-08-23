package nl.knokko.customitems.plugin.data;

import nl.knokko.customitems.bithelper.ByteArrayBitInput;
import nl.knokko.customitems.bithelper.ByteArrayBitOutput;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class TestEnergyStorageKey {

    @Test
    public void testSerialization() throws UnknownEncodingException {
        EnergyStorageKey testSubject = new EnergyStorageKey(new UUID(23, 56), new ContainerStorageKey(
                "test", null, "test2", new UUID(123, 456)
        ));
        ByteArrayBitOutput output = new ByteArrayBitOutput();
        testSubject.save(output);

        EnergyStorageKey loaded = EnergyStorageKey.load(new ByteArrayBitInput(output.getBytes()));
        assertEquals(testSubject, loaded);
    }
}
