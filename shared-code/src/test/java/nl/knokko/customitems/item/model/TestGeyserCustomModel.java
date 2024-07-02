package nl.knokko.customitems.item.model;

import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.Jsoner;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitInputStream;
import nl.knokko.customitems.bithelper.ByteArrayBitInput;
import nl.knokko.customitems.bithelper.ByteArrayBitOutput;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.IOHelper;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class TestGeyserCustomModel {

    @Test
    public void testParseAttachable() throws IOException, JsonException {
        byte[] originalStaff = IOHelper.readAllBytes(Objects.requireNonNull(
                TestGeyserCustomModel.class.getResourceAsStream("geyser/staff-original.json")
        ));

        GeyserCustomModel.AttachableParseResult result = GeyserCustomModel.parseAttachable(
                "kci_test_staff", originalStaff
        );
        assertNull(result.error);
        assertEquals("geometry.geyser_custom.geo_69ffc83", result.geometryId);
        assertNotNull(result.newJsonBytes);

        Object actualStaff;
        try (Scanner scanner = new Scanner(new ByteArrayInputStream(result.newJsonBytes))) {
            StringBuilder builder = new StringBuilder();
            while (scanner.hasNextLine()) builder.append(scanner.nextLine());
            actualStaff = Jsoner.deserialize(builder.toString());
        }

        Object expectedStaff;
        try (Scanner scanner = new Scanner(Objects.requireNonNull(
                TestGeyserCustomModel.class.getResourceAsStream("geyser/staff-finetuned.json"))
        )) {
            StringBuilder builder = new StringBuilder();
            while (scanner.hasNextLine()) builder.append(scanner.nextLine().trim());
            expectedStaff = Jsoner.deserialize(builder.toString());
        }

        assertEquals(expectedStaff, actualStaff);
    }

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
