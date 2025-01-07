package nl.knokko.customitems.serialization;

import nl.knokko.customitems.block.KciBlock;
import nl.knokko.customitems.item.KciItem;
import nl.knokko.customitems.item.model.DefaultItemModel;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.misc.CombinedResourcepack;
import nl.knokko.customitems.texture.ArmorTexture;
import nl.knokko.customitems.texture.FancyPantsTexture;
import nl.knokko.customitems.texture.KciTexture;
import nl.knokko.customitems.bithelper.BitInputStream;
import nl.knokko.customitems.util.StringEncoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class BackwardHelper {

    public static final float DELTA = 0.0001f;

    public static ItemSet[] loadItemSet(String name, boolean useTexty, boolean skipPluginTextures) {
        return new ItemSet[] {
                loadItemSet(name, ItemSet.Side.EDITOR, useTexty, skipPluginTextures),
                loadItemSet(name, ItemSet.Side.PLUGIN, useTexty, skipPluginTextures)
        };
    }

    public static ItemSet loadItemSet(String name, ItemSet.Side side, boolean useTexty, boolean skipPluginTextures) {
        String extension = side == ItemSet.Side.EDITOR ? ".cisb" : (useTexty ? ".cis.txt" : ".cis");
        String resourceName = "nl/knokko/customitems/serialization/" + name + extension;
        InputStream rawInput = BackwardHelper.class.getClassLoader().getResourceAsStream(resourceName);

        if (rawInput == null) {
            throw new IllegalArgumentException("Can't find resource '" + resourceName + "'");
        }

        if (side == ItemSet.Side.PLUGIN && useTexty) {
            try {
                List<Byte> rawByteList = new ArrayList<>(rawInput.available());
                int nextByte = rawInput.read();
                while (nextByte != -1) {
                    rawByteList.add((byte) nextByte);
                    nextByte = rawInput.read();
                }
                rawInput.close();

                byte[] rawByteArray = new byte[rawByteList.size()];
                for (int index = 0; index < rawByteArray.length; index++) {
                    rawByteArray[index] = rawByteList.get(index);
                }
                byte[] decodedBytes = StringEncoder.decodeTextyBytes(rawByteArray);
                rawInput = new ByteArrayInputStream(decodedBytes);
            } catch (IOException shouldNotHappen) {
                throw new RuntimeException(shouldNotHappen);
            }
        }

        BitInputStream bitInput = new BitInputStream(new BufferedInputStream(rawInput));
        ItemSet result;
        try {
            result = new ItemSet(bitInput, side, true);
            if (side == ItemSet.Side.EDITOR) {
                SaveEqualityHelper.testSaveEquality(result, skipPluginTextures);
            }
        } catch (Exception e) {
            throw new RuntimeException("Let the test fail", e);
        }
        bitInput.terminate();

        if (side == ItemSet.Side.EDITOR) {
            for (KciTexture texture : result.textures) assertNotNull(texture.getImage());
            for (ArmorTexture armorTexture : result.armorTextures) assertNotNull(armorTexture.getLayer1());
            for (FancyPantsTexture texture : result.fancyPants) assertNotNull(texture.getFrames().get(0).getLayer1());
            for (CombinedResourcepack pack : result.combinedResourcepacks) assertNotNull(pack.getContent());
        } else {
            for (FancyPantsTexture texture : result.fancyPants) assertEquals(0, texture.getFrames().size());
            if (skipPluginTextures) {
                assertEquals(0, result.textures.size());
                assertEquals(0, result.armorTextures.size());
                assertEquals(0, result.combinedResourcepacks.size());
            } else {
                for (KciTexture texture : result.textures) assertNull(texture.getImage());
                for (ArmorTexture armorTexture : result.armorTextures) assertNull(armorTexture.getLayer1());
                for (CombinedResourcepack pack : result.combinedResourcepacks) assertNull(pack.getContent());
            }
        }

        if (side == ItemSet.Side.EDITOR || !skipPluginTextures) {
            for (KciItem item : result.items) assertNotNull(item.getTexture());
            for (KciBlock block : result.blocks) assertNotNull(block.getModel());
        } else {
            for (KciItem item : result.items) {
                assertNull(item.getTextureReference());
                assertTrue(item.getModel() instanceof DefaultItemModel || item.getModel() == null);
                assertNull(item.getGeyserModel());
            }
            for (KciBlock block : result.blocks) assertNull(block.getModel());
        }

        return result;
    }

    public static void assertResourceEquals(String path, byte[] actual) {
        try {
            byte[] expected = new byte[actual.length];
            DataInputStream input = new DataInputStream(
                    Objects.requireNonNull(BackwardHelper.class.getClassLoader().getResourceAsStream(path))
            );
            input.readFully(expected);

            assertEquals(-1, input.read());
            input.close();
            assertArrayEquals(expected, actual);
        } catch (IOException io) {
            throw new RuntimeException("Let the test fail", io);
        }
    }

    public static void assertStringResourceEquals(String path, byte[] stringBytes) {
        try {
            InputStream input = BackwardHelper.class.getClassLoader().getResourceAsStream(path);
            assert input != null;
            List<Byte> actualByteList = new ArrayList<>(input.available());
            int next = input.read();
            while (next != -1) {
                // Don't count line separators
                if (next != 10 && next != 13) {
                    actualByteList.add((byte) next);
                }
                next = input.read();
            }
            input.close();

            List<Byte> expectedByteList = new ArrayList<>(stringBytes.length);
            for (byte stringByte : stringBytes) {
                if (stringByte != 10 && stringByte != 13) {
                    expectedByteList.add(stringByte);
                }
            }

            assertEquals(expectedByteList, actualByteList);
        } catch (IOException io) {
            throw new RuntimeException("Let the test fail", io);
        }
    }

    public static BufferedImage loadImage(String name) {
        try {
            InputStream input = BackwardHelper.class.getClassLoader().getResourceAsStream(
                    "nl/knokko/customitems/serialization/texture/" + name + ".png"
            );
            assert input != null;
            BufferedImage result = ImageIO.read(input);
            input.close();
            if (result == null) throw new IllegalArgumentException("Can't load image " + name);
            return result;
        } catch (IOException io) {
            throw new RuntimeException("Let the test fail", io);
        }
    }

    public static void checkTexture(ItemSet itemSet, String expectedName) {
        KciTexture texture = itemSet.textures.get(expectedName).get();
        if (itemSet.getSide() == ItemSet.Side.PLUGIN) return;
        BufferedImage expectedImage = loadImage(texture.getName());
        BufferedImage actualImage = texture.getImage();

        assertImageEqual(expectedImage, actualImage);
    }

    public static void assertImageEqual(BufferedImage expectedImage, BufferedImage actualImage) {
        assertEquals(expectedImage.getWidth(), actualImage.getWidth());
        assertEquals(expectedImage.getHeight(), actualImage.getHeight());
        for (int x = 0; x < expectedImage.getWidth(); x++) {
            for (int y = 0; y < expectedImage.getHeight(); y++) {
                assertEquals(expectedImage.getRGB(x, y), actualImage.getRGB(x, y));
            }
        }
    }

    @SafeVarargs
    public static <T> List<T> listOf(T... elements) {
        List<T> result = new ArrayList<>(elements.length);
        Collections.addAll(result, elements);
        return result;
    }
}
