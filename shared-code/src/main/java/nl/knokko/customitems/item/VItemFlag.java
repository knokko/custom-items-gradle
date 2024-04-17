package nl.knokko.customitems.item;

import nl.knokko.customitems.NameHelper;

import java.util.ArrayList;
import java.util.List;

import static nl.knokko.customitems.MCVersions.*;

public enum VItemFlag {

    HIDE_ENCHANTS(VERSION1_12, VERSION1_20),
    HIDE_ATTRIBUTES(VERSION1_12, VERSION1_20),
    HIDE_UNBREAKABLE(VERSION1_12, VERSION1_20),
    HIDE_DESTROYS(VERSION1_12, VERSION1_20),
    HIDE_PLACED_ON(VERSION1_12, VERSION1_20),
    HIDE_POTION_EFFECTS(VERSION1_12, VERSION1_20),
    HIDE_DYE(VERSION1_16, VERSION1_20),
    HIDE_ARMOR_TRIM(VERSION1_19, VERSION1_20);

    public static boolean[] getDefaultValues() {
		return new boolean[]{ false, false, true, false, false, false, false, false };
	}

	public static List<Boolean> getDefaultValuesList() {
	    List<Boolean> result = new ArrayList<>(8);
	    for (boolean defaultValue : getDefaultValues()) {
	        result.add(defaultValue);
        }
	    return result;
    }

    public final int firstVersion, lastVersion;

    VItemFlag(int firstVersion, int lastVersion) {
        this.firstVersion = firstVersion;
        this.lastVersion = lastVersion;
    }

	@Override
	public String toString() {
		return NameHelper.getNiceEnumName(name(), firstVersion, lastVersion);
	}
}