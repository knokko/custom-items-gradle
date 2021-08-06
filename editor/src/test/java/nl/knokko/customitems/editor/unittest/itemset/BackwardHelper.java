package nl.knokko.customitems.editor.unittest.itemset;

import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.texture.NamedImage;
import nl.knokko.util.bits.BitInputStream;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class BackwardHelper {

    public static final float DELTA = 0.0001f;

    public static ItemSet loadItemSet(String name) {
        String resourceName = "backward/itemset/" + name + ".cisb";
        InputStream rawInput = BackwardHelper.class.getClassLoader().getResourceAsStream(resourceName);

        if (rawInput == null) {
            throw new IllegalArgumentException("Can't find resource '" + resourceName + "'");
        }

        BitInputStream bitInput = new BitInputStream(new BufferedInputStream(rawInput));
        ItemSet result;
        try {
            result = new ItemSet(name, bitInput);
        } catch (Exception e) {
            throw new RuntimeException("Let the test fail", e);
        }
        bitInput.terminate();
        return result;
    }

    public static void assertResourceEquals(String path, byte[] actual) {
        try {
            byte[] expected = new byte[actual.length];
            DataInputStream input = new DataInputStream(
                    BackwardHelper.class.getClassLoader().getResourceAsStream(path)
            );
            input.readFully(expected);

            assertEquals(-1, input.read());
            input.close();
            assertArrayEquals(expected, actual);
        } catch (IOException io) {
            throw new RuntimeException("Let the test fail", io);
        }
    }

    public static BufferedImage loadImage(String name) {
        try {
            InputStream input = BackwardHelper.class.getClassLoader().getResourceAsStream(
                    "backward/itemset/texture/" + name + ".png"
            );
            BufferedImage result = ImageIO.read(input);
            input.close();
            return result;
        } catch (IOException io) {
            throw new RuntimeException("Let the test fail", io);
        }
    }

    public static void checkTexture(ItemSet itemSet, String expectedName) {
        NamedImage texture = itemSet.getTextureByName(expectedName);
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

    public static String[] stringArray(String...strings) {
        return strings;
    }

    @SafeVarargs
    public static <T> List<T> listOf(T... elements) {
        List<T> result = new ArrayList<>(elements.length);
        Collections.addAll(result, elements);
        return result;
    }
}
