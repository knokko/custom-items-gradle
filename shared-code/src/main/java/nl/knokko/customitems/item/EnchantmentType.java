/*******************************************************************************
 * The MIT License
 *
 * Copyright (c) 2019 knokko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *  
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
package nl.knokko.customitems.item;

import static nl.knokko.customitems.MCVersions.*;

import nl.knokko.customitems.NameHelper;

public enum EnchantmentType {
	
	PROTECTION_ENVIRONMENTAL("protection", 0),
	PROTECTION_FIRE("fire_protection", 1),
	PROTECTION_FALL("feather_falling", 2),
	PROTECTION_EXPLOSIONS("blast_protection", 3),
	PROTECTION_PROJECTILE("projectile_protection", 4),
	OXYGEN("respiration", 5),
	WATER_WORKER("aqua_affinity", 6),
	MENDING("mending", 70),
	THORNS("thorns", 7),
	VANISHING_CURSE("vanishing_curse", 71),
	DEPTH_STRIDER("depth_strider", 8),
	FROST_WALKER("frost_walker", 9),
	BINDING_CURSE("binding_curse", 10),
	DAMAGE_ALL("sharpness", 16),
	DAMAGE_UNDEAD("smite", 17),
	DAMAGE_ARTHROPODS("bane_of_arthropods", 18),
	KNOCKBACK("knockback", 19),
	FIRE_ASPECT("fire_aspect", 20),
	LOOT_BONUS_MOBS("looting", 21),
	SWEEPING_EDGE("sweeping", 22),
	DIG_SPEED("efficiency", 32),
	SILK_TOUCH("silk_touch", 33),
	DURABILITY("unbreaking", 34),
	LOOT_BONUS_BLOCKS("fortune", 35),
	ARROW_DAMAGE("power", 48),
	ARROW_KNOCKBACK("punch", 49),
	ARROW_FIRE("flame", 50),
	ARROW_INFINITE("infinity", 51),
	LUCK("luck_of_the_sea", 61),
	LURE("lure", 62),
	
	LOYALTY("loyalty", 65, VERSION1_13),
	CHANNELING("channeling", 68, VERSION1_13),
	RIPTIDE("riptide", 67, VERSION1_13),
	IMPALING("impaling", 66, VERSION1_13),
	
	// Numeric IDs seem to have been removed completely in minecraft 1.14
	MULTSHOT("multishot", -1, VERSION1_14),
	PIERCING("piercing", -1, VERSION1_14),
	QUICK_CHARGE("quick_charge", -1, VERSION1_14),
	
	SOUL_SPEED("soul_speed", -1, VERSION1_16);
	
	private final String niceName;
	
	public final int version;
	private final int numericID;
	private final String minecraftName;
	
	private EnchantmentType(String minecraftName, int numericID, int mcVersion) {
		niceName = NameHelper.getNiceEnumName(name());
		version = mcVersion;
		this.numericID = numericID;
		this.minecraftName = minecraftName;
	}
	
	private EnchantmentType(String minecraftName, int numericID) {
		this(minecraftName, numericID, VERSION1_12);
	}
	
	@Override
	public String toString() {
		// It looks like no enchantments have been renamed or removed
		return NameHelper.getNiceEnumName(name(), version, VERSION1_17);
	}
	
	public String getName() {
		return niceName;
	}
	
	public int getNumericID() {
		if (numericID == -1) {
			throw new UnsupportedOperationException("This enchantment doesn't have a numeric ID");
		}
		return numericID;
	}
	
	public String getMinecraftName() {
		return minecraftName;
	}
}
