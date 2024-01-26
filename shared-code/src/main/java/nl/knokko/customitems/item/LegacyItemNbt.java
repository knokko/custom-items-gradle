package nl.knokko.customitems.item;

import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.trouble.UnknownEncodingException;

import java.util.ArrayList;
import java.util.List;

public class LegacyItemNbt {

    public static List<String> load(BitInput input) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("LegacyItemNbt", encoding);

        int numEntries = input.readInt();
        List<String> result = new ArrayList<>(numEntries);

        for (int counter = 0; counter < numEntries; counter++) {
            int numKeyParts = input.readInt();

            JsonObject root = new JsonObject();
            JsonObject node = root;
            String key = input.readString();

            for (int keyCounter = 0; keyCounter < numKeyParts - 1; keyCounter++) {
                JsonObject newNode = new JsonObject();
                node.put(key, newNode);
                node = newNode;
                key = input.readString();
            }

            byte typeOrdinal = input.readByte();
            if (typeOrdinal == 0) node.put(key, input.readString());
            else if (typeOrdinal == 1) node.put(key, input.readInt());
            else throw new UnknownEncodingException("LegacyNbtType", typeOrdinal);

            result.add(Jsoner.serialize(root));
        }

        return result;
    }
}
