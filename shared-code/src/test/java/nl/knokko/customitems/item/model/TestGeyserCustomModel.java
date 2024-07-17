package nl.knokko.customitems.item.model;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitInputStream;
import nl.knokko.customitems.bithelper.ByteArrayBitInput;
import nl.knokko.customitems.bithelper.ByteArrayBitOutput;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestGeyserCustomModel {

    @Test
    public void testSaveAndLoad() throws UnknownEncodingException {
        GeyserCustomModel original = new GeyserCustomModel(
                "hello", "hi", new byte[1], new byte[2], new byte[3], new byte[4]
        );

        ByteArrayBitOutput output = new ByteArrayBitOutput();
        original.save(output);

        ByteArrayBitInput input = new ByteArrayBitInput(output.getBytes());
        GeyserCustomModel loaded = GeyserCustomModel.load(input);

        testLoaded(loaded);
    }

    @Test
    public void testBackwardCompatibility1() throws UnknownEncodingException {
        BitInput input = new BitInputStream(TestGeyserCustomModel.class.getResourceAsStream("geyser/backward1.bin"));
        GeyserCustomModel loaded = GeyserCustomModel.load(input);
        input.terminate();

        testLoaded(loaded);
    }

    private void testLoaded(GeyserCustomModel loaded) {
        assertEquals("hello", loaded.attachableId);
        assertEquals("hi", loaded.geometryId);
        assertEquals(1, loaded.animationFile.length);
        assertEquals(2, loaded.attachableFile.length);
        assertEquals(3, loaded.modelFile.length);
        assertEquals(4, loaded.textureFile.length);
    }
}
