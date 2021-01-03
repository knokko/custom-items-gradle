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
package nl.knokko.customitems.plugin.set.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Lists;

import nl.knokko.core.plugin.CorePlugin;
import nl.knokko.core.plugin.item.GeneralItemNBT;
import nl.knokko.core.plugin.item.attributes.ItemAttributes;
import nl.knokko.core.plugin.item.attributes.ItemAttributes.Single;
import nl.knokko.customitems.effect.EquippedPotionEffect;
import nl.knokko.customitems.effect.PotionEffect;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.Enchantment;
import nl.knokko.customitems.item.ReplaceCondition;
import nl.knokko.customitems.item.ReplaceCondition.ConditionOperation;
import nl.knokko.customitems.item.nbt.ExtraItemNbt;
import nl.knokko.customitems.item.nbt.NbtPair;
import nl.knokko.customitems.item.nbt.NbtValue;
import nl.knokko.customitems.item.nbt.NbtValueType;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.util.ItemUtils;

public abstract class CustomItem extends nl.knokko.customitems.item.CustomItem {
	
	public static CIMaterial getMaterial(CustomItemType itemType) {
		String materialName = itemType.name();
        
        // This method distinguishes minecraft 1.12 and before from minecraft 1.13 and later
        // That is what we need here, because Bukkit renamed all WOOD_* tools to WOODEN_* tools
        if (CorePlugin.useNewCommands()) {
        	materialName = materialName.replace("WOOD", "WOODEN").replace("GOLD", "GOLDEN");
        } else {
        	materialName = materialName.replace("SHOVEL", "SPADE");
        }
        
        return CIMaterial.valueOf(materialName);
	}
	
	protected final CIMaterial material;

	protected final Single[] attributeModifiers;
	
	protected BooleanRepresentation boolRepresentation;
    
    public CustomItem(
    		CustomItemType itemType, short itemDamage, String name, String alias, 
    		String displayName, String[] lore, AttributeModifier[] attributes, 
    		Enchantment[] defaultEnchantments, boolean[] itemFlags, 
    		List<PotionEffect> playerEffects, List<PotionEffect> targetEffects, 
    		Collection<EquippedPotionEffect> equippedEffects, String[] commands, 
    		ReplaceCondition[] conditions, ConditionOperation op, 
    		ExtraItemNbt extraNbt, float attackRange
    ){
        super(
        		itemType, itemDamage, name, alias, displayName, lore, attributes, 
        		defaultEnchantments, itemFlags, playerEffects, targetEffects, 
        		equippedEffects, commands, conditions, op, extraNbt, attackRange
        );
        
        material = getMaterial(itemType);
        
        attributeModifiers = new Single[attributes.length];
        for (int index = 0; index < attributes.length; index++) {
        	AttributeModifier a = attributes[index];
        	attributeModifiers[index] = new Single(a.getAttribute().getName(), a.getSlot().getSlot(), a.getOperation().getOperation(), a.getValue());
        }
    }
    
    // Needed in ItemUpdater
    public short getInternalItemDamage() {
    	return itemDamage;
    }
    
    public void setBooleanRepresentation(BooleanRepresentation boolRep) {
    	this.boolRepresentation = boolRep;
    }
    
    public BooleanRepresentation getBooleanRepresentation() {
    	return boolRepresentation;
    }
    
    public Single[] getAttributeModifiers() {
    	return attributeModifiers;
    }
    
    public Long getMaxDurabilityNew() {
    	// Simple custom items are unbreakable, but tools should override this
    	return null;
    }
    
    protected List<String> createLore(){
    	return Lists.newArrayList(lore);
    }
    
    public List<String> createLore(Long durability) {
    	// This should be overridden by CustomTool
    	return createLore();
    }
    
    protected ItemMeta createItemMeta(ItemStack item, List<String> lore) {
    	ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(lore);
        meta.setUnbreakable(true);
        ItemFlag[] allFlags = ItemFlag.values();
        for (int index = 0; index < allFlags.length && index < itemFlags.length; index++) {
        	if (itemFlags[index]) {
        		meta.addItemFlags(allFlags[index]);
        	}
        }
        return meta;
    }
    
    public ItemStack create(int amount, List<String> lore){
    	ItemStack item = ItemAttributes.createWithAttributes(material.name(), amount, attributeModifiers);
        item.setItemMeta(createItemMeta(item, lore));
        item.setDurability(itemDamage);
        for (Enchantment enchantment : defaultEnchantments) {
        	item.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.getByName(enchantment.getType().name()), enchantment.getLevel());
        }
        
        ItemStack[] pResult = {null};
        CustomItemNBT.readWrite(item, nbt -> {
        	long lastModified = CustomItemsPlugin.getInstance().getSetExportTime();
        	nbt.set(name, lastModified, null, boolRepresentation);
        	initNBT(nbt);
        }, result -> pResult[0] = result);
        
        // Give it the extra nbt, if needed
        Collection<NbtPair> extraNbtPairs = extraNbt.getPairs();
        if (!extraNbtPairs.isEmpty()) {
        	GeneralItemNBT nbt = GeneralItemNBT.readWriteInstance(pResult[0]);
        	for (NbtPair extraPair : extraNbtPairs) {
        		NbtValue value = extraPair.getValue();
        		if (value.getType() == NbtValueType.INTEGER) {
        			nbt.set(extraPair.getKey().getParts(), value.getIntValue());
        		} else if (value.getType() == NbtValueType.STRING) {
        			nbt.set(extraPair.getKey().getParts(), value.getStringValue());
        		} else {
        			throw new Error("Unknown nbt value type: " + value.getType());
        		}
        	}
        	
        	pResult[0] = nbt.backToBukkit();
        }
        
        return pResult[0];
    }
    
    protected void initNBT(CustomItemNBT nbt) {}
    
    public ItemStack create(int amount) {
    	return create(amount, createLore());
    }
    
    public static short getDamage(ItemStack item) {
    	return item.getDurability();
    }
    
    public boolean forbidDefaultUse(ItemStack item) {
    	return true;
    }
    
    public boolean is(ItemStack item){
    	if (!ItemUtils.isEmpty(item)) {
    		boolean[] pResult = {false};
    		CustomItemNBT.readOnly(item, nbt -> {
    			if (nbt.hasOurNBT()) {
    				if (nbt.getName().equals(this.name)) {
    					pResult[0] = true;
    				}
    			}
    		});
    		
    		return pResult[0];
    	} else {
    		return false;
    	}
    }
    
    public CIMaterial getMaterial() {
    	return material;
    }
    
    public boolean canStack() {
    	return getMaxStacksize() > 1;
    }
    
    public abstract int getMaxStacksize();
    
    public boolean allowVanillaEnchanting() {
    	return false;
    }
    
    public boolean allowAnvilActions() {
    	return false;
    }
    
    public void onBlockBreak(Player player, ItemStack item, boolean wasSolid) {}
    
    public void onEntityHit(LivingEntity attacker, ItemStack weapon, Entity target) {
    	Collection<org.bukkit.potion.PotionEffect> pe = new ArrayList<org.bukkit.potion.PotionEffect>();
    	for (PotionEffect effect : playerEffects) {
    		pe.add(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.getByName(effect.getEffect().name()), effect.getDuration() * 20, effect.getLevel() - 1));
    	}
    	Collection<org.bukkit.potion.PotionEffect> te = new ArrayList<org.bukkit.potion.PotionEffect>();
    	for (PotionEffect effect : targetEffects) {
    		te.add(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.getByName(effect.getEffect().name()), effect.getDuration() * 20, effect.getLevel() - 1));
    	}
    	attacker.addPotionEffects(pe);
    	if (target instanceof LivingEntity) {
    		LivingEntity t = (LivingEntity) target;
    		t.addPotionEffects(te);
    	}
    }
}