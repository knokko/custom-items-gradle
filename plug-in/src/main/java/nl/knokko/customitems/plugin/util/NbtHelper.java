package nl.knokko.customitems.plugin.util;

import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.changeme.nbtapi.iface.ReadableNBT;

public class NbtHelper {

    public static String getNested(ReadableNBT nbt, String[] key, String defaultValue) {
        if (nbt == null) return defaultValue;
        for (int index = 0; index < key.length - 1; index++) {
            nbt = nbt.getCompound(key[index]);
            if (nbt == null) return defaultValue;
        }
        if (defaultValue == null) return nbt.getOrNull(key[key.length - 1], String.class);
        else return nbt.getOrDefault(key[key.length - 1], defaultValue);
    }

    public static int getNested(ReadableNBT nbt, String[] key, int defaultValue) {
        if (nbt == null) return defaultValue;
        for (int index = 0; index < key.length - 1; index++) {
            nbt = nbt.getCompound(key[index]);
            if (nbt == null) return defaultValue;
        }
        return nbt.getOrDefault(key[key.length - 1], defaultValue);
    }

    public static void setNested(ReadWriteNBT nbt, String[] key, String value) {
        for (int index = 0; index < key.length - 1; index++) {
            nbt = nbt.getOrCreateCompound(key[index]);
        }
        nbt.setString(key[key.length - 1], value);
    }

    public static void setNested(ReadWriteNBT nbt, String[] key, int value) {
        for (int index = 0; index < key.length - 1; index++) {
            nbt = nbt.getOrCreateCompound(key[index]);
        }
        nbt.setInteger(key[key.length - 1], value);
    }

    public static void removeNested(ReadWriteNBT nbt, String[] key) {
        if (nbt == null) return;
        for (int index = 0; index < key.length - 1; index++) {
            nbt = nbt.getCompound(key[index]);
            if (nbt == null) return;
        }
        nbt.removeKey(key[key.length - 1]);
    }
}
