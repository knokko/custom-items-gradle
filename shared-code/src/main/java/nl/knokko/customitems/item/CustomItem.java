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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nl.knokko.customitems.effect.EffectType;
import nl.knokko.customitems.effect.EquippedPotionEffect;
import nl.knokko.customitems.effect.PassivePotionEffect;
import nl.knokko.customitems.effect.PotionEffect;
import nl.knokko.customitems.item.ReplaceCondition.ConditionOperation;
import nl.knokko.customitems.item.nbt.ExtraItemNbt;
import nl.knokko.util.bits.BitInput;

public abstract class CustomItem {
	
	public static final int UNBREAKABLE_TOOL_DURABILITY = -1;
	
	public static Collection<EquippedPotionEffect> readEquippedEffects(BitInput input) {
		int numEffects = input.readInt();
		Collection<EquippedPotionEffect> effects = new ArrayList<>(numEffects);
		for (int counter = 0; counter < numEffects; counter++) {
			
			String effectName = input.readString();
			EffectType effectType = EffectType.valueOf(effectName);
			int level = input.readInt();
			PassivePotionEffect effect = new PassivePotionEffect(effectType, level);
			
			String slotName = input.readString();
			AttributeModifier.Slot slot = AttributeModifier.Slot.valueOf(slotName);
			
			effects.add(new EquippedPotionEffect(effect, slot));
		}
		
		return effects;
	}
    
    protected CustomItemType itemType;
    protected short itemDamage;
    
    protected String name;
    protected String alias;
    protected String displayName;
    protected String[] lore;
    
    protected AttributeModifier[] attributes;
    protected Enchantment[] defaultEnchantments;
    protected boolean[] itemFlags;
    
    protected List<PotionEffect> playerEffects;
    protected List<PotionEffect> targetEffects;
    protected Collection<EquippedPotionEffect> equippedEffects;
    
    protected String[] commands;
    protected ReplaceCondition[] conditions;
    protected ConditionOperation op;
    
    protected ExtraItemNbt extraNbt;
    protected float attackRange;
    
    public CustomItem(
    		CustomItemType itemType, short itemDamage, String name, String alias,
    		String displayName, String[] lore, AttributeModifier[] attributes, 
    		Enchantment[] defaultEnchantments, boolean[] itemFlags, 
    		List<PotionEffect> playerEffects, List<PotionEffect> targetEffects, 
    		Collection<EquippedPotionEffect> equippedEffects, String[] commands, 
    		ReplaceCondition[] conditions, ConditionOperation op,
    		ExtraItemNbt extraNbt, float attackRange
    ){
        this.itemType = itemType;
        this.itemDamage = itemDamage;
        if (name == null) throw new NullPointerException("name");
        this.name = name;
        this.alias = alias;
        this.displayName = displayName;
        this.lore = lore;
        this.attributes = attributes;
        this.defaultEnchantments = defaultEnchantments;
        this.itemFlags = itemFlags;
        this.playerEffects = playerEffects;
        this.targetEffects = targetEffects;
        this.equippedEffects = equippedEffects;
        this.commands = commands;
        this.conditions = conditions;
        this.op = op;
        this.extraNbt = extraNbt;
        this.attackRange = attackRange;
    }
    
    // For validation checks (and some other stuff), it is very important that the equals() method of custom 
    // items only return true when `other` refers to the same object as `this`.
    @Override
    public final boolean equals(Object other) {
    	return this == other;
    }
    
    public String getName(){
        return name;
    }
    
    public String getAlias() {
    	return alias;
    }
    
    public String getDisplayName() {
    	return displayName;
    }
    
    public String[] getLore() {
    	return lore;
    }
    
    public CustomItemType getItemType() {
    	return itemType;
    }
    
    public AttributeModifier[] getAttributes() {
    	return attributes;
    }
    
    public Enchantment[] getDefaultEnchantments() {
    	return defaultEnchantments;
    }
    
    public boolean[] getItemFlags() {
    	return itemFlags;
    }
    
    public List<PotionEffect> getPlayerEffects () {
    	return playerEffects;
    }
    
    public List<PotionEffect> getTargetEffects () {
    	return targetEffects;
    }
    
    public Collection<EquippedPotionEffect> getEquippedEffects() {
    	return equippedEffects;
    }
    
    public String[] getCommands() {
    	return commands;
    }
    
    public ReplaceCondition[] getReplaceConditions() {
    	return conditions;
    }
    
    public ConditionOperation getConditionOperator() {
    	return op;
    }
    
    public ExtraItemNbt getExtraNbt() {
    	return extraNbt;
    }
    
    public float getAttackRange() {
    	return attackRange;
    }
}