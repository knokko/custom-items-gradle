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
package nl.knokko.customitems.item.enchantment;

import static nl.knokko.customitems.MCVersions.*;

import nl.knokko.customitems.NameHelper;

public enum EnchantmentType {
	
	PROTECTION_ENVIRONMENTAL("protection"),
	PROTECTION_FIRE("fire_protection"),
	PROTECTION_FALL("feather_falling"),
	PROTECTION_EXPLOSIONS("blast_protection"),
	PROTECTION_PROJECTILE("projectile_protection"),
	OXYGEN("respiration"),
	WATER_WORKER("aqua_affinity"),
	MENDING("mending"),
	THORNS("thorns"),
	VANISHING_CURSE("vanishing_curse"),
	DEPTH_STRIDER("depth_strider"),
	FROST_WALKER("frost_walker"),
	BINDING_CURSE("binding_curse"),
	DAMAGE_ALL("sharpness"),
	DAMAGE_UNDEAD("smite"),
	DAMAGE_ARTHROPODS("bane_of_arthropods"),
	KNOCKBACK("knockback"),
	FIRE_ASPECT("fire_aspect"),
	LOOT_BONUS_MOBS("looting"),
	SWEEPING_EDGE("sweeping"),
	DIG_SPEED("efficiency"),
	SILK_TOUCH("silk_touch"),
	DURABILITY("unbreaking"),
	LOOT_BONUS_BLOCKS("fortune"),
	ARROW_DAMAGE("power"),
	ARROW_KNOCKBACK("punch"),
	ARROW_FIRE("flame"),
	ARROW_INFINITE("infinity"),
	LUCK("luck_of_the_sea"),
	LURE("lure"),
	
	LOYALTY("loyalty", VERSION1_13),
	CHANNELING("channeling", VERSION1_13),
	RIPTIDE("riptide", VERSION1_13),
	IMPALING("impaling", VERSION1_13),
	
	MULTSHOT("multishot", VERSION1_14),
	PIERCING("piercing", VERSION1_14),
	QUICK_CHARGE("quick_charge", VERSION1_14),
	
	SOUL_SPEED("soul_speed", VERSION1_16);
	
	public final int version;
	private final String key;

	EnchantmentType(String key, int mcVersion) {
		this.version = mcVersion;
		this.key = key;
	}
	
	EnchantmentType(String key) {
		this(key, VERSION1_12);
	}
	
	@Override
	public String toString() {
		// It looks like no enchantments have been renamed or removed
		return NameHelper.getNiceEnumName(key, version, VERSION1_18);
	}

	/**
	 * The key to be used in <i>Enchantment.getByKey(NamespacedKey.minecraft(<b>key</b>))</i>
	 */
	public String getKey() {
		return key;
	}
}
