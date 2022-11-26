package nl.knokko.customitems.item;

import nl.knokko.customitems.NameHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * A ItemFlag can hide some Attributes from ItemStacks.
 * Copied straight from Bukkit, but separated from the Bukkit API to ensure that the order
 * in which the constants occur won't change and so that the Editor doesn't need to import the
 * entire Bukkit API.
 */
public enum ItemFlag {

    /**
     * Setting to show/hide enchants
     */
    HIDE_ENCHANTS,
    /**
     * Setting to show/hide Attributes like Damage
     */
    HIDE_ATTRIBUTES,
    /**
     * Setting to show/hide the unbreakable State
     */
    HIDE_UNBREAKABLE,
    /**
     * Setting to show/hide what the ItemStack can break/destroy
     */
    HIDE_DESTROYS,
    /**
     * Setting to show/hide where this ItemStack can be build/placed on
     */
    HIDE_PLACED_ON,
    /**
     * Setting to show/hide potion effects on this ItemStack
     */
    HIDE_POTION_EFFECTS;
	
	public static boolean[] getDefaultValues() {
		return new boolean[]{false, false, true, false, false, false};
	}

	public static List<Boolean> getDefaultValuesList() {
	    List<Boolean> result = new ArrayList<>(6);
	    for (boolean defaultValue : getDefaultValues()) {
	        result.add(defaultValue);
        }
	    return result;
    }
	
	@Override
	public String toString() {
		return NameHelper.getNiceEnumName(name());
	}
}