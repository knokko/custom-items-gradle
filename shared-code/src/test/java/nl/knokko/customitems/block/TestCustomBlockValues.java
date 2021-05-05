package nl.knokko.customitems.block;

import nl.knokko.customitems.block.drop.CustomBlockDrop;
import nl.knokko.customitems.block.drop.RequiredItems;
import nl.knokko.customitems.block.drop.SilkTouchRequirement;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.recipe.OutputTable;
import nl.knokko.customitems.texture.NamedImage;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitInputStream;
import nl.knokko.util.bits.ByteArrayBitInput;
import nl.knokko.util.bits.ByteArrayBitOutput;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestCustomBlockValues {

    private static OutputTable createExampleOutputTable1() {
        OutputTable.Entry example1 = new OutputTable.Entry("Result1", 40);

        List<OutputTable.Entry> entries = new ArrayList<>(1);
        entries.add(example1);

        return new OutputTable(entries);
    }

    private static RequiredItems createExampleRequiredItems1() {
        Collection<RequiredItems.VanillaEntry> vanillaItems = new ArrayList<>(1);
        vanillaItems.add(new RequiredItems.VanillaEntry(CIMaterial.GUNPOWDER, false));

        RequiredItems example = new RequiredItems(true);

        // I skip the custom items for now because they are annoying to test
        example.setCustomItems(new ArrayList<>(0));
        example.setEnabled(true);
        example.setInverted(false);
        example.setVanillaItems(vanillaItems);

        return example;
    }

    private static Collection<CustomBlockDrop> createExampleDrops1() {
        CustomBlockDrop example1 = new CustomBlockDrop(true);
        example1.setItemsToDrop(createExampleOutputTable1());
        example1.setRequiredItems(createExampleRequiredItems1());
        example1.setSilkTouchRequirement(SilkTouchRequirement.OPTIONAL);

        Collection<CustomBlockDrop> example = new ArrayList<>(1);
        example.add(example1);

        return example;
    }

    private static NamedImage createExampleTexture1() {
        return new NamedImage("example_texture", new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB));
    }

    private static CustomBlockValues createExample1() {
        CustomBlockValues example = new CustomBlockValues(true);

        example.setName("example1");
        example.setDrops(createExampleDrops1());
        example.setTexture(createExampleTexture1());

        return example;
    }

    private static BitInput fileInput(String name) {
        InputStream byteStream = TestCustomBlockValues.class.getClassLoader().getResourceAsStream(
                "nl/knokko/customitems/block/" + name + ".bin"
        );
        return new BitInputStream(byteStream);
    }

    @Test
    public void testSerialization() throws UnknownEncodingException {
        ByteArrayBitOutput output = new ByteArrayBitOutput();

        CustomBlockValues example1 = createExample1();
        example1.save(output, result -> output.addString((String) result));

        BitInput input = new ByteArrayBitInput(output.getBytes());
        CustomBlockValues loaded = CustomBlockValues.load(
                input,
                (name) -> { throw new UnsupportedOperationException("Shouldn't be used"); },
                input::readString,
                (textureName) -> {
                    assertEquals("example_texture", textureName);
                    return createExampleTexture1();
                }, false
        );

        assertEquals(example1, loaded);
    }

    @Test
    public void testBackwardCompatibility1() throws UnknownEncodingException {
        BitInput blockInput1 = fileInput("block1");
        CustomBlockValues loaded = CustomBlockValues.load(
                blockInput1,
                (name) -> { throw new UnsupportedOperationException("Shouldn't be used"); },
                blockInput1::readString,
                (textureName) -> {
                    assertEquals("example_texture", textureName);
                    return createExampleTexture1();
                }, false
        );
        blockInput1.terminate();

        assertEquals(createExample1(), loaded);
    }
}
