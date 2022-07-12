package nl.knokko.customitems.plugin.data;

import nl.knokko.customitems.bithelper.ByteArrayBitInput;
import nl.knokko.customitems.bithelper.ByteArrayBitOutput;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class TestContainerStorageKey {

    private static final ContainerStorageKey[] TEST_SUBJECTS = {
            new ContainerStorageKey("global", null, null, null),
            new ContainerStorageKey("per-player", null, null, new UUID(1234, 5678)),
            new ContainerStorageKey(
                    "per-location",
                    new PassiveLocation(new UUID(34, 12), 6, 7, 8),
                    null, null
            ), new ContainerStorageKey(
                    "per-location-per-player",
                    new PassiveLocation(new UUID(11, 12), 13, 14, 15),
                    null, new UUID(17, 18)
            ), new ContainerStorageKey("per-string", null, "string host 1", null),
            new ContainerStorageKey("per-string-per-player", null, "string host 2", new UUID(22, 33))
    };

    @Test
    public void testSerialization() throws UnknownEncodingException {
        for (ContainerStorageKey testSubject : TEST_SUBJECTS) {
            testSerialization(testSubject);
        }
    }

    private void testSerialization(ContainerStorageKey testSubject) throws UnknownEncodingException {
        ByteArrayBitOutput output = new ByteArrayBitOutput();
        testSubject.save(output);

        ContainerStorageKey loaded = ContainerStorageKey.load(new ByteArrayBitInput(output.getBytes()));
        assertEquals(testSubject, loaded);
    }

    @Test
    public void testHashcode() {
        for (ContainerStorageKey testSubject : TEST_SUBJECTS) {
            testHashcode(testSubject);
        }
    }

    private void testHashcode(ContainerStorageKey testSubject) {
        assertEquals(testSubject.hashCode(), new ContainerStorageKey(testSubject).hashCode());
    }
}
