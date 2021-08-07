package customitems.plugin.set.backward;

import nl.knokko.customitems.plugin.set.ItemSet;
import nl.knokko.util.bits.BitInputStream;

import java.io.BufferedInputStream;
import java.io.InputStream;

public class BackwardHelper {

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
}
