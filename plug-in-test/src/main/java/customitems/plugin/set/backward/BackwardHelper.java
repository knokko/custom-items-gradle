package customitems.plugin.set.backward;

import nl.knokko.customitems.plugin.set.ItemSet;
import nl.knokko.util.bits.BitInputStream;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BackwardHelper {

    public static final float DELTA = 0.0001f;

    public static ItemSet loadItemSet(String name) {
        String resourceName = "backward/itemset/" + name + ".cis";
        InputStream rawInput = BackwardHelper.class.getClassLoader().getResourceAsStream(resourceName);

        if (rawInput == null) {
            throw new IllegalArgumentException("Can't find resource '" + resourceName + "'");
        }

        BitInputStream bitInput = new BitInputStream(new BufferedInputStream(rawInput));
        ItemSet result;
        try {
            result = new ItemSet(bitInput);
        } catch (Exception e) {
            throw new RuntimeException("Let the test fail", e);
        }
        bitInput.terminate();
        return result;
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
