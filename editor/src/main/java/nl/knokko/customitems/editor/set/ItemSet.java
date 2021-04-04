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
package nl.knokko.customitems.editor.set;

import static nl.knokko.customitems.MCVersions.FIRST_VERSION;
import static nl.knokko.customitems.MCVersions.LAST_VERSION;
import static nl.knokko.customitems.MCVersions.VERSION1_12;
import static nl.knokko.customitems.NameHelper.versionName;
import static nl.knokko.customitems.encoding.SetEncoding.ENCODING_1;
import static nl.knokko.customitems.encoding.SetEncoding.ENCODING_2;
import static nl.knokko.customitems.encoding.SetEncoding.ENCODING_3;
import static nl.knokko.customitems.encoding.SetEncoding.ENCODING_4;
import static nl.knokko.customitems.encoding.SetEncoding.ENCODING_5;
import static nl.knokko.customitems.encoding.SetEncoding.ENCODING_6;
import static nl.knokko.customitems.encoding.SetEncoding.ENCODING_7;
import static nl.knokko.customitems.encoding.SetEncoding.ENCODING_8;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageOutputStream;

import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.container.CustomContainer;
import nl.knokko.customitems.container.VanillaContainerType;
import nl.knokko.customitems.container.fuel.CustomFuelRegistry;
import nl.knokko.customitems.container.fuel.FuelEntry;
import nl.knokko.customitems.container.fuel.FuelMode;
import nl.knokko.customitems.container.slot.*;
import nl.knokko.customitems.container.slot.display.CustomItemDisplayItem;
import nl.knokko.customitems.container.slot.display.DataVanillaDisplayItem;
import nl.knokko.customitems.container.slot.display.SimpleVanillaDisplayItem;
import nl.knokko.customitems.container.slot.display.SlotDisplay;
import nl.knokko.customitems.damage.DamageResistances;
import nl.knokko.customitems.damage.DamageSource;
import nl.knokko.customitems.drops.BlockDrop;
import nl.knokko.customitems.drops.BlockType;
import nl.knokko.customitems.drops.CIEntityType;
import nl.knokko.customitems.drops.Drop;
import nl.knokko.customitems.drops.EntityDrop;
import nl.knokko.customitems.editor.Editor;
import nl.knokko.customitems.editor.set.item.*;
import nl.knokko.customitems.editor.set.item.texture.ArmorTextures;
import nl.knokko.customitems.editor.set.item.texture.BowTextures;
import nl.knokko.customitems.editor.set.projectile.cover.CustomProjectileCover;
import nl.knokko.customitems.editor.set.projectile.cover.EditorProjectileCover;
import nl.knokko.customitems.editor.set.projectile.cover.SphereProjectileCover;
import nl.knokko.customitems.editor.set.recipe.Recipe;
import nl.knokko.customitems.editor.set.recipe.ShapedRecipe;
import nl.knokko.customitems.editor.set.recipe.ShapelessRecipe;
import nl.knokko.customitems.editor.set.recipe.ingredient.CustomItemIngredient;
import nl.knokko.customitems.editor.set.recipe.ingredient.DataVanillaIngredient;
import nl.knokko.customitems.editor.set.recipe.ingredient.Ingredient;
import nl.knokko.customitems.editor.set.recipe.ingredient.NoIngredient;
import nl.knokko.customitems.editor.set.recipe.ingredient.SimpleVanillaIngredient;
import nl.knokko.customitems.editor.set.recipe.result.CopiedResult;
import nl.knokko.customitems.editor.set.recipe.result.CustomItemResult;
import nl.knokko.customitems.editor.set.recipe.result.DataVanillaResult;
import nl.knokko.customitems.editor.set.recipe.result.Result;
import nl.knokko.customitems.editor.set.recipe.result.SimpleVanillaResult;
import nl.knokko.customitems.editor.util.ReadOnlyReference;
import nl.knokko.customitems.editor.util.Reference;
import nl.knokko.customitems.effect.EffectType;
import nl.knokko.customitems.effect.EquippedPotionEffect;
import nl.knokko.customitems.effect.PotionEffect;
import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.AttributeModifier.Attribute;
import nl.knokko.customitems.item.AttributeModifier.Operation;
import nl.knokko.customitems.item.AttributeModifier.Slot;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.CustomItemType.Category;
import nl.knokko.customitems.item.CustomToolDurability;
import nl.knokko.customitems.item.Enchantment;
import nl.knokko.customitems.item.EnchantmentType;
import nl.knokko.customitems.item.ItemFlag;
import nl.knokko.customitems.item.ItemSetBase;
import nl.knokko.customitems.item.ReplaceCondition;
import nl.knokko.customitems.item.ReplaceCondition.ConditionOperation;
import nl.knokko.customitems.item.ReplaceCondition.ReplacementCondition;
import nl.knokko.customitems.item.ReplaceCondition.ReplacementOperation;
import nl.knokko.customitems.item.WandCharges;
import nl.knokko.customitems.item.nbt.ExtraItemNbt;
import nl.knokko.customitems.projectile.CIProjectile;
import nl.knokko.customitems.projectile.ProjectileCover;
import nl.knokko.customitems.projectile.effects.ProjectileEffect;
import nl.knokko.customitems.projectile.effects.ProjectileEffects;
import nl.knokko.customitems.recipe.ContainerRecipe;
import nl.knokko.customitems.recipe.ContainerRecipe.InputEntry;
import nl.knokko.customitems.recipe.ContainerRecipe.OutputEntry;
import nl.knokko.customitems.recipe.OutputTable;
import nl.knokko.customitems.recipe.SCIngredient;
import nl.knokko.customitems.trouble.IntegrityException;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.StringEncoder;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.gui.keycode.KeyCode;
import nl.knokko.gui.window.input.WindowInput;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;
import nl.knokko.util.bits.BooleanArrayBitOutput;
import nl.knokko.util.bits.ByteArrayBitInput;
import nl.knokko.util.bits.ByteArrayBitOutput;

public class ItemSet implements ItemSetBase {

	private Recipe loadRecipe(BitInput input) throws UnknownEncodingException {
		byte encoding = input.readByte();
		if (encoding == RecipeEncoding.SHAPED_RECIPE)
			return new ShapedRecipe(input, this);
		if (encoding == RecipeEncoding.SHAPELESS_RECIPE)
			return new ShapelessRecipe(input, this);
		throw new UnknownEncodingException("Recipe", encoding);
	}
	
	private CustomFuelRegistry loadFuelRegistry(BitInput input) throws UnknownEncodingException {
		return CustomFuelRegistry.load(input, () -> Ingredient.loadIngredient(input, this));
	}
	
	private CustomContainer loadContainer(BitInput input) throws UnknownEncodingException {
		return CustomContainer.load(input, 
				this::getCustomItemByName, this::getFuelRegistryByName, 
				() -> Ingredient.loadIngredient(input, this),
				() -> Recipe.loadResult(input, this)
		);
	}

	private CustomItem loadItem(BitInput input, boolean checkCustomModel) throws UnknownEncodingException {
		byte encoding = input.readByte();
		switch (encoding) {
			case ItemEncoding.ENCODING_SIMPLE_1 : return loadSimpleItem1(input, checkCustomModel);
			case ItemEncoding.ENCODING_SIMPLE_2 : return loadSimpleItem2(input, checkCustomModel);
			case ItemEncoding.ENCODING_SIMPLE_3 : return loadSimpleItem3(input, checkCustomModel);
			case ItemEncoding.ENCODING_SIMPLE_4 : return loadSimpleItem4(input, checkCustomModel);
			case ItemEncoding.ENCODING_SIMPLE_5 : return loadSimpleItem5(input, checkCustomModel);
			case ItemEncoding.ENCODING_SIMPLE_6 : return loadSimpleItem6(input, checkCustomModel);
			case ItemEncoding.ENCODING_SIMPLE_9 : return loadSimpleItem9(input, checkCustomModel);
			case ItemEncoding.ENCODING_TOOL_2 : return loadTool2(input, checkCustomModel);
			case ItemEncoding.ENCODING_TOOL_3 : return loadTool3(input, checkCustomModel);
			case ItemEncoding.ENCODING_TOOL_4 : return loadTool4(input, checkCustomModel);
			case ItemEncoding.ENCODING_TOOL_5 : return loadTool5(input, checkCustomModel);
			case ItemEncoding.ENCODING_TOOL_6 : return loadTool6(input, checkCustomModel);
			case ItemEncoding.ENCODING_TOOL_9 : return loadTool9(input, checkCustomModel);
			case ItemEncoding.ENCODING_SHEAR_5 : return loadShear5(input, checkCustomModel);
			case ItemEncoding.ENCODING_SHEAR_6 : return loadShear6(input, checkCustomModel);
			case ItemEncoding.ENCODING_SHEAR_9 : return loadShear9(input, checkCustomModel);
			case ItemEncoding.ENCODING_BOW_3 : return loadBow3(input, checkCustomModel);
			case ItemEncoding.ENCODING_BOW_4 : return loadBow4(input, checkCustomModel);
			case ItemEncoding.ENCODING_BOW_5 : return loadBow5(input, checkCustomModel);
			case ItemEncoding.ENCODING_BOW_6 : return loadBow6(input, checkCustomModel);
			case ItemEncoding.ENCODING_BOW_9 : return loadBow9(input, checkCustomModel);
			case ItemEncoding.ENCODING_ARMOR_4 : return loadArmor4(input, checkCustomModel);
			case ItemEncoding.ENCODING_ARMOR_5 : return loadArmor5(input, checkCustomModel);
			case ItemEncoding.ENCODING_ARMOR_6 : return loadArmor6(input, checkCustomModel);
			case ItemEncoding.ENCODING_ARMOR_7 : return loadArmor7(input, checkCustomModel);
			case ItemEncoding.ENCODING_ARMOR_8 : return loadArmor8(input, checkCustomModel);
			case ItemEncoding.ENCODING_ARMOR_9 : return loadArmor9(input, checkCustomModel);
			case ItemEncoding.ENCODING_SHIELD_6 : return loadShield6(input, checkCustomModel);
			case ItemEncoding.ENCODING_SHIELD_7 : return loadShield7(input, checkCustomModel);
			case ItemEncoding.ENCODING_SHIELD_9 : return loadShield9(input, checkCustomModel);
			case ItemEncoding.ENCODING_TRIDENT_7 : return loadTrident7(input, checkCustomModel);
			case ItemEncoding.ENCODING_TRIDENT_8 : return loadTrident8(input, checkCustomModel);
			case ItemEncoding.ENCODING_TRIDENT_9 : return loadTrident9(input, checkCustomModel);
			case ItemEncoding.ENCODING_HOE_5 : return loadHoe5(input, checkCustomModel);
			case ItemEncoding.ENCODING_HOE_6 : return loadHoe6(input, checkCustomModel);
			case ItemEncoding.ENCODING_HOE_9 : return loadHoe9(input, checkCustomModel);
			case ItemEncoding.ENCODING_WAND_8: return loadWand8(input);
			case ItemEncoding.ENCODING_WAND_9: return loadWand9(input);
			case ItemEncoding.ENCODING_HELMET3D_9: return loadHelmet3d9(input, checkCustomModel);
			case ItemEncoding.ENCODING_POCKET_CONTAINER_9: return loadPocketContainer9(input, checkCustomModel);
			default : throw new UnknownEncodingException("Item", encoding);
		}
	}
	
	private byte[] loadCustomModel(BitInput input, boolean check) {
		if (check && input.readBoolean()) {
			return input.readByteArray();
		} else {
			return null;
		}
	}

	private CustomItem loadSimpleItem1(BitInput input, boolean checkCustomModel) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		// System.out.println("itemType is " + itemType.name());
		// System.out.println("loadSimple item with damage " + damage + " and name " +
		// name + " and displayName " + displayName);
		String[] lore = new String[input.readByte() & 0xFF];
		// System.out.println("lore length is " + lore.length);
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[0];
		String imageName = input.readJavaString();
		NamedImage texture = getTextureByName(imageName);
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new SimpleCustomItem(
				itemType, name, "", displayName, lore, attributes, 
				new Enchantment[0], 64, texture, ItemFlag.getDefaultValues(), 
				customModel, new ArrayList<>(), new ArrayList<>(), 
				new ArrayList<>(), new String[] {}, 
				new ReplaceCondition[0], ConditionOperation.NONE, 
				new ExtraItemNbt(), 1f
		);
	}

	private CustomItem loadSimpleItem2(BitInput input, boolean checkCustomModel) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		String imageName = input.readJavaString();
		NamedImage texture = getTextureByName(imageName);
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new SimpleCustomItem(
				itemType, name, "", displayName, lore, attributes, 
				new Enchantment[0], 64, texture, ItemFlag.getDefaultValues(), 
				customModel, new ArrayList<>(), new ArrayList<>(), 
				new ArrayList<>(), new String[] {}, 
				new ReplaceCondition[0], ConditionOperation.NONE, 
				new ExtraItemNbt(), 1f
		);
	}
	
	private CustomItem loadSimpleItem3(BitInput input, boolean checkCustomModel) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		String imageName = input.readJavaString();
		NamedImage texture = getTextureByName(imageName);
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new SimpleCustomItem(
				itemType, name, "", displayName, lore, attributes, 
				defaultEnchantments, 64, texture, ItemFlag.getDefaultValues(), 
				customModel, new ArrayList<>(), new ArrayList<>(), 
				new ArrayList<>(), new String[] {}, 
				new ReplaceCondition[0], ConditionOperation.NONE, 
				new ExtraItemNbt(), 1f
		);
	}
	
	private CustomItem loadSimpleItem4(BitInput input, boolean checkCustomModel) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		byte maxStacksize = input.readByte();
		String imageName = input.readJavaString();
		NamedImage texture = getTextureByName(imageName);
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new SimpleCustomItem(
				itemType, name, "", displayName, lore, attributes, 
				defaultEnchantments, maxStacksize, texture, 
				ItemFlag.getDefaultValues(), customModel, 
				new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
				new String[] {}, new ReplaceCondition[0], ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}
	
	private CustomItem loadSimpleItem5(BitInput input, boolean checkCustomModel) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		byte maxStacksize = input.readByte();
		
		// Use hardcoded 6 instead of variable because only 6 item flags existed in this encoding
		boolean[] itemFlags = input.readBooleans(6);
		
		String imageName = input.readJavaString();
		NamedImage texture = getTextureByName(imageName);
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new SimpleCustomItem(
				itemType, name, "", displayName, lore, attributes, 
				defaultEnchantments, maxStacksize, texture, itemFlags, customModel, 
				new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
				new String[] {}, new ReplaceCondition[0], ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}
	
	private CustomItem loadSimpleItem6(BitInput input, boolean checkCustomModel) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		byte maxStacksize = input.readByte();
		
		// Use hardcoded 6 instead of variable because only 6 item flags existed in this encoding
		boolean[] itemFlags = input.readBooleans(6);
		
		List<PotionEffect> playerEffects = new ArrayList<PotionEffect>();
		int peLength = (input.readByte() & 0xFF);
		for (int index = 0; index < peLength; index++) {
			playerEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		List<PotionEffect> targetEffects = new ArrayList<PotionEffect>();
		int teLength = (input.readByte() & 0xFF);
		for (int index = 0; index < teLength; index++) {
			targetEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		String[] commands = new String[input.readByte() & 0xFF];
		for (int index = 0; index < commands.length; index++) {
			commands[index] = input.readJavaString();
		}
		
		String imageName = input.readJavaString();
		NamedImage texture = null;
		for (NamedImage current : textures) {
			if (current.getName().equals(imageName)) {
				texture = current;
				break;
			}
		}
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new SimpleCustomItem(
				itemType, name, "", displayName, lore, attributes, 
				defaultEnchantments, maxStacksize, texture, itemFlags, customModel, 
				playerEffects, targetEffects, new ArrayList<>(), commands, 
				new ReplaceCondition[0], ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}
	
	private CustomItem loadSimpleItem9(
			BitInput input, boolean checkCustomModel
	) throws UnknownEncodingException {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		input.readShort();
		String name = input.readJavaString();
		String alias = input.readString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		byte maxStacksize = input.readByte();
		
		// Use hardcoded 6 instead of variable because only 6 item flags existed in this encoding
		boolean[] itemFlags = input.readBooleans(6);
		
		List<PotionEffect> playerEffects = new ArrayList<PotionEffect>();
		int peLength = (input.readByte() & 0xFF);
		for (int index = 0; index < peLength; index++) {
			playerEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		List<PotionEffect> targetEffects = new ArrayList<PotionEffect>();
		int teLength = (input.readByte() & 0xFF);
		for (int index = 0; index < teLength; index++) {
			targetEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		Collection<EquippedPotionEffect> equippedEffects = CustomItem.readEquippedEffects(input);
		String[] commands = new String[input.readByte() & 0xFF];
		for (int index = 0; index < commands.length; index++) {
			commands[index] = input.readJavaString();
		}
		
		ReplaceCondition[] conditions = new ReplaceCondition[input.readByte() & 0xFF];
		for (int index = 0; index < conditions.length; index++) {
			conditions[index] = loadReplaceCondition(input);
		}
		ConditionOperation op = ConditionOperation.valueOf(input.readJavaString());
		ExtraItemNbt extraNbt = ExtraItemNbt.load(input);
		float attackRange = input.readFloat();
		
		String imageName = input.readJavaString();
		NamedImage texture = null;
		for (NamedImage current : textures) {
			if (current.getName().equals(imageName)) {
				texture = current;
				break;
			}
		}
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new SimpleCustomItem(
				itemType, name, alias, displayName, lore, attributes, 
				defaultEnchantments, maxStacksize, texture, itemFlags, customModel, 
				playerEffects, targetEffects, equippedEffects, 
				commands, conditions, op, extraNbt, attackRange
		);
	}

	private CustomItem loadTool2(BitInput input, boolean checkCustomModel) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		int durability = input.readInt();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		String imageName = input.readJavaString();
		NamedImage texture = getTextureByName(imageName);
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new CustomTool(
				itemType, name, "", displayName, lore, attributes, 
				new Enchantment[0], durability, allowEnchanting, allowAnvil, 
				new NoIngredient(), texture, ItemFlag.getDefaultValues(),
				CustomToolDurability.defaultEntityHitDurabilityLoss(itemType), 
				CustomToolDurability.defaultBlockBreakDurabilityLoss(itemType), 
				customModel, new ArrayList<>(), new ArrayList<>(), 
				new ArrayList<>(), new String[] {}, 
				new ReplaceCondition[0], ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}

	private CustomItem loadTool3(BitInput input, boolean checkCustomModel) throws UnknownEncodingException {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		int durability = input.readInt();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = Ingredient.loadIngredient(input, this);
		String imageName = input.readJavaString();
		NamedImage texture = getTextureByName(imageName);
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new CustomTool(
				itemType, name, "", displayName, lore, attributes, 
				new Enchantment[0], durability, allowEnchanting, allowAnvil, 
				repairItem, texture, ItemFlag.getDefaultValues(), 
				CustomToolDurability.defaultEntityHitDurabilityLoss(itemType), 
				CustomToolDurability.defaultBlockBreakDurabilityLoss(itemType), 
				customModel, new ArrayList<>(), new ArrayList<>(), 
				new ArrayList<>(), new String[] {}, 
				new ReplaceCondition[0], ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}
	
	private CustomItem loadTool4(BitInput input, boolean checkCustomModel) throws UnknownEncodingException {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = Ingredient.loadIngredient(input, this);
		String imageName = input.readJavaString();
		NamedImage texture = getTextureByName(imageName);
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new CustomTool(
				itemType, name, "", displayName, lore, attributes, 
				defaultEnchantments, durability, allowEnchanting,
				allowAnvil, repairItem, texture, ItemFlag.getDefaultValues(),
				CustomToolDurability.defaultEntityHitDurabilityLoss(itemType), 
				CustomToolDurability.defaultBlockBreakDurabilityLoss(itemType), 
				customModel, new ArrayList<>(), new ArrayList<>(), 
				new ArrayList<>(), new String[] {}, 
				new ReplaceCondition[0], ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}
	
	private CustomItem loadTool5(BitInput input, boolean checkCustomModel) throws UnknownEncodingException{
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = Ingredient.loadIngredient(input, this);
		
		// Use hardcoded 6 instead of variable because only 6 item flags existed in this encoding
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		
		String imageName = input.readJavaString();
		NamedImage texture = getTextureByName(imageName);
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new CustomTool(
				itemType, name, "", displayName, lore, attributes, 
				defaultEnchantments, durability, allowEnchanting, allowAnvil, 
				repairItem, texture, itemFlags, entityHitDurabilityLoss, 
				blockBreakDurabilityLoss, customModel,
				new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
				new String[] {}, new ReplaceCondition[0], ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}
	
	private CustomItem loadTool6(BitInput input, boolean checkCustomModel) throws UnknownEncodingException {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = Ingredient.loadIngredient(input, this);
		
		// Use hardcoded 6 instead of variable because only 6 item flags existed in this encoding
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		
		List<PotionEffect> playerEffects = new ArrayList<PotionEffect>();
		int peLength = (input.readByte() & 0xFF);
		for (int index = 0; index < peLength; index++) {
			playerEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		List<PotionEffect> targetEffects = new ArrayList<PotionEffect>();
		int teLength = (input.readByte() & 0xFF);
		for (int index = 0; index < teLength; index++) {
			targetEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		String[] commands = new String[input.readByte() & 0xFF];
		for (int index = 0; index < commands.length; index++) {
			commands[index] = input.readJavaString();
		}
		
		String imageName = input.readJavaString();
		NamedImage texture = null;
		for (NamedImage current : textures) {
			if (current.getName().equals(imageName)) {
				texture = current;
				break;
			}
		}
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new CustomTool(
				itemType, name, "", displayName, lore, attributes, 
				defaultEnchantments, durability, allowEnchanting, allowAnvil, 
				repairItem, texture, itemFlags, entityHitDurabilityLoss, 
				blockBreakDurabilityLoss, customModel, playerEffects, targetEffects, 
				new ArrayList<>(), commands, 
				new ReplaceCondition[0], ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}
	
	private CustomItem loadTool9(
			BitInput input, boolean checkCustomModel
	) throws UnknownEncodingException {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		input.readShort();
		String name = input.readJavaString();
		String alias = input.readString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = Ingredient.loadIngredient(input, this);
		
		// Use hardcoded 6 instead of variable because only 6 item flags existed in this encoding
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		
		List<PotionEffect> playerEffects = new ArrayList<PotionEffect>();
		int peLength = (input.readByte() & 0xFF);
		for (int index = 0; index < peLength; index++) {
			playerEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		List<PotionEffect> targetEffects = new ArrayList<PotionEffect>();
		int teLength = (input.readByte() & 0xFF);
		for (int index = 0; index < teLength; index++) {
			targetEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		Collection<EquippedPotionEffect> equippedEffects = CustomItem.readEquippedEffects(input);
		String[] commands = new String[input.readByte() & 0xFF];
		for (int index = 0; index < commands.length; index++) {
			commands[index] = input.readJavaString();
		}
		ReplaceCondition[] conditions = new ReplaceCondition[input.readByte() & 0xFF];
		for (int index = 0; index < conditions.length; index++) {
			conditions[index] = loadReplaceCondition(input);
		}
		ConditionOperation op = ConditionOperation.valueOf(input.readJavaString());
		ExtraItemNbt extraNbt = ExtraItemNbt.load(input);
		float attackRange = input.readFloat();
		
		String imageName = input.readJavaString();
		NamedImage texture = null;
		for (NamedImage current : textures) {
			if (current.getName().equals(imageName)) {
				texture = current;
				break;
			}
		}
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new CustomTool(
				itemType, name, alias, displayName, lore, attributes, 
				defaultEnchantments, durability, allowEnchanting, allowAnvil, 
				repairItem, texture, itemFlags, entityHitDurabilityLoss, 
				blockBreakDurabilityLoss, customModel, playerEffects, targetEffects, 
				equippedEffects, commands, conditions, op, extraNbt, attackRange
		);
	}
	
	private CustomItem loadHoe5(BitInput input, boolean checkCustomModel) throws UnknownEncodingException {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = Ingredient.loadIngredient(input, this);
		
		// Use hardcoded 6 instead of variable because only 6 item flags existed in this encoding
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		int tillDurabilityLoss = input.readInt();
		
		String imageName = input.readJavaString();
		NamedImage texture = getTextureByName(imageName);
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new CustomHoe(
				itemType, name, "", displayName, lore, attributes, 
				defaultEnchantments, durability, allowEnchanting, allowAnvil, 
				repairItem, texture, itemFlags, entityHitDurabilityLoss, 
				blockBreakDurabilityLoss, tillDurabilityLoss, customModel, 
				new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 
				new String[] {}, new ReplaceCondition[0], ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}
	
	private CustomItem loadHoe6(BitInput input, boolean checkCustomModel) throws UnknownEncodingException {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = Ingredient.loadIngredient(input, this);
		
		// Use hardcoded 6 instead of variable because only 6 item flags existed in this encoding
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		int tillDurabilityLoss = input.readInt();
		
		List<PotionEffect> playerEffects = new ArrayList<PotionEffect>();
		int peLength = (input.readByte() & 0xFF);
		for (int index = 0; index < peLength; index++) {
			playerEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		List<PotionEffect> targetEffects = new ArrayList<PotionEffect>();
		int teLength = (input.readByte() & 0xFF);
		for (int index = 0; index < teLength; index++) {
			targetEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		String[] commands = new String[input.readByte() & 0xFF];
		for (int index = 0; index < commands.length; index++) {
			commands[index] = input.readJavaString();
		}
		
		String imageName = input.readJavaString();
		NamedImage texture = null;
		for (NamedImage current : textures) {
			if (current.getName().equals(imageName)) {
				texture = current;
				break;
			}
		}
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new CustomHoe(
				itemType, name, "", displayName, lore, attributes, 
				defaultEnchantments, durability, allowEnchanting, allowAnvil, 
				repairItem, texture, itemFlags, entityHitDurabilityLoss, 
				blockBreakDurabilityLoss, tillDurabilityLoss, customModel, 
				playerEffects, targetEffects, new ArrayList<>(), commands, 
				new ReplaceCondition[0], ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}
	
	private CustomItem loadHoe9(
			BitInput input, boolean checkCustomModel
	) throws UnknownEncodingException {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		input.readShort();
		String name = input.readJavaString();
		String alias = input.readString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = Ingredient.loadIngredient(input, this);
		
		// Use hardcoded 6 instead of variable because only 6 item flags existed in this encoding
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		int tillDurabilityLoss = input.readInt();
		
		List<PotionEffect> playerEffects = new ArrayList<PotionEffect>();
		int peLength = (input.readByte() & 0xFF);
		for (int index = 0; index < peLength; index++) {
			playerEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		List<PotionEffect> targetEffects = new ArrayList<PotionEffect>();
		int teLength = (input.readByte() & 0xFF);
		for (int index = 0; index < teLength; index++) {
			targetEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		Collection<EquippedPotionEffect> equippedEffects = CustomItem.readEquippedEffects(input);
		String[] commands = new String[input.readByte() & 0xFF];
		for (int index = 0; index < commands.length; index++) {
			commands[index] = input.readJavaString();
		}
		ReplaceCondition[] conditions = new ReplaceCondition[input.readByte() & 0xFF];
		for (int index = 0; index < conditions.length; index++) {
			conditions[index] = loadReplaceCondition(input);
		}
		ConditionOperation op = ConditionOperation.valueOf(input.readJavaString());
		ExtraItemNbt extraNbt = ExtraItemNbt.load(input);
		float attackRange = input.readFloat();
		
		String imageName = input.readJavaString();
		NamedImage texture = null;
		for (NamedImage current : textures) {
			if (current.getName().equals(imageName)) {
				texture = current;
				break;
			}
		}
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new CustomHoe(
				itemType, name, alias, displayName, lore, attributes, 
				defaultEnchantments, durability, allowEnchanting, allowAnvil, 
				repairItem, texture, itemFlags, entityHitDurabilityLoss, 
				blockBreakDurabilityLoss, tillDurabilityLoss, customModel, 
				playerEffects, targetEffects, equippedEffects, 
				commands, conditions, op, extraNbt, attackRange
		);
	}
	
	private CustomItem loadShear5(BitInput input, boolean checkCustomModel) throws UnknownEncodingException {
		CustomItemType.valueOf(input.readJavaString());
		input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = Ingredient.loadIngredient(input, this);
		
		// Use hardcoded 6 instead of variable because only 6 item flags existed in this encoding
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		int shearDurabilityLoss = input.readInt();
		
		String imageName = input.readJavaString();
		NamedImage texture = getTextureByName(imageName);
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new CustomShears(
				name, "", displayName, lore, attributes, defaultEnchantments, 
				durability, allowEnchanting, allowAnvil, repairItem, texture, 
				itemFlags, entityHitDurabilityLoss, blockBreakDurabilityLoss, 
				shearDurabilityLoss, customModel, new ArrayList<>(), 
				new ArrayList<>(), new ArrayList<>(), new String[] {}, 
				new ReplaceCondition[0], ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}
	
	private CustomItem loadShear6(BitInput input, boolean checkCustomModel) throws UnknownEncodingException {
		CustomItemType.valueOf(input.readJavaString());
		input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = Ingredient.loadIngredient(input, this);
		
		// Use hardcoded 6 instead of variable because only 6 item flags existed in this encoding
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		int shearDurabilityLoss = input.readInt();
		
		List<PotionEffect> playerEffects = new ArrayList<PotionEffect>();
		int peLength = (input.readByte() & 0xFF);
		for (int index = 0; index < peLength; index++) {
			playerEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		List<PotionEffect> targetEffects = new ArrayList<PotionEffect>();
		int teLength = (input.readByte() & 0xFF);
		for (int index = 0; index < teLength; index++) {
			targetEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		String[] commands = new String[input.readByte() & 0xFF];
		for (int index = 0; index < commands.length; index++) {
			commands[index] = input.readJavaString();
		}
		
		String imageName = input.readJavaString();
		NamedImage texture = null;
		for (NamedImage current : textures) {
			if (current.getName().equals(imageName)) {
				texture = current;
				break;
			}
		}
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new CustomShears(
				name, "", displayName, lore, attributes, defaultEnchantments, 
				durability, allowEnchanting, allowAnvil, repairItem, texture, 
				itemFlags, entityHitDurabilityLoss, blockBreakDurabilityLoss, 
				shearDurabilityLoss, customModel, playerEffects, targetEffects, 
				new ArrayList<>(), commands, 
				new ReplaceCondition[0], ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}
	
	private CustomItem loadShear9(
			BitInput input, boolean checkCustomModel
	) throws UnknownEncodingException {
		input.readShort();
		String name = input.readJavaString();
		String alias = input.readString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = Ingredient.loadIngredient(input, this);
		
		// Use hardcoded 6 instead of variable because only 6 item flags existed in this encoding
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		int shearDurabilityLoss = input.readInt();
		
		List<PotionEffect> playerEffects = new ArrayList<PotionEffect>();
		int peLength = (input.readByte() & 0xFF);
		for (int index = 0; index < peLength; index++) {
			playerEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		List<PotionEffect> targetEffects = new ArrayList<PotionEffect>();
		int teLength = (input.readByte() & 0xFF);
		for (int index = 0; index < teLength; index++) {
			targetEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		Collection<EquippedPotionEffect> equippedEffects = CustomItem.readEquippedEffects(input);
		String[] commands = new String[input.readByte() & 0xFF];
		for (int index = 0; index < commands.length; index++) {
			commands[index] = input.readJavaString();
		}
		
		ReplaceCondition[] conditions = new ReplaceCondition[input.readByte() & 0xFF];
		for (int index = 0; index < conditions.length; index++) {
			conditions[index] = loadReplaceCondition(input);
		}
		ConditionOperation op = ConditionOperation.valueOf(input.readJavaString());
		ExtraItemNbt extraNbt = ExtraItemNbt.load(input);
		float attackRange = input.readFloat();
		
		String imageName = input.readJavaString();
		NamedImage texture = null;
		for (NamedImage current : textures) {
			if (current.getName().equals(imageName)) {
				texture = current;
				break;
			}
		}
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new CustomShears(
				name, alias, displayName, lore, attributes, defaultEnchantments, 
				durability, allowEnchanting, allowAnvil, repairItem, texture, 
				itemFlags, entityHitDurabilityLoss, blockBreakDurabilityLoss, 
				shearDurabilityLoss, customModel, playerEffects, targetEffects, 
				equippedEffects, commands, conditions, op, extraNbt, attackRange
		);
	}

	private CustomBow loadBow3(BitInput input, boolean checkCustomModel) throws UnknownEncodingException {
		input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		int durability = input.readInt();
		double damageMultiplier = input.readDouble();
		double speedMultiplier = input.readDouble();
		int knockbackStrength = input.readInt();
		boolean gravity = input.readBoolean();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = Ingredient.loadIngredient(input, this);
		String imageName = input.readJavaString();
		NamedImage texture = getTextureByName(imageName);
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new CustomBow(
				name, "", displayName, lore, attributes, new Enchantment[0], 
				durability, damageMultiplier, speedMultiplier, knockbackStrength, 
				gravity, allowEnchanting, allowAnvil, repairItem, 
				(BowTextures) texture, ItemFlag.getDefaultValues(), 0, 0, 1, 
				customModel, new ArrayList<>(), new ArrayList<>(), 
				new ArrayList<>(), new String[] {}, 
				new ReplaceCondition[0], ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}
	
	private CustomBow loadBow4(BitInput input, boolean checkCustomModel) throws UnknownEncodingException {
		input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		double damageMultiplier = input.readDouble();
		double speedMultiplier = input.readDouble();
		int knockbackStrength = input.readInt();
		boolean gravity = input.readBoolean();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = Ingredient.loadIngredient(input, this);
		String imageName = input.readJavaString();
		NamedImage texture = getTextureByName(imageName);
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new CustomBow(
				name, "", displayName, lore, attributes, defaultEnchantments, 
				durability, damageMultiplier, speedMultiplier, knockbackStrength, 
				gravity, allowEnchanting, allowAnvil, repairItem, 
				(BowTextures) texture, ItemFlag.getDefaultValues(), 0, 0, 1, 
				customModel, new ArrayList<>(), new ArrayList<>(), 
				new ArrayList<>(), new String[] {}, 
				new ReplaceCondition[0], ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}
	
	private CustomBow loadBow5(BitInput input, boolean checkCustomModel) throws UnknownEncodingException {
		input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		double damageMultiplier = input.readDouble();
		double speedMultiplier = input.readDouble();
		int knockbackStrength = input.readInt();
		boolean gravity = input.readBoolean();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = Ingredient.loadIngredient(input, this);
		
		// Use hardcoded 6 instead of variable because only 6 item flags existed in this encoding
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		int shootDurabilityLoss = input.readInt();
		
		String imageName = input.readJavaString();
		NamedImage texture = getTextureByName(imageName);
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new CustomBow(
				name, "", displayName, lore, attributes, defaultEnchantments, 
				durability, damageMultiplier, speedMultiplier, knockbackStrength, 
				gravity, allowEnchanting, allowAnvil, repairItem, 
				(BowTextures) texture, itemFlags, entityHitDurabilityLoss, 
				blockBreakDurabilityLoss, shootDurabilityLoss, customModel, 
				new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 
				new String[] {}, new ReplaceCondition[0], ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}
	
	private CustomBow loadBow6(BitInput input, boolean checkCustomModel) throws UnknownEncodingException {
		input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		double damageMultiplier = input.readDouble();
		double speedMultiplier = input.readDouble();
		int knockbackStrength = input.readInt();
		boolean gravity = input.readBoolean();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = Ingredient.loadIngredient(input, this);
		
		// Use hardcoded 6 instead of variable because only 6 item flags existed in this encoding
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		int shootDurabilityLoss = input.readInt();
		
		List<PotionEffect> playerEffects = new ArrayList<PotionEffect>();
		int peLength = (input.readByte() & 0xFF);
		for (int index = 0; index < peLength; index++) {
			playerEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		List<PotionEffect> targetEffects = new ArrayList<PotionEffect>();
		int teLength = (input.readByte() & 0xFF);
		for (int index = 0; index < teLength; index++) {
			targetEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		String[] commands = new String[input.readByte() & 0xFF];
		for (int index = 0; index < commands.length; index++) {
			commands[index] = input.readJavaString();
		}
		
		String imageName = input.readJavaString();
		NamedImage texture = null;
		for (NamedImage current : textures) {
			if (current.getName().equals(imageName)) {
				texture = current;
				break;
			}
		}
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new CustomBow(
				name, "", displayName, lore, attributes, defaultEnchantments, 
				durability, damageMultiplier, speedMultiplier, knockbackStrength, 
				gravity, allowEnchanting, allowAnvil, repairItem, 
				(BowTextures) texture, itemFlags, entityHitDurabilityLoss, 
				blockBreakDurabilityLoss, shootDurabilityLoss, customModel, 
				playerEffects, targetEffects, new ArrayList<>(),
				commands, new ReplaceCondition[0], ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}
	
	private CustomBow loadBow9(BitInput input, boolean checkCustomModel) throws UnknownEncodingException {
		input.readShort();
		String name = input.readJavaString();
		String alias = input.readString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		double damageMultiplier = input.readDouble();
		double speedMultiplier = input.readDouble();
		int knockbackStrength = input.readInt();
		boolean gravity = input.readBoolean();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = Ingredient.loadIngredient(input, this);
		
		// Use hardcoded 6 instead of variable because only 6 item flags existed in this encoding
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		int shootDurabilityLoss = input.readInt();
		
		List<PotionEffect> playerEffects = new ArrayList<PotionEffect>();
		int peLength = (input.readByte() & 0xFF);
		for (int index = 0; index < peLength; index++) {
			playerEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		List<PotionEffect> targetEffects = new ArrayList<PotionEffect>();
		int teLength = (input.readByte() & 0xFF);
		for (int index = 0; index < teLength; index++) {
			targetEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		Collection<EquippedPotionEffect> equippedEffects = CustomItem.readEquippedEffects(input);
		String[] commands = new String[input.readByte() & 0xFF];
		for (int index = 0; index < commands.length; index++) {
			commands[index] = input.readJavaString();
		}
		
		ReplaceCondition[] conditions = new ReplaceCondition[input.readByte() & 0xFF];
		for (int index = 0; index < conditions.length; index++) {
			conditions[index] = loadReplaceCondition(input);
		}
		ConditionOperation op = ConditionOperation.valueOf(input.readJavaString());
		ExtraItemNbt extraNbt = ExtraItemNbt.load(input);
		float attackRange = input.readFloat();
		
		String imageName = input.readJavaString();
		NamedImage texture = null;
		for (NamedImage current : textures) {
			if (current.getName().equals(imageName)) {
				texture = current;
				break;
			}
		}
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new CustomBow(
				name, alias, displayName, lore, attributes, defaultEnchantments, 
				durability, damageMultiplier, speedMultiplier, knockbackStrength, 
				gravity, allowEnchanting, allowAnvil, repairItem, 
				(BowTextures) texture, itemFlags, entityHitDurabilityLoss, 
				blockBreakDurabilityLoss, shootDurabilityLoss, customModel, 
				playerEffects, targetEffects, equippedEffects,
				commands, conditions, op, extraNbt, attackRange
		);
	}
	
	private CustomItem loadArmor4(BitInput input, boolean checkCustomModel) throws UnknownEncodingException {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = Ingredient.loadIngredient(input, this);
		int red;
		int green;
		int blue;
		if (itemType.isLeatherArmor()) {
			red = input.readByte() & 0xFF;
			green = input.readByte() & 0xFF;
			blue = input.readByte() & 0xFF;
		} else {
			red = 160;
			green = 101;
			blue = 64;
		}
		String imageName = input.readJavaString();
		NamedImage texture = getTextureByName(imageName);
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new CustomArmor(
				itemType, name, "", displayName, lore, attributes, 
				defaultEnchantments, durability, allowEnchanting, allowAnvil, 
				repairItem, texture, red, green, blue, ItemFlag.getDefaultValues(), 
				0, 0, new DamageResistances(), customModel, new ArrayList<>(), 
				new ArrayList<>(), new ArrayList<>(), new String[] {}, 
				new ReplaceCondition[0], ConditionOperation.NONE,
				new ExtraItemNbt(), null, 1f
		);
	}
	
	private CustomItem loadArmor5(BitInput input, boolean checkCustomModel) throws UnknownEncodingException {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = Ingredient.loadIngredient(input, this);
		int red;
		int green;
		int blue;
		if (itemType.isLeatherArmor()) {
			red = input.readByte() & 0xFF;
			green = input.readByte() & 0xFF;
			blue = input.readByte() & 0xFF;
		} else {
			red = 160;
			green = 101;
			blue = 64;
		}
		
		// Don't use ItemFlag.values().length because it only had 6 flags during the version it was saved
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		
		String imageName = input.readJavaString();
		NamedImage texture = getTextureByName(imageName);
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new CustomArmor(
				itemType, name, "", displayName, lore, attributes, 
				defaultEnchantments, durability, allowEnchanting, allowAnvil, 
				repairItem, texture, red, green, blue, itemFlags, 
				entityHitDurabilityLoss, blockBreakDurabilityLoss, 
				new DamageResistances(), customModel, new ArrayList<>(), 
				new ArrayList<>(), new ArrayList<>(), new String[] {}, 
				new ReplaceCondition[0], ConditionOperation.NONE,
				new ExtraItemNbt(), null, 1f
		);
	}
	
	private CustomItem loadArmor6(BitInput input, boolean checkCustomModel) throws UnknownEncodingException {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = Ingredient.loadIngredient(input, this);
		int red;
		int green;
		int blue;
		if (itemType.isLeatherArmor()) {
			red = input.readByte() & 0xFF;
			green = input.readByte() & 0xFF;
			blue = input.readByte() & 0xFF;
		} else {
			red = 160;
			green = 101;
			blue = 64;
		}
		
		// Don't use ItemFlag.values().length because it only had 6 flags during the version it was saved
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		
		DamageResistances resistances = DamageResistances.load12(input);
		
		String imageName = input.readJavaString();
		NamedImage texture = getTextureByName(imageName);
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new CustomArmor(
				itemType, name, "", displayName, lore, attributes, defaultEnchantments, 
				durability, allowEnchanting, allowAnvil, repairItem, texture, red, 
				green, blue, itemFlags, entityHitDurabilityLoss, 
				blockBreakDurabilityLoss, resistances, customModel, 
				new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 
				new String[] {}, new ReplaceCondition[0], ConditionOperation.NONE,
				new ExtraItemNbt(), null, 1f
		);
	}
	
	private CustomItem loadArmor7(BitInput input, boolean checkCustomModel) throws UnknownEncodingException {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = Ingredient.loadIngredient(input, this);
		int red;
		int green;
		int blue;
		if (itemType.isLeatherArmor()) {
			red = input.readByte() & 0xFF;
			green = input.readByte() & 0xFF;
			blue = input.readByte() & 0xFF;
		} else {
			red = 160;
			green = 101;
			blue = 64;
		}
		
		// Don't use ItemFlag.values().length because it only had 6 flags during the version it was saved
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		
		DamageResistances resistances = DamageResistances.load14(input);
		
		String imageName = input.readJavaString();
		NamedImage texture = getTextureByName(imageName);
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new CustomArmor(
				itemType, name, "", displayName, lore, attributes, 
				defaultEnchantments, durability, allowEnchanting, allowAnvil, 
				repairItem, texture, red, green, blue, itemFlags, 
				entityHitDurabilityLoss, blockBreakDurabilityLoss, resistances, 
				customModel, new ArrayList<>(), new ArrayList<>(),
				new ArrayList<>(), new String[] {}, 
				new ReplaceCondition[0], ConditionOperation.NONE,
				new ExtraItemNbt(), null, 1f
		);
	}
	
	private CustomItem loadArmor8(BitInput input, boolean checkCustomModel) throws UnknownEncodingException {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = Ingredient.loadIngredient(input, this);
		int red;
		int green;
		int blue;
		if (itemType.isLeatherArmor()) {
			red = input.readByte() & 0xFF;
			green = input.readByte() & 0xFF;
			blue = input.readByte() & 0xFF;
		} else {
			red = 160;
			green = 101;
			blue = 64;
		}
		
		// Don't use ItemFlag.values().length because it only had 6 flags during the version it was saved
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		
		DamageResistances resistances = DamageResistances.load14(input);
		
		List<PotionEffect> playerEffects = new ArrayList<PotionEffect>();
		int peLength = (input.readByte() & 0xFF);
		for (int index = 0; index < peLength; index++) {
			playerEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		List<PotionEffect> targetEffects = new ArrayList<PotionEffect>();
		int teLength = (input.readByte() & 0xFF);
		for (int index = 0; index < teLength; index++) {
			targetEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		String[] commands = new String[input.readByte() & 0xFF];
		for (int index = 0; index < commands.length; index++) {
			commands[index] = input.readJavaString();
		}
		
		String imageName = input.readJavaString();
		NamedImage texture = null;
		for (NamedImage current : textures) {
			if (current.getName().equals(imageName)) {
				texture = current;
				break;
			}
		}
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new CustomArmor(
				itemType, name, "", displayName, lore, attributes, 
				defaultEnchantments, durability, allowEnchanting, allowAnvil, 
				repairItem, texture, red, green, blue, itemFlags, 
				entityHitDurabilityLoss, blockBreakDurabilityLoss, resistances, 
				customModel, playerEffects, targetEffects, new ArrayList<>(), 
				commands, new ReplaceCondition[0], ConditionOperation.NONE,
				new ExtraItemNbt(), null, 1f
		);
	}
	
	private CustomItem loadArmor9(BitInput input, boolean checkCustomModel) throws UnknownEncodingException {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		input.readShort();
		String name = input.readJavaString();
		String alias = input.readString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = Ingredient.loadIngredient(input, this);
		int red;
		int green;
		int blue;
		if (itemType.isLeatherArmor()) {
			red = input.readByte() & 0xFF;
			green = input.readByte() & 0xFF;
			blue = input.readByte() & 0xFF;
		} else {
			red = 160;
			green = 101;
			blue = 64;
		}
		
		// Don't use ItemFlag.values().length because it only had 6 flags during the version it was saved
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		
		DamageResistances resistances = DamageResistances.load14(input);
		
		List<PotionEffect> playerEffects = new ArrayList<PotionEffect>();
		int peLength = (input.readByte() & 0xFF);
		for (int index = 0; index < peLength; index++) {
			playerEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		List<PotionEffect> targetEffects = new ArrayList<PotionEffect>();
		int teLength = (input.readByte() & 0xFF);
		for (int index = 0; index < teLength; index++) {
			targetEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		Collection<EquippedPotionEffect> equippedEffects = CustomItem.readEquippedEffects(input);
		String[] commands = new String[input.readByte() & 0xFF];
		for (int index = 0; index < commands.length; index++) {
			commands[index] = input.readJavaString();
		}
		
		ReplaceCondition[] conditions = new ReplaceCondition[input.readByte() & 0xFF];
		for (int index = 0; index < conditions.length; index++) {
			conditions[index] = loadReplaceCondition(input);
		}
		ConditionOperation op = ConditionOperation.valueOf(input.readJavaString());
		ExtraItemNbt extraNbt = ExtraItemNbt.load(input);
		ReadOnlyReference<ArmorTextures> wornTexture;
		if (input.readBoolean()) {
			wornTexture = getArmorTexture(input.readString());
		} else {
			wornTexture = null;
		}
		float attackRange = input.readFloat();
		
		String imageName = input.readJavaString();
		NamedImage texture = null;
		for (NamedImage current : textures) {
			if (current.getName().equals(imageName)) {
				texture = current;
				break;
			}
		}
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new CustomArmor(
				itemType, name, alias, displayName, lore, attributes, 
				defaultEnchantments, durability, allowEnchanting, allowAnvil, 
				repairItem, texture, red, green, blue, itemFlags, 
				entityHitDurabilityLoss, blockBreakDurabilityLoss, resistances, 
				customModel, playerEffects, targetEffects, equippedEffects, 
				commands, conditions, op, extraNbt, wornTexture, attackRange
		);
	}
	
	private CustomItem loadHelmet3d9(BitInput input, boolean checkCustomModel) throws UnknownEncodingException {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		input.readShort();
		String name = input.readJavaString();
		String alias = input.readString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = Ingredient.loadIngredient(input, this);
		
		// Don't use ItemFlag.values().length because it only had 6 flags during the version it was saved
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		
		DamageResistances resistances = DamageResistances.load14(input);
		
		List<PotionEffect> playerEffects = new ArrayList<PotionEffect>();
		int peLength = (input.readByte() & 0xFF);
		for (int index = 0; index < peLength; index++) {
			playerEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		List<PotionEffect> targetEffects = new ArrayList<PotionEffect>();
		int teLength = (input.readByte() & 0xFF);
		for (int index = 0; index < teLength; index++) {
			targetEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		Collection<EquippedPotionEffect> equippedEffects = CustomItem.readEquippedEffects(input);
		String[] commands = new String[input.readByte() & 0xFF];
		for (int index = 0; index < commands.length; index++) {
			commands[index] = input.readJavaString();
		}
		
		ReplaceCondition[] conditions = new ReplaceCondition[input.readByte() & 0xFF];
		for (int index = 0; index < conditions.length; index++) {
			conditions[index] = loadReplaceCondition(input);
		}
		ConditionOperation op = ConditionOperation.valueOf(input.readJavaString());
		ExtraItemNbt extraNbt = ExtraItemNbt.load(input);
		// Discard worn texture flag, which is always false for 3d helmets
		input.readBoolean();
		float attackRange = input.readFloat();
		
		String imageName = input.readJavaString();
		NamedImage texture = null;
		for (NamedImage current : textures) {
			if (current.getName().equals(imageName)) {
				texture = current;
				break;
			}
		}
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new CustomHelmet3D(
				itemType, name, alias, displayName, lore, attributes, 
				defaultEnchantments, durability, allowEnchanting, allowAnvil, 
				repairItem, texture, itemFlags, entityHitDurabilityLoss, 
				blockBreakDurabilityLoss, resistances, customModel, playerEffects, 
				targetEffects, equippedEffects, commands, conditions, op, extraNbt,
				attackRange
		);
	}
	
	private CustomItem loadShield6(BitInput input, boolean checkCustomModel) throws UnknownEncodingException {
		CustomItemType.valueOf(input.readJavaString());
		input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = Ingredient.loadIngredient(input, this);
		
		// Use hardcoded 6 instead of variable because only 6 item flags existed in this encoding
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		double thresholdDamage = input.readDouble();
		
		String imageName = input.readJavaString();
		NamedImage texture = getTextureByName(imageName);
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		byte[] customBlockingModel = loadCustomModel(input, checkCustomModel);
		return new CustomShield(
				name, "", displayName, lore, attributes, defaultEnchantments, 
				durability, allowEnchanting, allowAnvil, repairItem, texture, 
				itemFlags, entityHitDurabilityLoss, blockBreakDurabilityLoss, 
				thresholdDamage, customModel, customBlockingModel, 
				new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 
				new String[] {}, new ReplaceCondition[0], ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}
	
	private CustomItem loadShield7(BitInput input, boolean checkCustomModel) throws UnknownEncodingException {
		CustomItemType.valueOf(input.readJavaString());
		input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = Ingredient.loadIngredient(input, this);
		
		// Use hardcoded 6 instead of variable because only 6 item flags existed in this encoding
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		double thresholdDamage = input.readDouble();
		
		List<PotionEffect> playerEffects = new ArrayList<PotionEffect>();
		int peLength = (input.readByte() & 0xFF);
		for (int index = 0; index < peLength; index++) {
			playerEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		List<PotionEffect> targetEffects = new ArrayList<PotionEffect>();
		int teLength = (input.readByte() & 0xFF);
		for (int index = 0; index < teLength; index++) {
			targetEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		String[] commands = new String[input.readByte() & 0xFF];
		for (int index = 0; index < commands.length; index++) {
			commands[index] = input.readJavaString();
		}
		
		String imageName = input.readJavaString();
		NamedImage texture = null;
		for (NamedImage current : textures) {
			if (current.getName().equals(imageName)) {
				texture = current;
				break;
			}
		}
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		byte[] customBlockingModel = loadCustomModel(input, checkCustomModel);
		return new CustomShield(
				name, "", displayName, lore, attributes, defaultEnchantments, 
				durability, allowEnchanting, allowAnvil, repairItem, texture, 
				itemFlags, entityHitDurabilityLoss, blockBreakDurabilityLoss, 
				thresholdDamage, customModel, customBlockingModel, playerEffects, 
				targetEffects, new ArrayList<>(), commands, 
				new ReplaceCondition[0], ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}
	
	private CustomItem loadShield9(BitInput input, boolean checkCustomModel) throws UnknownEncodingException {
		input.readShort();
		String name = input.readJavaString();
		String alias = input.readString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = Ingredient.loadIngredient(input, this);
		
		// Use hardcoded 6 instead of variable because only 6 item flags existed in this encoding
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		double thresholdDamage = input.readDouble();
		
		List<PotionEffect> playerEffects = new ArrayList<PotionEffect>();
		int peLength = (input.readByte() & 0xFF);
		for (int index = 0; index < peLength; index++) {
			playerEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		List<PotionEffect> targetEffects = new ArrayList<PotionEffect>();
		int teLength = (input.readByte() & 0xFF);
		for (int index = 0; index < teLength; index++) {
			targetEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		Collection<EquippedPotionEffect> equippedEffects = CustomItem.readEquippedEffects(input);
		String[] commands = new String[input.readByte() & 0xFF];
		for (int index = 0; index < commands.length; index++) {
			commands[index] = input.readJavaString();
		}
		
		ReplaceCondition[] conditions = new ReplaceCondition[input.readByte() & 0xFF];
		for (int index = 0; index < conditions.length; index++) {
			conditions[index] = loadReplaceCondition(input);
		}
		ConditionOperation op = ConditionOperation.valueOf(input.readJavaString());
		ExtraItemNbt extraNbt = ExtraItemNbt.load(input);
		float attackRange = input.readFloat();
		
		String imageName = input.readJavaString();
		NamedImage texture = null;
		for (NamedImage current : textures) {
			if (current.getName().equals(imageName)) {
				texture = current;
				break;
			}
		}
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		byte[] customBlockingModel = loadCustomModel(input, checkCustomModel);
		return new CustomShield(
				name, alias, displayName, lore, attributes, defaultEnchantments, 
				durability, allowEnchanting, allowAnvil, repairItem, texture, 
				itemFlags, entityHitDurabilityLoss, blockBreakDurabilityLoss, 
				thresholdDamage, customModel, customBlockingModel, 
				playerEffects, targetEffects, equippedEffects, 
				commands, conditions, op, extraNbt, attackRange
		);
	}
	
	private CustomItem loadTrident7(BitInput input, boolean checkCustomModel) throws UnknownEncodingException {
		input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = Ingredient.loadIngredient(input, this);
		
		// Use hardcoded 6 instead of variable because only 6 item flags existed in this encoding
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		int throwDurabilityLoss = input.readInt();
		double throwDamageMultiplier = input.readDouble();
		double speedMultiplier = input.readDouble();
		
		String imageName = input.readJavaString();
		NamedImage texture = getTextureByName(imageName);
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		byte[] customInHandModel = loadCustomModel(input, checkCustomModel);
		byte[] customThrowingModel = loadCustomModel(input, checkCustomModel);
		return new CustomTrident(
				name, "", displayName, lore, attributes, defaultEnchantments, 
				durability, allowEnchanting, allowAnvil, throwDamageMultiplier, 
				speedMultiplier, repairItem, texture, itemFlags, 
				entityHitDurabilityLoss, blockBreakDurabilityLoss, 
				throwDurabilityLoss, customModel, customInHandModel, 
				customThrowingModel, new ArrayList<>(), new ArrayList<>(),
				new ArrayList<>(), new String[] {}, 
				new ReplaceCondition[0], ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}
	
	private CustomItem loadTrident8(BitInput input, boolean checkCustomModel) throws UnknownEncodingException {
		input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = Ingredient.loadIngredient(input, this);
		
		// Use hardcoded 6 instead of variable because only 6 item flags existed in this encoding
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		int throwDurabilityLoss = input.readInt();
		double throwDamageMultiplier = input.readDouble();
		double speedMultiplier = input.readDouble();
		
		List<PotionEffect> playerEffects = new ArrayList<PotionEffect>();
		int peLength = (input.readByte() & 0xFF);
		for (int index = 0; index < peLength; index++) {
			playerEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		List<PotionEffect> targetEffects = new ArrayList<PotionEffect>();
		int teLength = (input.readByte() & 0xFF);
		for (int index = 0; index < teLength; index++) {
			targetEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		String[] commands = new String[input.readByte() & 0xFF];
		for (int index = 0; index < commands.length; index++) {
			commands[index] = input.readJavaString();
		}
		
		String imageName = input.readJavaString();
		NamedImage texture = null;
		for (NamedImage current : textures) {
			if (current.getName().equals(imageName)) {
				texture = current;
				break;
			}
		}
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		byte[] customInHandModel = loadCustomModel(input, checkCustomModel);
		byte[] customThrowingModel = loadCustomModel(input, checkCustomModel);
		return new CustomTrident(
				name, "", displayName, lore, attributes, defaultEnchantments, 
				durability, allowEnchanting, allowAnvil, throwDamageMultiplier, 
				speedMultiplier, repairItem, texture, itemFlags, 
				entityHitDurabilityLoss, blockBreakDurabilityLoss, 
				throwDurabilityLoss, customModel, customInHandModel, 
				customThrowingModel, playerEffects, targetEffects, new ArrayList<>(),
				commands, new ReplaceCondition[0], ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}
	
	private CustomItem loadTrident9(BitInput input, boolean checkCustomModel) throws UnknownEncodingException {
		input.readShort();
		String name = input.readJavaString();
		String alias = input.readString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = Ingredient.loadIngredient(input, this);
		
		// Use hardcoded 6 instead of variable because only 6 item flags existed in this encoding
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		int throwDurabilityLoss = input.readInt();
		double throwDamageMultiplier = input.readDouble();
		double speedMultiplier = input.readDouble();
		
		List<PotionEffect> playerEffects = new ArrayList<PotionEffect>();
		int peLength = (input.readByte() & 0xFF);
		for (int index = 0; index < peLength; index++) {
			playerEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		List<PotionEffect> targetEffects = new ArrayList<PotionEffect>();
		int teLength = (input.readByte() & 0xFF);
		for (int index = 0; index < teLength; index++) {
			targetEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		Collection<EquippedPotionEffect> equippedEffects = CustomItem.readEquippedEffects(input);
		String[] commands = new String[input.readByte() & 0xFF];
		for (int index = 0; index < commands.length; index++) {
			commands[index] = input.readJavaString();
		}
		
		ReplaceCondition[] conditions = new ReplaceCondition[input.readByte() & 0xFF];
		for (int index = 0; index < conditions.length; index++) {
			conditions[index] = loadReplaceCondition(input);
		}
		ConditionOperation op = ConditionOperation.valueOf(input.readJavaString());
		ExtraItemNbt extraNbt = ExtraItemNbt.load(input);
		float attackRange = input.readFloat();
		
		String imageName = input.readJavaString();
		NamedImage texture = null;
		for (NamedImage current : textures) {
			if (current.getName().equals(imageName)) {
				texture = current;
				break;
			}
		}
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		byte[] customInHandModel = loadCustomModel(input, checkCustomModel);
		byte[] customThrowingModel = loadCustomModel(input, checkCustomModel);
		return new CustomTrident(
				name, alias, displayName, lore, attributes, defaultEnchantments, 
				durability, allowEnchanting, allowAnvil, throwDamageMultiplier, 
				speedMultiplier, repairItem, texture, itemFlags, 
				entityHitDurabilityLoss, blockBreakDurabilityLoss, 
				throwDurabilityLoss, customModel, customInHandModel, 
				customThrowingModel, playerEffects, targetEffects, equippedEffects, 
				commands, conditions, op, extraNbt, attackRange
		);
	}
	
	private CustomItem loadWand8(BitInput input) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		
		// Use hardcoded 6 instead of variable because only 6 item flags existed in this encoding
		boolean[] itemFlags = input.readBooleans(6);
		
		List<PotionEffect> playerEffects = new ArrayList<PotionEffect>();
		int peLength = (input.readByte() & 0xFF);
		for (int index = 0; index < peLength; index++) {
			playerEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		List<PotionEffect> targetEffects = new ArrayList<PotionEffect>();
		int teLength = (input.readByte() & 0xFF);
		for (int index = 0; index < teLength; index++) {
			targetEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		String[] commands = new String[input.readByte() & 0xFF];
		for (int index = 0; index < commands.length; index++) {
			commands[index] = input.readJavaString();
		}
		
		CIProjectile projectile = getProjectileByName(input.readString());
		int cooldown = input.readInt();
		WandCharges charges;
		if (input.readBoolean()) {
			charges = WandCharges.fromBits(input);
		} else {
			charges = null;
		}
		int amountPerShot = input.readInt();
		
		String imageName = input.readJavaString();
		NamedImage texture = null;
		for (NamedImage current : textures) {
			if (current.getName().equals(imageName)) {
				texture = current;
				break;
			}
		}
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, true);
		return new CustomWand(
				itemType, name, "", displayName, lore, attributes, 
				defaultEnchantments, texture, itemFlags, customModel, 
				playerEffects, targetEffects, new ArrayList<>(), commands, 
				new ReplaceCondition[0], ConditionOperation.NONE, projectile, 
				cooldown, charges, amountPerShot, new ExtraItemNbt(), 1f
		);
	}
	
	private CustomItem loadWand9(BitInput input) throws UnknownEncodingException {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		input.readShort();
		String name = input.readJavaString();
		String alias = input.readString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		
		// Use hardcoded 6 instead of variable because only 6 item flags existed in this encoding
		boolean[] itemFlags = input.readBooleans(6);
		
		List<PotionEffect> playerEffects = new ArrayList<PotionEffect>();
		int peLength = (input.readByte() & 0xFF);
		for (int index = 0; index < peLength; index++) {
			playerEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		List<PotionEffect> targetEffects = new ArrayList<PotionEffect>();
		int teLength = (input.readByte() & 0xFF);
		for (int index = 0; index < teLength; index++) {
			targetEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		Collection<EquippedPotionEffect> equippedEffects = CustomItem.readEquippedEffects(input);
		String[] commands = new String[input.readByte() & 0xFF];
		for (int index = 0; index < commands.length; index++) {
			commands[index] = input.readJavaString();
		}
		
		ReplaceCondition[] conditions = new ReplaceCondition[input.readByte() & 0xFF];
		for (int index = 0; index < conditions.length; index++) {
			conditions[index] = loadReplaceCondition(input);
		}
		ConditionOperation op = ConditionOperation.valueOf(input.readJavaString());
		
		CIProjectile projectile = getProjectileByName(input.readString());
		int cooldown = input.readInt();
		WandCharges charges;
		if (input.readBoolean()) {
			charges = WandCharges.fromBits(input);
		} else {
			charges = null;
		}
		int amountPerShot = input.readInt();
		ExtraItemNbt extraNbt = ExtraItemNbt.load(input);
		float attackRange = input.readFloat();
		
		String imageName = input.readJavaString();
		NamedImage texture = null;
		for (NamedImage current : textures) {
			if (current.getName().equals(imageName)) {
				texture = current;
				break;
			}
		}
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, true);
		return new CustomWand(
				itemType, name, alias, displayName, lore, attributes, 
				defaultEnchantments, texture, itemFlags, customModel, playerEffects, 
				targetEffects, equippedEffects, commands, conditions, op,
				projectile, cooldown, charges, amountPerShot, extraNbt,
				attackRange
		);
	}

	private CustomItem loadPocketContainer9(
			BitInput input, boolean checkCustomModel
	) throws UnknownEncodingException {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		input.readShort();
		String name = input.readJavaString();
		String alias = input.readString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++) {
			lore[index] = input.readJavaString();
		}
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());

		// Use hardcoded 6 instead of variable because only 6 item flags existed in this encoding
		boolean[] itemFlags = input.readBooleans(6);

		List<PotionEffect> playerEffects = new ArrayList<PotionEffect>();
		int peLength = (input.readByte() & 0xFF);
		for (int index = 0; index < peLength; index++) {
			playerEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		List<PotionEffect> targetEffects = new ArrayList<PotionEffect>();
		int teLength = (input.readByte() & 0xFF);
		for (int index = 0; index < teLength; index++) {
			targetEffects.add(new PotionEffect(EffectType.valueOf(input.readJavaString()), input.readInt(), input.readInt()));
		}
		Collection<EquippedPotionEffect> equippedEffects = CustomItem.readEquippedEffects(input);
		String[] commands = new String[input.readByte() & 0xFF];
		for (int index = 0; index < commands.length; index++) {
			commands[index] = input.readJavaString();
		}

		ReplaceCondition[] conditions = new ReplaceCondition[input.readByte() & 0xFF];
		for (int index = 0; index < conditions.length; index++) {
			conditions[index] = loadReplaceCondition(input);
		}
		ConditionOperation op = ConditionOperation.valueOf(input.readJavaString());
		ExtraItemNbt extraNbt = ExtraItemNbt.load(input);
		float attackRange = input.readFloat();

		int numContainers = input.readInt();
		Collection<String> containerNames = new ArrayList<>(numContainers);
		for (int counter = 0; counter < numContainers; counter++) {
			containerNames.add(input.readString());
		}

		String imageName = input.readJavaString();
		NamedImage texture = null;
		for (NamedImage current : textures) {
			if (current.getName().equals(imageName)) {
				texture = current;
				break;
			}
		}
		if (texture == null)
			throw new IllegalArgumentException("Can't find texture " + imageName);
		byte[] customModel = loadCustomModel(input, checkCustomModel);
		return new CustomPocketContainer(
				itemType, name, alias, displayName, lore, attributes,
				defaultEnchantments, texture, itemFlags, customModel,
				playerEffects, targetEffects, equippedEffects,
				commands, conditions, op, extraNbt, attackRange,
				containerNames, null
		);
	}

	private AttributeModifier loadAttribute2(BitInput input) {
		return new AttributeModifier(Attribute.valueOf(input.readJavaString()), Slot.valueOf(input.readJavaString()),
				Operation.values()[(int) input.readNumber((byte) 2, false)], input.readDouble());
	}
	
	private ReplaceCondition loadReplaceCondition(BitInput input) {
		return new ReplaceCondition(ReplacementCondition.valueOf(input.readJavaString()), input.readJavaString(), 
				ReplacementOperation.valueOf(input.readJavaString()), input.readInt(), input.readJavaString());
	}

	private boolean bypassChecks() {
		WindowInput input = Editor.getWindow().getInput();
		return input.isKeyDown(KeyCode.KEY_CONTROL) && input.isKeyDown(KeyCode.KEY_SHIFT);
	}

	private final String fileName;

	private Collection<NamedImage> textures;
	private Collection<CustomItem> items;
	private Collection<Recipe> recipes;
	private Collection<BlockDrop> blockDrops;
	private Collection<EntityDrop> mobDrops;
	private Collection<EditorProjectileCover> projectileCovers;
	private Collection<CIProjectile> projectiles;
	private Collection<CustomFuelRegistry> fuelRegistries;
	private Collection<CustomContainer> containers;
	private Collection<String> deletedItems;
	private Collection<Reference<ArmorTextures>> armorTextures;

	public ItemSet(String fileName) {
		this.fileName = fileName;
		textures = new ArrayList<>();
		items = new ArrayList<>();
		recipes = new ArrayList<>();
		blockDrops = new ArrayList<>();
		mobDrops = new ArrayList<>();
		projectileCovers = new ArrayList<>();
		projectiles = new ArrayList<>();
		fuelRegistries = new ArrayList<>();
		containers = new ArrayList<>();
		deletedItems = new ArrayList<>();
		armorTextures = new ArrayList<>();
	}

	public ItemSet(String fileName, BitInput input) 
			throws UnknownEncodingException, IntegrityException, IOException {
		this.fileName = fileName;
		byte encoding = input.readByte();
		if (encoding == ENCODING_1)
			load1(input);
		else if (encoding == ENCODING_2)
			load2(input);
		else if (encoding == ENCODING_3)
			load3(input);
		else if (encoding == ENCODING_4)
			load4(input);
		else if (encoding == ENCODING_5)
			load5(input);
		else if (encoding == ENCODING_6)
			load6(input);
		else if (encoding == ENCODING_7)
			load7(input);
		else if (encoding == ENCODING_8)
			load8(input);
		else
			throw new UnknownEncodingException("ItemSet", encoding);
	}

	private String checkName(String name) {
		if (name == null)
			return "The name can't be null";
		if (name.isEmpty())
			return "You can't leave the name empty.";
		for (int index = 0; index < name.length(); index++) {
			char c = name.charAt(index);
			if (c >= 'A' && c <= 'Z')
				return "Uppercase characters are not allowed in names.";
			if ((c < 'a' || c > 'z') && c != '_' && (c < '0' || c > '9'))
				return "The _ character is the only special character that is allowed in names.";
		}
		return null;
	}

	private void load1(BitInput input) throws UnknownEncodingException, IOException {
		// Textures
		int textureAmount = input.readInt();
		textures = new ArrayList<NamedImage>(textureAmount);
		for (int counter = 0; counter < textureAmount; counter++)
			textures.add(new NamedImage(input, false));

		// Items
		int itemAmount = input.readInt();
		items = new ArrayList<CustomItem>(itemAmount);
		for (int counter = 0; counter < itemAmount; counter++)
			items.add(loadItem(input, false));

		// Recipes
		int recipeAmount = input.readInt();
		recipes = new ArrayList<Recipe>(recipeAmount);
		for (int counter = 0; counter < recipeAmount; counter++)
			recipes.add(loadRecipe(input));
		
		// Drops (there are no drops in this encoding)
		blockDrops = new ArrayList<>();
		mobDrops = new ArrayList<>();
		
		// Projectiles (there are no projectiles in this encoding)
		projectileCovers = new ArrayList<>();
		projectiles = new ArrayList<>();
		
		// Containers (there are no containers in this encoding)
		fuelRegistries = new ArrayList<>();
		containers = new ArrayList<>();
		
		// Deleted item names aren't remembered in this encoding
		deletedItems = new ArrayList<>();
		
		// There are no armor textures in this encoding
		armorTextures = new ArrayList<>();
	}

	private void load2(BitInput input) throws UnknownEncodingException, IOException {
		// Textures
		int textureAmount = input.readInt();
		textures = new ArrayList<NamedImage>(textureAmount);
		for (int counter = 0; counter < textureAmount; counter++) {
			byte textureType = input.readByte();
			if (textureType == NamedImage.ENCODING_BOW)
				textures.add(new BowTextures(input, false));
			else if (textureType == NamedImage.ENCODING_SIMPLE)
				textures.add(new NamedImage(input, false));
			else
				throw new UnknownEncodingException("Texture", textureType);
		}

		// Items
		int itemAmount = input.readInt();
		items = new ArrayList<CustomItem>(itemAmount);
		for (int counter = 0; counter < itemAmount; counter++)
			items.add(loadItem(input, false));

		// Recipes
		int recipeAmount = input.readInt();
		recipes = new ArrayList<Recipe>(recipeAmount);
		for (int counter = 0; counter < recipeAmount; counter++)
			recipes.add(loadRecipe(input));
		
		// Drops (there are no drops in this encoding)
		blockDrops = new ArrayList<>();
		mobDrops = new ArrayList<>();
		
		// Projectiles (there are no projectiles in this encoding)
		projectileCovers = new ArrayList<>();
		projectiles = new ArrayList<>();
		
		// Containers (there are no containers in this encoding)
		fuelRegistries = new ArrayList<>();
		containers = new ArrayList<>();
		
		// Deleted item names aren't remembered in this encoding
		deletedItems = new ArrayList<>();
		
		// There are no armor textures in this encoding
		armorTextures = new ArrayList<>();
	}
	
	private void load3(BitInput input) throws UnknownEncodingException, IOException {
		// Textures
		int textureAmount = input.readInt();
		textures = new ArrayList<NamedImage>(textureAmount);
		for (int counter = 0; counter < textureAmount; counter++) {
			byte textureType = input.readByte();
			if (textureType == NamedImage.ENCODING_BOW)
				textures.add(new BowTextures(input, false));
			else if (textureType == NamedImage.ENCODING_SIMPLE)
				textures.add(new NamedImage(input, false));
			else
				throw new UnknownEncodingException("Texture", textureType);
		}

		// Items
		int itemAmount = input.readInt();
		items = new ArrayList<CustomItem>(itemAmount);
		for (int counter = 0; counter < itemAmount; counter++)
			items.add(loadItem(input, false));

		// Recipes
		int recipeAmount = input.readInt();
		recipes = new ArrayList<Recipe>(recipeAmount);
		for (int counter = 0; counter < recipeAmount; counter++)
			recipes.add(loadRecipe(input));
		
		// Drops
		int numBlockDrops = input.readInt();
		blockDrops = new ArrayList<>(numBlockDrops);
		for (int counter = 0; counter < numBlockDrops; counter++) {
			blockDrops.add(BlockDrop.load(
					input, this::createCustomItemResult, 
					() -> Recipe.loadResult(input, this), this::getCustomItemByName
			));
		}
		
		int numMobDrops = input.readInt();
		mobDrops = new ArrayList<>(numMobDrops);
		for (int counter = 0; counter < numMobDrops; counter++) {
			mobDrops.add(EntityDrop.load(
					input, this::createCustomItemResult, 
					() -> Recipe.loadResult(input, this), this::getCustomItemByName
			));
		}
		
		// Projectiles (there are no projectiles in this encoding)
		projectileCovers = new ArrayList<>();
		projectiles = new ArrayList<>();
		
		// Containers (there are no containers in this encoding)
		fuelRegistries = new ArrayList<>();
		containers = new ArrayList<>();
		
		// Deleted item names aren't remembered in this encoding
		deletedItems = new ArrayList<>();
		
		// There are no armor textures in this encoding
		armorTextures = new ArrayList<>();
	}
	
	private void load4(BitInput input) throws UnknownEncodingException, IOException {
		// Textures
		int textureAmount = input.readInt();
		textures = new ArrayList<NamedImage>(textureAmount);
		for (int counter = 0; counter < textureAmount; counter++) {
			byte textureType = input.readByte();
			if (textureType == NamedImage.ENCODING_BOW)
				textures.add(new BowTextures(input, false));
			else if (textureType == NamedImage.ENCODING_SIMPLE)
				textures.add(new NamedImage(input, false));
			else
				throw new UnknownEncodingException("Texture", textureType);
		}

		// Items
		int itemAmount = input.readInt();
		items = new ArrayList<CustomItem>(itemAmount);
		for (int counter = 0; counter < itemAmount; counter++)
			items.add(loadItem(input, true));

		// Recipes
		int recipeAmount = input.readInt();
		recipes = new ArrayList<Recipe>(recipeAmount);
		for (int counter = 0; counter < recipeAmount; counter++)
			recipes.add(loadRecipe(input));
		
		// Drops
		int numBlockDrops = input.readInt();
		blockDrops = new ArrayList<>(numBlockDrops);
		for (int counter = 0; counter < numBlockDrops; counter++) {
			blockDrops.add(BlockDrop.load(
					input, this::createCustomItemResult, 
					() -> Recipe.loadResult(input, this), this::getCustomItemByName
			));
		}
		
		int numMobDrops = input.readInt();
		mobDrops = new ArrayList<>(numMobDrops);
		for (int counter = 0; counter < numMobDrops; counter++) {
			mobDrops.add(EntityDrop.load(
					input, this::createCustomItemResult, 
					() -> Recipe.loadResult(input, this), this::getCustomItemByName
			));
		}
		
		// Projectiles (there are no projectiles in this encoding)
		projectileCovers = new ArrayList<>();
		projectiles = new ArrayList<>();
		
		// Containers (there are no containers in this encoding)
		fuelRegistries = new ArrayList<>();
		containers = new ArrayList<>();
		
		// Deleted item names aren't remembered in this encoding
		deletedItems = new ArrayList<>();
		
		// There are no armor textures in this encoding
		armorTextures = new ArrayList<>();
	}
	
	private void load5(BitInput input) throws UnknownEncodingException, IOException {
		// Textures
		int textureAmount = input.readInt();
		textures = new ArrayList<NamedImage>(textureAmount);
		for (int counter = 0; counter < textureAmount; counter++) {
			byte textureType = input.readByte();
			if (textureType == NamedImage.ENCODING_BOW)
				textures.add(new BowTextures(input, false));
			else if (textureType == NamedImage.ENCODING_SIMPLE)
				textures.add(new NamedImage(input, false));
			else
				throw new UnknownEncodingException("Texture", textureType);
		}
		
		// Projectile covers
		int numProjectileCovers = input.readInt();
		projectileCovers = new ArrayList<>(numProjectileCovers);
		for (int counter = 0; counter < numProjectileCovers; counter++)
			projectileCovers.add(EditorProjectileCover.fromBits(input, this));
		
		// Projectiles
		int numProjectiles = input.readInt();
		projectiles = new ArrayList<>(numProjectiles);
		for (int counter = 0; counter < numProjectiles; counter++)
			projectiles.add(CIProjectile.fromBits(input, this));
		
		// Notify the projectile effects that all projectiles have been loaded
		for (CIProjectile projectile : projectiles)
			projectile.afterProjectilesAreLoaded(this);

		// Items
		int itemAmount = input.readInt();
		items = new ArrayList<CustomItem>(itemAmount);
		for (int counter = 0; counter < itemAmount; counter++)
			items.add(loadItem(input, true));

		// Recipes
		int recipeAmount = input.readInt();
		recipes = new ArrayList<Recipe>(recipeAmount);
		for (int counter = 0; counter < recipeAmount; counter++)
			recipes.add(loadRecipe(input));
		
		// Drops
		int numBlockDrops = input.readInt();
		blockDrops = new ArrayList<>(numBlockDrops);
		for (int counter = 0; counter < numBlockDrops; counter++) {
			blockDrops.add(BlockDrop.load(
					input, this::createCustomItemResult, 
					() -> Recipe.loadResult(input, this), this::getCustomItemByName
			));
		}
		
		int numMobDrops = input.readInt();
		mobDrops = new ArrayList<>(numMobDrops);
		for (int counter = 0; counter < numMobDrops; counter++) {
			mobDrops.add(EntityDrop.load(
					input, this::createCustomItemResult, 
					() -> Recipe.loadResult(input, this), this::getCustomItemByName
			));
		}
		
		// Containers (there are no containers in this encoding)
		fuelRegistries = new ArrayList<>();
		containers = new ArrayList<>();
		
		// Deleted item names aren't remembered in this encoding
		deletedItems = new ArrayList<>();
		
		// There are no armor textures in this encoding
		armorTextures = new ArrayList<>();
	}
	
	private void load6(BitInput rawInput) 
			throws UnknownEncodingException, IntegrityException, IOException {
		// Check integrity
		long expectedHash = rawInput.readLong();
		byte[] remaining;
		try {
			// Catch undefined behavior when the remaining size is wrong
			remaining = rawInput.readByteArray();
		} catch (Throwable t) {
			throw new IntegrityException(t);
		}
		long actualHash = hash(remaining);
		if (expectedHash != actualHash)
			throw new IntegrityException(expectedHash, actualHash);
		
		BitInput input = new ByteArrayBitInput(remaining);
		
		// Textures
		int textureAmount = input.readInt();
		textures = new ArrayList<NamedImage>(textureAmount);
		for (int counter = 0; counter < textureAmount; counter++) {
			byte textureType = input.readByte();
			if (textureType == NamedImage.ENCODING_BOW)
				textures.add(new BowTextures(input, false));
			else if (textureType == NamedImage.ENCODING_SIMPLE)
				textures.add(new NamedImage(input, false));
			else
				throw new UnknownEncodingException("Texture", textureType);
		}
		
		// Projectile covers
		int numProjectileCovers = input.readInt();
		projectileCovers = new ArrayList<>(numProjectileCovers);
		for (int counter = 0; counter < numProjectileCovers; counter++)
			projectileCovers.add(EditorProjectileCover.fromBits(input, this));
		
		// Projectiles
		int numProjectiles = input.readInt();
		projectiles = new ArrayList<>(numProjectiles);
		for (int counter = 0; counter < numProjectiles; counter++)
			projectiles.add(CIProjectile.fromBits(input, this));
		
		// Notify the projectile effects that all projectiles have been loaded
		for (CIProjectile projectile : projectiles)
			projectile.afterProjectilesAreLoaded(this);

		// Items
		int itemAmount = input.readInt();
		items = new ArrayList<CustomItem>(itemAmount);
		for (int counter = 0; counter < itemAmount; counter++)
			items.add(loadItem(input, true));

		// Recipes
		int recipeAmount = input.readInt();
		recipes = new ArrayList<Recipe>(recipeAmount);
		for (int counter = 0; counter < recipeAmount; counter++)
			recipes.add(loadRecipe(input));
		
		// Drops
		int numBlockDrops = input.readInt();
		blockDrops = new ArrayList<>(numBlockDrops);
		for (int counter = 0; counter < numBlockDrops; counter++) {
			blockDrops.add(BlockDrop.load(
					input, this::createCustomItemResult, 
					() -> Recipe.loadResult(input, this), this::getCustomItemByName
			));
		}
		
		int numMobDrops = input.readInt();
		mobDrops = new ArrayList<>(numMobDrops);
		for (int counter = 0; counter < numMobDrops; counter++) {
			mobDrops.add(EntityDrop.load(
					input, this::createCustomItemResult, 
					() -> Recipe.loadResult(input, this), this::getCustomItemByName
			));
		}
		
		// Containers (there are no containers in this encoding)
		fuelRegistries = new ArrayList<>();
		containers = new ArrayList<>();
		
		// Deleted item names aren't remembered in this encoding
		deletedItems = new ArrayList<>();
		
		// There are no armor textures in this encoding
		armorTextures = new ArrayList<>();
	}
	
	private void load7(BitInput rawInput) 
			throws UnknownEncodingException, IntegrityException, IOException {
		// Check integrity
		long expectedHash = rawInput.readLong();
		byte[] remaining;
		try {
			// Catch undefined behavior when the remaining size is wrong
			remaining = rawInput.readByteArray();
		} catch (Throwable t) {
			throw new IntegrityException(t);
		}
		long actualHash = hash(remaining);
		if (expectedHash != actualHash)
			throw new IntegrityException(expectedHash, actualHash);
		
		BitInput input = new ByteArrayBitInput(remaining);
		
		// Textures
		int textureAmount = input.readInt();
		textures = new ArrayList<NamedImage>(textureAmount);
		for (int counter = 0; counter < textureAmount; counter++) {
			byte textureType = input.readByte();
			if (textureType == NamedImage.ENCODING_BOW)
				textures.add(new BowTextures(input, false));
			else if (textureType == NamedImage.ENCODING_SIMPLE)
				textures.add(new NamedImage(input, false));
			else
				throw new UnknownEncodingException("Texture", textureType);
		}
		
		// Projectile covers
		int numProjectileCovers = input.readInt();
		projectileCovers = new ArrayList<>(numProjectileCovers);
		for (int counter = 0; counter < numProjectileCovers; counter++)
			projectileCovers.add(EditorProjectileCover.fromBits(input, this));
		
		// Projectiles
		int numProjectiles = input.readInt();
		projectiles = new ArrayList<>(numProjectiles);
		for (int counter = 0; counter < numProjectiles; counter++)
			projectiles.add(CIProjectile.fromBits(input, this));
		
		// Notify the projectile effects that all projectiles have been loaded
		for (CIProjectile projectile : projectiles)
			projectile.afterProjectilesAreLoaded(this);

		// Items
		int itemAmount = input.readInt();
		items = new ArrayList<CustomItem>(itemAmount);
		for (int counter = 0; counter < itemAmount; counter++)
			items.add(loadItem(input, true));

		// Recipes
		int recipeAmount = input.readInt();
		recipes = new ArrayList<Recipe>(recipeAmount);
		for (int counter = 0; counter < recipeAmount; counter++)
			recipes.add(loadRecipe(input));
		
		// Drops
		int numBlockDrops = input.readInt();
		blockDrops = new ArrayList<>(numBlockDrops);
		for (int counter = 0; counter < numBlockDrops; counter++) {
			blockDrops.add(BlockDrop.load(
					input, this::createCustomItemResult, 
					() -> Recipe.loadResult(input, this), this::getCustomItemByName
			));
		}
		
		int numMobDrops = input.readInt();
		mobDrops = new ArrayList<>(numMobDrops);
		for (int counter = 0; counter < numMobDrops; counter++) {
			mobDrops.add(EntityDrop.load(
					input, this::createCustomItemResult, 
					() -> Recipe.loadResult(input, this), this::getCustomItemByName
			));
		}
		
		// Custom containers and fuel registries
		int numFuelRegistries = input.readInt();
		fuelRegistries = new ArrayList<>(numFuelRegistries);
		for (int counter = 0; counter < numFuelRegistries; counter++) {
			fuelRegistries.add(loadFuelRegistry(input));
		}
		
		int numContainers = input.readInt();
		containers = new ArrayList<>(numContainers);
		for (int counter = 0; counter < numContainers; counter++) {
			containers.add(loadContainer(input));
		}
		
		// Deleted item names aren't remembered in this encoding
		deletedItems = new ArrayList<>();
		
		// There are no armor textures in this encoding
		armorTextures = new ArrayList<>();
	}
	
	private void load8(BitInput rawInput) 
			throws UnknownEncodingException, IntegrityException, IOException {
		// Check integrity
		long expectedHash = rawInput.readLong();
		byte[] remaining;
		try {
			// Catch undefined behavior when the remaining size is wrong
			remaining = rawInput.readByteArray();
		} catch (Throwable t) {
			throw new IntegrityException(t);
		}
		long actualHash = hash(remaining);
		if (expectedHash != actualHash)
			throw new IntegrityException(expectedHash, actualHash);
		
		BitInput input = new ByteArrayBitInput(remaining);
		
		// Textures
		int textureAmount = input.readInt();
		textures = new ArrayList<NamedImage>(textureAmount);
		for (int counter = 0; counter < textureAmount; counter++) {
			byte textureType = input.readByte();
			if (textureType == NamedImage.ENCODING_BOW)
				textures.add(new BowTextures(input, true));
			else if (textureType == NamedImage.ENCODING_SIMPLE)
				textures.add(new NamedImage(input, true));
			else
				throw new UnknownEncodingException("Texture", textureType);
		}
		
		// Armor textures
		int numArmorTextures = input.readInt();
		armorTextures = new ArrayList<>(numArmorTextures);
		for (int counter = 0; counter < numArmorTextures; counter++) {
			armorTextures.add(new Reference<>(ArmorTextures.load(input)));
		}
		
		// Projectile covers
		int numProjectileCovers = input.readInt();
		projectileCovers = new ArrayList<>(numProjectileCovers);
		for (int counter = 0; counter < numProjectileCovers; counter++)
			projectileCovers.add(EditorProjectileCover.fromBits(input, this));
		
		// Projectiles
		int numProjectiles = input.readInt();
		projectiles = new ArrayList<>(numProjectiles);
		for (int counter = 0; counter < numProjectiles; counter++)
			projectiles.add(CIProjectile.fromBits(input, this));
		
		// Notify the projectile effects that all projectiles have been loaded
		for (CIProjectile projectile : projectiles)
			projectile.afterProjectilesAreLoaded(this);

		// Items
		int itemAmount = input.readInt();
		items = new ArrayList<CustomItem>(itemAmount);
		for (int counter = 0; counter < itemAmount; counter++)
			items.add(loadItem(input, true));

		// Recipes
		int recipeAmount = input.readInt();
		recipes = new ArrayList<Recipe>(recipeAmount);
		for (int counter = 0; counter < recipeAmount; counter++)
			recipes.add(loadRecipe(input));
		
		// Drops
		int numBlockDrops = input.readInt();
		blockDrops = new ArrayList<>(numBlockDrops);
		for (int counter = 0; counter < numBlockDrops; counter++) {
			blockDrops.add(BlockDrop.load(
					input, this::createCustomItemResult, 
					() -> Recipe.loadResult(input, this), this::getCustomItemByName
			));
		}
		
		int numMobDrops = input.readInt();
		mobDrops = new ArrayList<>(numMobDrops);
		for (int counter = 0; counter < numMobDrops; counter++) {
			mobDrops.add(EntityDrop.load(
					input, this::createCustomItemResult, 
					() -> Recipe.loadResult(input, this), this::getCustomItemByName
			));
		}
		
		// Custom containers and fuel registries
		int numFuelRegistries = input.readInt();
		fuelRegistries = new ArrayList<>(numFuelRegistries);
		for (int counter = 0; counter < numFuelRegistries; counter++) {
			fuelRegistries.add(loadFuelRegistry(input));
		}
		
		int numContainers = input.readInt();
		containers = new ArrayList<>(numContainers);
		for (int counter = 0; counter < numContainers; counter++) {
			containers.add(loadContainer(input));
		}

		// Match the pocket containers with their containers (this had to be postponed until containers were loaded)
		for (CustomItem item : items) {
			if (item instanceof CustomPocketContainer) {
				((CustomPocketContainer) item).findContainers(this);
			}
		}
		
		// Deleted item names
		int numDeletedItems = input.readInt();
		deletedItems = new ArrayList<>(numDeletedItems);
		for (int counter = 0; counter < numDeletedItems; counter++) {
			deletedItems.add(input.readString());
		}
	}

	/**
	 * A String containing only the quote character. I use this constant because
	 * it's annoying to get that character inside a String
	 */
	private static final String Q = "" + '"';
	
	public static String[] getDefaultModel(CustomItem item) {
		return getDefaultModel(
				item.getItemType(), item.getTexture().getName(), 
				item.getItemType().isLeatherArmor(), 
				!(item instanceof CustomHelmet3D)
		);
	}
	
	public static String[] getDefaultModel(CustomItemType type, String textureName, boolean isLeather, boolean hasDefault) {
		if (!hasDefault) {
			return new String[] {
					"There is no default model for this item type",
					"because it requires a custom model. This is a",
					"complex task, so only do this if you know what",
					"you're doing."
			};
		} else if (type == CustomItemType.BOW) {
			return getDefaultModelBow(textureName);
		} else if (type == CustomItemType.SHIELD) {
			return getDefaultModelShield(textureName);
		} else if (type == CustomItemType.TRIDENT) {
			return getDefaultModelTrident(textureName);
		} else {
			String[] start = {
			"{",
			"    \"parent\": \"item/handheld\",",
			"    \"textures\": {",
			"        \"layer0\": \"customitems/" + textureName + Q + (isLeather ? "," : "")
			};
			
			String[] mid;
			if (isLeather) {
				mid = new String[] {"        \"layer1\": \"customitems/" + textureName + Q};
			} else {
				mid = new String[0];
			}
			
			String[] end = {
			"    }",
			"}"
			};
			
			return chain(start, mid, end);
		}
	}
	
	public static String[] getDefaultModelBow(String textureName) {
		return new String[] {
			"{",
			"    \"parent\": \"item/bow\",",
			"    \"textures\": {",
			"        \"layer0\": \"customitems/" + textureName + "_standby\"",
			"    }",
			"}"
		};
	}
	
	public static String[] getDefaultModelShield(String textureName) {
		return new String[] {
				"{",
				"    \"parent\": \"item/handheld\",",
				"    \"textures\": {",
				"        \"layer0\": \"customitems/" + textureName + "\"",
				"    },",
				"    \"display\": {",
				"        \"thirdperson_righthand\": {",
				"            \"rotation\": [0, -90, 0],",
				"            \"translation\": [3, -1.5, 6],",
				"            \"scale\": [1.25, 1.25, 1.25]",
				"        },",
				"        \"thirdperson_lefthand\": {",
				"            \"rotation\": [0, -90, 0],",
				"            \"translation\": [3, -2, 4],",
				"            \"scale\": [1.25, 1.25, 1.25]",
				"        },",
				"        \"firstperson_righthand\": {",
				"            \"rotation\": [-5, 0, -5],",
				"            \"translation\": [-2, -5, 0],",
				"            \"scale\": [1.35, 1.35, 1.35]",
				"        },",
				"        \"firstperson_lefthand\": {",
				"            \"rotation\": [5, 0, -5],",
				"            \"translation\": [-1.5, -5, 0],",
				"            \"scale\": [1.35, 1.35, 1.35]",
				"        }",
				"    }",
				"}"
		};
	}
	
	public static String[] getDefaultModelBlockingShield(String textureName) {
		return new String[] {
				"{",
				"    \"parent\": \"item/handheld\",",
				"    \"textures\": {",
				"        \"layer0\": \"customitems/" + textureName + "\"",
				"    },",
				"    \"display\": {",
				"        \"thirdperson_righthand\": {",
				"            \"rotation\": [35, -45, -5],",
				"            \"translation\": [5, 0, 1],",
				"            \"scale\": [1.15, 1.15, 1.15]",
				"        },",
				"        \"thirdperson_lefthand\": {",
				"            \"rotation\": [35, -35, -5],",
				"            \"translation\": [3, -3, -1],",
				"            \"scale\": [1.25, 1.25, 1.25]",
				"        },",
				"        \"firstperson_righthand\": {",
				"            \"rotation\": [0, -5, 5],",
				"            \"translation\": [-6, -0.5, 0],",
				"            \"scale\": [1.2, 1.2, 1.2]",
				"        },",
				"        \"firstperson_lefthand\": {",
				"            \"rotation\": [0, -5, 5],",
				"            \"translation\": [-6, -2.5, 0],",
				"            \"scale\": [1.2, 1.2, 1.2]",
				"        }",
				"    }",
				"}"
		};
	}
	
	public static String[] getDefaultModelTrident(String textureName) {
		return new String[] {
				"{",
				"    \"parent\": \"item/generated\",",
				"    \"textures\": {",
				"        \"layer0\": \"customitems/" + textureName + "\"",
				"    },",
				"    \"display\": {",
				"        \"gui\": {",
				"            \"rotation\": [0, 0, -45],",
				"            \"translation\": [0, 0, 0],",
				"            \"scale\": [1, 1, 1]",
				"        },",
				"        \"ground\": {",
				"            \"rotation\": [0, 0, -45],",
				"            \"translation\": [0, 0, 0],",
				"            \"scale\": [0.5, 0.5, 0.5]",
				"        }",
				"    }",
				"}"
		};
	}
	
	public static String[] getDefaultModelTridentInHand(String textureName) {
		return new String[] {
				"{",
				"    \"parent\": \"item/handheld\",",
				"    \"textures\": {",
				"        \"layer0\": \"customitems/" + textureName + "\"",
				"    },",
				"    \"display\": {",
				"        \"thirdperson_righthand\": {",
				"            \"rotation\": [0, 65, 0],",
				"            \"translation\": [0, 0, 0],",
				"            \"scale\": [0.5, 1.8, 1.0]",
				"        },",
				"        \"thirdperson_lefthand\": {",
				"            \"rotation\": [0, 65, 0],",
				"            \"translation\": [0, 0, 0],",
				"            \"scale\": [0.5, 1.8, 1.0]",
				"        },",
				"        \"firstperson_righthand\": {",
				"            \"rotation\": [-30, 100, 0],",
				"            \"translation\": [4, 2, 0],",
				"            \"scale\": [0.5, 1.0, 1.0]",
				"        },",
				"        \"firstperson_lefthand\": {",
				"            \"rotation\": [-30, 100, 0],",
				"            \"translation\": [4, 2, 0],",
				"            \"scale\": [0.5, 1.0, 1.0]",
				"        }",
				"    }",
				"}"
		};
	}
	
	public static String[] getDefaultModelTridentThrowing(String textureName) {
		return new String[] {
				"{",
				"    \"parent\": \"item/handheld\",",
				"    \"textures\": {",
				"        \"layer0\": \"customitems/" + textureName + "\"",
				"    },",
				"    \"display\": {",
				"        \"thirdperson_righthand\": {",
				"            \"rotation\": [0, 90, 180],",
				"            \"translation\": [1, -3, 2],",
				"            \"scale\": [1, 2, 1]",
				"        },",
				"        \"thirdperson_lefthand\": {",
				"            \"rotation\": [0, 90, 180],",
				"            \"translation\": [1, -3, 2],",
				"            \"scale\": [1, 2, 1]",
				"        },",
				"        \"firstperson_righthand\": {",
				"            \"rotation\": [-20, -90, 0],",
				"            \"translation\": [5, 2, -1],",
				"            \"scale\": [1, 2, 1]",
				"        },",
				"        \"firstperson_lefthand\": {",
				"            \"rotation\": [-20, -90, 0],",
				"            \"translation\": [5, 2, -1],",
				"            \"scale\": [1, 2, 1]",
				"        }",
				"    }",
				"}"
		};
	}
	
	private static String[] getMinecraftModelTridentInHandBegin() {
		return new String[] {
				"{",
				"    \"parent\": \"builtin/entity\",",
				"    \"textures\": {",
				"        \"particle\": \"item/trident\"",
				"    },",
				"    \"display\": {",
				"        \"thirdperson_righthand\": {",
				"            \"rotation\": [0, 60, 0],",
				"            \"translation\": [11, 17, -2],",
				"            \"scale\": [1, 1, 1]",
				"        },",
				"        \"thirdperson_lefthand\": {",
				"            \"rotation\": [0, 60, 0],",
				"            \"translation\": [3, 17, 12],",
				"            \"scale\": [1, 1, 1]",
				"        },",
				"        \"firstperson_righthand\": {",
				"            \"rotation\": [0, -90, 25],",
				"            \"translation\": [-3, 17, 1],",
				"            \"scale\": [1, 1, 1]",
				"        },",
				"        \"firstperson_lefthand\": {",
				"            \"rotation\": [0, 90, -25],",
				"            \"translation\": [13, 17, 1],",
				"            \"scale\": [1, 1, 1]",
				"        },",
				"        \"gui\": {",
				"            \"rotation\": [15, -25, -5],",
				"            \"translation\": [2, 3, 0],",
				"            \"scale\": [0.65, 0.65, 0.65]",
				"        },",
				"        \"fixed\": {",
				"            \"rotation\": [0, 180, 0],",
				"            \"translation\": [-2, 4, -5],",
				"            \"scale\": [0.5, 0.5, 0.5]",
				"        },",
				"        \"ground\": {",
				"            \"rotation\": [0, 0, 0],",
				"            \"translation\": [4, 4, 2],",
				"            \"scale\": [0.25, 0.25, 0.25]",
				"        }",
				"    },",
				"    \"overrides\": [",
				"        {\"predicate\": {\"throwing\": 1}, \"model\": \"item/trident_throwing\"},",
		};
	}
	
	private static String[] getMinecraftModelTridentInHandEnd() {
		return new String[] {
				"        {\"predicate\": {\"damaged\": 1, \"damage\": 0}, \"model\": \"item/trident_in_hand\"},",
				"        {\"predicate\": {\"damaged\": 1, \"damage\": 0, \"throwing\": 1}, \"model\": \"item/trident_throwing\"}",
				"    ]",
				"}"
		};
	}
	
	public static String[] chain(String[]...arrays) {
		int length = 0;
		for (String[] array : arrays) {
			length += array.length;
		}
		String[] result = new String[length];
		int index = 0;
		for (String[] array : arrays) {
			System.arraycopy(array, 0, result, index, array.length);
			index += array.length;
		}
		return result;
	}
	
	/**
	 * Export the item set for minecraft version 1.mcVersion with the resourcepack format for mc
	 * 1.13.x and 1.14.x
	 * @param mcVersion The minecraft version to export for, after the 1.
	 * @return The error message if exporting failed, or null if the item set was exported successfully
	 */
	public String exportFor13Or14(int mcVersion) {
		return exportFor13Or14(mcVersion, 4);
	}
	
	public String exportFor15() {
		
		// It seems like nothing relevant for this plug-in changed in the resourcepack format
		return exportFor13Or14(MCVersions.VERSION1_15, 5);
	}
	
	public String exportFor16() {
		
		// They raised the resourcepack format from 5 to 6
		// But for some reason, they made that switch between 1.16.1 and 1.16.2
		return exportFor13Or14(MCVersions.VERSION1_16, 6);
	}
	
	private void exportOptifineArmor(ZipOutputStream zipOutput, int mcVersion) throws IOException {

		String citPrefix;
		if (mcVersion <= VERSION1_12) {
			citPrefix = "assets/minecraft/mcpatcher/cit/";
		} else {
			citPrefix = "assets/minecraft/optifine/cit/";
		}

		// Armor textures
		for (Reference<ArmorTextures> armorTexture : armorTextures) {
			String prefix = 
					citPrefix + "customarmor/" + armorTexture.get().getName() + "/";
			ZipEntry firstLayerEntry = new ZipEntry(prefix + "layer_1.png");
			zipOutput.putNextEntry(firstLayerEntry);
			ImageIO.write(
					armorTexture.get().getLayer1(), 
					"PNG", 
					new MemoryCacheImageOutputStream(zipOutput)
			);
			zipOutput.closeEntry();
			
			ZipEntry secondLayerEntry = new ZipEntry(prefix + "layer_2.png");
			zipOutput.putNextEntry(secondLayerEntry);
			ImageIO.write(
					armorTexture.get().getLayer2(), 
					"PNG", 
					new MemoryCacheImageOutputStream(zipOutput)
			);
			zipOutput.closeEntry();
		}
		
		// Link the custom armor to their textures
		for (CustomItem item : items) {
			if (item instanceof CustomArmor) {
				
				CustomArmor armor = (CustomArmor) item;
				if (armor.getWornTexture() != null) {
					
					ArmorTextures wornTexture = armor.getWornTexture().get();
					String prefix = 
							citPrefix + "customarmor/" + wornTexture.getName() + "/";
					ZipEntry armorEntry = new ZipEntry(prefix + armor.getName() + ".properties");
					zipOutput.putNextEntry(armorEntry);
					
					PrintWriter propertyWriter = new PrintWriter(zipOutput);
					propertyWriter.println("type=armor");
					String vanillaName = armor.getItemType().getModelName14();
					propertyWriter.println("items=" + vanillaName);
					vanillaName = armor.getItemType().getTextureName12();
					String vanillaMaterial = vanillaName.substring(0, vanillaName.indexOf('_'));
					propertyWriter.println("texture." + vanillaMaterial + "_layer_1=layer_1");
					propertyWriter.println("texture." + vanillaMaterial + "_layer_2=layer_2");
					propertyWriter.println("nbt.KnokkosCustomItems.Name=" + armor.getName());
					propertyWriter.flush();
					
					zipOutput.closeEntry();
				}
			}
		}
	}
	
	private String exportFor13Or14(int mcVersion, int packFormat) {
		String versionError = validateExportVersion(mcVersion);
		if (versionError != null) {
			return versionError;
		}
		
		// Make sure every item has the right internal item damage before exporting
		Map<CustomItemType, List<DurabilityClaim>> claimMap;
		try {
			claimMap = assignDurabilities();
		} catch (DurabilityClaimException claimTrouble) {
			return "Too many items have " + claimTrouble.exceededType + " as internal item type";
		}
		
		try {
			
			// See exportOld for explanation
			File file = new File(Editor.getFolder() + "/" + fileName + ".cis");// cis stands for Custom Item Set
			OutputStream fileOutput = Files.newOutputStream(file.toPath());
			ByteArrayBitOutput output = new ByteArrayBitOutput();
			export8(output);
			output.terminate();
			
			byte[] bytes = output.getBytes();
			fileOutput.write(bytes);
			fileOutput.flush();
			fileOutput.close();
			
			byte[] textyBytes = StringEncoder.encodeTextyBytes(bytes, true);
			fileOutput = Files.newOutputStream(new File(Editor.getFolder() + "/" + fileName + ".txt").toPath());
			fileOutput.write(textyBytes);
			fileOutput.flush();
			fileOutput.close();
			
			ZipOutputStream zipOutput = new ZipOutputStream(
					new FileOutputStream(new File(Editor.getFolder() + "/" + fileName + ".zip")));

			// Custom textures
			for (NamedImage texture : textures) {
				String textureName = texture.getName();
				if (texture instanceof BowTextures) {
					textureName += "_standby";
					BowTextures bt = (BowTextures) texture;
					List<BowTextures.Entry> pullTextures = bt.getPullTextures();
					int index = 0;
					for (BowTextures.Entry pullTexture : pullTextures) {
						ZipEntry entry = new ZipEntry("assets/minecraft/textures/customitems/" + bt.getName()
								+ "_pulling_" + index++ + ".png");
						zipOutput.putNextEntry(entry);
						ImageIO.write(pullTexture.getTexture(), "PNG", new MemoryCacheImageOutputStream(zipOutput));
						zipOutput.closeEntry();
					}
				}
				ZipEntry entry = new ZipEntry("assets/minecraft/textures/customitems/" + textureName + ".png");
				zipOutput.putNextEntry(entry);
				ImageIO.write(texture.getImage(), "PNG", new MemoryCacheImageOutputStream(zipOutput));
				zipOutput.closeEntry();
			}
			
			exportOptifineArmor(zipOutput, mcVersion);

			// Custom item models
			for (CustomItem item : items) {
				ZipEntry entry = new ZipEntry("assets/minecraft/models/customitems/" + item.getName() + ".json");
				zipOutput.putNextEntry(entry);
				PrintWriter jsonWriter = new PrintWriter(zipOutput);
				byte[] customModel = item.getCustomModel();
				if (customModel != null) {
					zipOutput.write(customModel);
					zipOutput.flush();
				} else {
					String[] modelContent = getDefaultModel(item);
					for (String line : modelContent) {
						jsonWriter.println(line);
					}
					jsonWriter.flush();
				}
				zipOutput.closeEntry();
				if (item instanceof CustomBow) {
					CustomBow bow = (CustomBow) item;
					List<BowTextures.Entry> pullTextures = bow.getTexture().getPullTextures();
					String textureName = item.getTexture().getName() + "_pulling_";
					for (int index = 0; index < pullTextures.size(); index++) {
						entry = new ZipEntry("assets/minecraft/models/customitems/" + item.getName() + "_pulling_"
								+ index + ".json");
						zipOutput.putNextEntry(entry);
						jsonWriter = new PrintWriter(zipOutput);
						jsonWriter.println("{");
						jsonWriter.println("    " + Q + "parent" + Q + ": " + Q + "item/bow" + Q + ",");
						jsonWriter.println("    " + Q + "textures" + Q + ": {");
						jsonWriter.println(
								"        " + Q + "layer0" + Q + ": " + Q + "customitems/" + textureName + index + Q);
						jsonWriter.println("    }");
						jsonWriter.println("}");
						jsonWriter.flush();
						zipOutput.closeEntry();
					}
				} else if (item instanceof CustomShield) {
					CustomShield shield = (CustomShield) item;
					byte[] blockingModel = shield.getBlockingModel();
					entry = new ZipEntry("assets/minecraft/models/customitems/" + item.getName() + "_blocking.json");
					zipOutput.putNextEntry(entry);
					if (blockingModel != null) {
						zipOutput.write(blockingModel);
						zipOutput.flush();
					} else {
						String[] modelContent = getDefaultModelBlockingShield(item.getTexture().getName());
						jsonWriter = new PrintWriter(zipOutput);
						for (String line : modelContent) {
							jsonWriter.println(line);
						}
						jsonWriter.flush();
					}
				} else if (item instanceof CustomTrident) {
					CustomTrident trident = (CustomTrident) item;
					byte[] inHandModel = trident.customInHandModel;
					entry = new ZipEntry("assets/minecraft/models/customitems/" + item.getName() + "_in_hand.json");
					zipOutput.putNextEntry(entry);
					if (inHandModel != null) {
						zipOutput.write(inHandModel);
						zipOutput.flush();
					} else {
						String[] modelContent = getDefaultModelTridentInHand(item.getTexture().getName());
						jsonWriter = new PrintWriter(zipOutput);
						for (String line : modelContent) {
							jsonWriter.println(line);
						}
						jsonWriter.flush();
					}
					byte[] throwingModel = trident.customThrowingModel;
					entry = new ZipEntry("assets/minecraft/models/customitems/" + item.getName() + "_throwing.json");
					zipOutput.putNextEntry(entry);
					if (throwingModel != null) {
						zipOutput.write(throwingModel);
						zipOutput.flush();
					} else {
						String[] modelContent = getDefaultModelTridentThrowing(item.getTexture().getName());
						jsonWriter = new PrintWriter(zipOutput);
						for (String line : modelContent) {
							jsonWriter.println(line);
						}
						jsonWriter.flush();
					}
				}
			}
			
			// Projectile covers
			for (EditorProjectileCover cover : projectileCovers) {
				ZipEntry entry = new ZipEntry("assets/minecraft/models/customprojectiles/" + cover.name + ".json");
				zipOutput.putNextEntry(entry);
				cover.writeModel(zipOutput);
				zipOutput.flush();
			}
			
			
			
			for (Entry<CustomItemType, List<DurabilityClaim>> claimsEntry : claimMap.entrySet()) {
				
				CustomItemType itemType = claimsEntry.getKey();
				List<DurabilityClaim> claims = claimsEntry.getValue();
				if (!claims.isEmpty()) {

					String modelName = itemType.getModelName14();
					String textureName = itemType.getTextureName14();
					
					ZipEntry zipEntry = new ZipEntry("assets/minecraft/models/item/" + modelName + ".json");
					zipOutput.putNextEntry(zipEntry);
					final PrintWriter jsonWriter = new PrintWriter(zipOutput);

					if (itemType == CustomItemType.BOW) {
						// Begin of the json file
						jsonWriter.println("{");
						jsonWriter.println("    " + Q + "parent" + Q + ": " + Q + "item/generated" + Q + ",");
						jsonWriter.println("    " + Q + "textures" + Q + ": {");
						jsonWriter.println("        " + Q + "layer0" + Q + ": " + Q + "item/bow" + Q);
						jsonWriter.println("    },");
						// Display
						jsonWriter.println("    " + Q + "display" + Q + ": {");
						jsonWriter.println("        " + Q + "thirdperson_righthand" + Q + ": {");
						jsonWriter.println("            " + Q + "rotation" + Q + ": [ -80, 260, -40 ],");
						jsonWriter.println("            " + Q + "translation" + Q + ": [ -1, -2, 2.5 ],");
						jsonWriter.println("            " + Q + "scale" + Q + ": [ 0.9, 0.9, 0.9 ]");
						jsonWriter.println("        },");
						jsonWriter.println("        " + Q + "thirdperson_lefthand" + Q + ": {");
						jsonWriter.println("            " + Q + "rotation" + Q + ": [ -80, -280, 40 ],");
						jsonWriter.println("            " + Q + "translation" + Q + ": [ -1, -2, 2.5 ],");
						jsonWriter.println("            " + Q + "scale" + Q + ": [ 0.9, 0.9, 0.9 ]");
						jsonWriter.println("        },");
						jsonWriter.println("        " + Q + "firstperson_righthand" + Q + ": {");
						jsonWriter.println("            " + Q + "rotation" + Q + ": [ 0, -90, 25 ],");
						jsonWriter.println("            " + Q + "translation" + Q + ": [ 1.13, 3.2, 1.13 ],");
						jsonWriter.println("            " + Q + "scale" + Q + ": [ 0.68, 0.68, 0.68 ]");
						jsonWriter.println("        },");
						jsonWriter.println("        " + Q + "firstperson_lefthand" + Q + ": {");
						jsonWriter.println("            " + Q + "rotation" + Q + ": [ 0, 90, -25 ],");
						jsonWriter.println("            " + Q + "translation" + Q + ": [ 1.13, 3.2, 1.13 ],");
						jsonWriter.println("            " + Q + "scale" + Q + ": [ 0.68, 0.68, 0.68 ]");
						jsonWriter.println("        }");
						jsonWriter.println("    },");
						// The interesting part...
						jsonWriter.println("    " + Q + "overrides" + Q + ": [");

						jsonWriter.println("        { " + Q + "predicate" + Q + ": { " + Q + "pulling" + Q + ": 1 }, "
								+ Q + "model" + Q + ": " + Q + "item/bow_pulling_0" + Q + "},");
						jsonWriter.println("        { " + Q + "predicate" + Q + ": { " + Q + "pulling" + Q + ": 1, " + Q
								+ "pull" + Q + ": 0.65 }, " + Q + "model" + Q + ": " + Q + "item/bow_pulling_1" + Q
								+ "},");
						jsonWriter.println("        { " + Q + "predicate" + Q + ": { " + Q + "pulling" + Q + ": 1, " + Q
								+ "pull" + Q + ": 0.9 }, " + Q + "model" + Q + ": " + Q + "item/bow_pulling_2" + Q
								+ "},");

						for (DurabilityClaim claim : claims) {
							jsonWriter.println("        { " + Q + "predicate" + Q + ": {" + Q + "damaged" + Q + ": 0, "
									+ Q + "damage" + Q + ": "
									+ (double) claim.itemDamage / itemType.getMaxDurability() + "}, " + Q
									+ "model" + Q + ": " + Q + claim.resourcePath + Q + "},");
							List<BowTextures.Entry> pullTextures = claim.bowTextures.getPullTextures();
							int counter = 0;
							for (BowTextures.Entry pullTexture : pullTextures) {
								jsonWriter.println("        { " + Q + "predicate" + Q + ": {" + Q + "damaged" + Q
										+ ": 0, " + Q + "damage" + Q + ": "
										+ (double) claim.itemDamage / itemType.getMaxDurability() + ", "
										+ Q + "pulling" + Q + ": 1, " + Q + "pull" + Q + ": " + pullTexture.getPull()
										+ "}, " + Q + "model" + Q + ": " + Q + claim.resourcePath
										+ "_pulling_" + counter++ + Q + "},");
							}
						}
						// End of the json file
						jsonWriter.println("        { \"predicate\": {\"damaged\": 1, \"damage\": 0}, \"model\": \"item/" + modelName + "\"},");
						jsonWriter.println("        { \"predicate\": {\"damaged\": 1, \"damage\": 0, \"pulling\": 1 }, \"model\": \"item/" + modelName + "_pulling_0\"},");
						jsonWriter.println("        { \"predicate\": {\"damaged\": 1, \"damage\": 0, \"pulling\": 1, \"pull\": 0.65 }, \"model\": \"item/" + modelName + "_pulling_1\"},");
						jsonWriter.println("        { \"predicate\": {\"damaged\": 1, \"damage\": 0, \"pulling\": 1, \"pull\": 0.9 }, \"model\": \"item/" + modelName + "_pulling_2\"}");
						jsonWriter.println("    ]");
						jsonWriter.println("}");
					} else if (itemType == CustomItemType.SHIELD) {
						
						// The beginning
						jsonWriter.println("{");
						jsonWriter.println("    \"parent\": \"builtin/entity\",");
						jsonWriter.println("    \"textures\": {");
						jsonWriter.println("        \"particle\": \"block/dark_oak_planks\"");
						jsonWriter.println("    },");
						jsonWriter.println("    \"display\": {");
						
						// All the display stuff, it's copied from minecrafts default shield model
						jsonWriter.println("        \"thirdperson_righthand\": {");
						jsonWriter.println("            \"rotation\": [0,90,0],");
						jsonWriter.println("            \"translation\": [10,6,-4],");
						jsonWriter.println("            \"scale\": [1,1,1]");
						jsonWriter.println("        },");
						jsonWriter.println("        \"thirdperson_lefthand\": {");
						jsonWriter.println("            \"rotation\": [0,90,0],");
						jsonWriter.println("            \"translation\": [10,6,12],");
						jsonWriter.println("            \"scale\": [1,1,1]");
						jsonWriter.println("        },");
						jsonWriter.println("        \"firstperson_righthand\": {");
						jsonWriter.println("            \"rotation\": [0,180,5],");
						jsonWriter.println("            \"translation\": [-10,2,-10],");
						jsonWriter.println("            \"scale\": [1.25,1.25,1.25]");
						jsonWriter.println("        },");
						jsonWriter.println("        \"firstperson_lefthand\": {");
						jsonWriter.println("            \"rotation\": [0,180,5],");
						jsonWriter.println("            \"translation\": [10,0,-10],");
						jsonWriter.println("            \"scale\": [1.25,1.25,1.25]");
						jsonWriter.println("        },");
						jsonWriter.println("        \"gui\": {");
						jsonWriter.println("            \"rotation\": [15,-25,-5],");
						jsonWriter.println("            \"translation\": [2,3,0],");
						jsonWriter.println("            \"scale\": [0.65,0.65,0.65]");
						jsonWriter.println("        },");
						jsonWriter.println("        \"fixed\": {");
						jsonWriter.println("            \"rotation\": [0,180,0],");
						jsonWriter.println("            \"translation\": [-2,4,-5],");
						jsonWriter.println("            \"scale\": [0.5,0.5,0.5]");
						jsonWriter.println("        },");
						jsonWriter.println("        \"ground\": {");
						jsonWriter.println("            \"rotation\": [0,0,0],");
						jsonWriter.println("            \"translation\": [4,4,2],");
						jsonWriter.println("            \"scale\": [0.25,0.25,0.25]");
						jsonWriter.println("        }");
						jsonWriter.println("    }, \"overrides\": [");
						
						// The next entry is part of preserving vanilla shield blocking model
						jsonWriter.println("        { \"predicate\": { \"blocking\": 1 }, \"model\": \"item/shield_blocking\" },");
						
						// Now the part for the custom shield predicates...
						for (DurabilityClaim claim : claims) {
							jsonWriter.println("        { \"predicate\": { \"blocking\": 0, \"damaged\": 0, \"damage\": "
									+ (double) claim.itemDamage / itemType.getMaxDurability() + " }, \"model\": \"" + claim.resourcePath + "\" },");
							jsonWriter.println("        { \"predicate\": { \"blocking\": 1, \"damaged\": 0, \"damage\": "
									+ (double) claim.itemDamage / itemType.getMaxDurability() + " }, \"model\": \"" + claim.resourcePath + "_blocking\" },");
						}
						
						// The next ones are required to preserve the vanilla shield models
						jsonWriter.println("        { \"predicate\": { \"blocking\": 0, \"damaged\": 1, \"damage\": 0 }, \"model\": \"item/shield\" },");
						jsonWriter.println("        { \"predicate\": { \"blocking\": 1, \"damaged\": 1, \"damage\": 0 }, \"model\": \"item/shield_blocking\" }");
						
						// Now finish the json
						jsonWriter.println("    ]");
						jsonWriter.println("}");
					} else {
						// Begin of the json file
						jsonWriter.println("{");
						jsonWriter.println("    " + Q + "parent" + Q + ": " + Q + "item/handheld" + Q + ",");
						jsonWriter.println("    " + Q + "textures" + Q + ": {");
						jsonWriter.print("        " + Q + "layer0" + Q + ": " + Q + "item/" + textureName + Q);
						boolean isLeatherArmor = itemType.isLeatherArmor();
						if (isLeatherArmor) {
							jsonWriter.print(",");
						}
						jsonWriter.println();
						if (isLeatherArmor) {
							jsonWriter.print("        " + Q + "layer1" + Q + ": " + Q + "item/" + textureName + "_overlay" + Q);
						}
						jsonWriter.println("    },");
						jsonWriter.println("    " + Q + "overrides" + Q + ": [");

						// Now the interesting part
						for (DurabilityClaim claim : claims) {
							jsonWriter.println("        { " + Q + "predicate" + Q + ": {" + Q + "damaged" + Q + ": 0, "
									+ Q + "damage" + Q + ": "
									+ (double) claim.itemDamage / itemType.getMaxDurability() + "}, " + Q
									+ "model" + Q + ": " + Q + claim.resourcePath + Q + "},");
						}

						// End of the json file
						jsonWriter.println("        { " + Q + "predicate" + Q + ": {" + Q + "damaged" + Q + ": 1, " + Q
								+ "damage" + Q + ": 0}, " + Q + "model" + Q + ": " + Q + "item/" + modelName + Q + "}");
						jsonWriter.println("    ]");
						jsonWriter.println("}");
					}
					jsonWriter.flush();
					
					// Not part of the if-else chain above because the base item model of trident is not special
					if (itemType == CustomItemType.TRIDENT) {
						
						// The beginning:
						zipEntry = new ZipEntry("assets/minecraft/models/item/" + modelName + "_in_hand.json");
						zipOutput.putNextEntry(zipEntry);
						String[] begin = getMinecraftModelTridentInHandBegin();
						
						String[] end = getMinecraftModelTridentInHandEnd();
						
						for (String line : begin) {
							jsonWriter.println(line);
						}
						
						for (DurabilityClaim claim : claims) {
							jsonWriter.println("        { \"predicate\": { \"throwing\": 0, \"damaged\": 0, \"damage\": "
									+ (double) claim.itemDamage / itemType.getMaxDurability() + " }, \"model\": \"" + claim.resourcePath + "_in_hand\" },");
							jsonWriter.println("        { \"predicate\": { \"throwing\": 1, \"damaged\": 0, \"damage\": "
									+ (double) claim.itemDamage / itemType.getMaxDurability() + " }, \"model\": \"" + claim.resourcePath + "_throwing\" },");
						}
						
						for (String line : end) {
							jsonWriter.println(line);
						}
						
						jsonWriter.flush();
					}
					zipOutput.closeEntry();
				}
			}

			// pack.mcmeta
			ZipEntry mcMeta = new ZipEntry("pack.mcmeta");
			zipOutput.putNextEntry(mcMeta);
			PrintWriter jsonWriter = new PrintWriter(zipOutput);
			jsonWriter.println("{");
			jsonWriter.println("    " + Q + "pack" + Q + ": {");
			jsonWriter.println("        " + Q + "pack_format" + Q + ": " + packFormat + ",");
			jsonWriter.println("        " + Q + "description" + Q + ": " + Q + "CustomItemSet" + Q);
			jsonWriter.println("    }");
			jsonWriter.println("}");
			jsonWriter.flush();
			zipOutput.closeEntry();

			zipOutput.close();
			return null;
		} catch (IOException ioex) {
			ioex.printStackTrace();
			return ioex.getMessage();
		}
	}
	
	private int firstIngredientVersion(Ingredient ingredient) {
		if (ingredient instanceof NoIngredient || ingredient instanceof CustomItemIngredient) {
			return FIRST_VERSION;
		} else if (ingredient instanceof SimpleVanillaIngredient) {
			return ((SimpleVanillaIngredient) ingredient).getType().firstVersion;
		} else {
			return ((DataVanillaIngredient) ingredient).getType().firstVersion;
		}
	}
	
	private int lastIngredientVersion(Ingredient ingredient) {
		if (ingredient instanceof NoIngredient || ingredient instanceof CustomItemIngredient) {
			return LAST_VERSION;
		} else if (ingredient instanceof SimpleVanillaIngredient) {
			return ((SimpleVanillaIngredient) ingredient).getType().lastVersion;
		} else {
			// Minecraft got rid of metadata after minecraft 1.12
			return VERSION1_12;
		}
	}
	
	private int firstResultVersion(Result result) {
		if (result instanceof CustomItemResult || result instanceof CopiedResult) {
			return FIRST_VERSION;
		} else if (result instanceof SimpleVanillaResult) {
			return ((SimpleVanillaResult) result).getType().firstVersion;
		} else {
			return ((DataVanillaResult) result).getType().firstVersion;
		}
	}
	
	private int lastResultVersion(Result result) {
		if (result instanceof CustomItemResult || result instanceof CopiedResult) {
			return LAST_VERSION;
		} else if (result instanceof SimpleVanillaResult) {
			return ((SimpleVanillaResult) result).getType().lastVersion;
		} else {
			// Minecraft got rid of metadata after minecraft 1.12
			return VERSION1_12;
		}
	}
	
	private String validateExportVersion(int version) {
		
		// Reject everything from a higher minecraft version
		for (CustomItem item : items) {
			for (Enchantment enchant : item.getDefaultEnchantments()) {
				if (enchant.getType().version > version) {
					return "The item " + item.getName() + " has enchantment " + enchant.getType().getName() + ", which was added after minecraft " + versionName(version);
				}
			}
			if (item instanceof CustomTool) {
				CustomTool tool = (CustomTool) item;
				if (firstIngredientVersion(tool.getRepairItem()) > version) {
					return "The repair item " + tool.getRepairItem() + " for " + tool.getName() + " was added after minecraft " + versionName(version);
				}
				if (lastIngredientVersion(tool.getRepairItem()) < version) {
					return "The repair item " + tool.getRepairItem() + " for " + tool.getName() + " no longer exists in minecraft " + versionName(version);
				}
				
				if (item instanceof CustomArmor) {
					CustomArmor armor = (CustomArmor) item;
					DamageSource[] sources = DamageSource.values();
					for (DamageSource source : sources) {
						if ((source.firstVersion > version || source.lastVersion < version) && armor.getDamageResistances().getResistance(source) != 0) {
							return "Armor " + item.getName() + " has a damage resistance against " + source + ", which doesn't exist in minecraft " + versionName(version);
						}
					}
				}
				
				if (item.getItemType().firstVersion > version) {
					return "The item " + item.getName() + " is a " + item.getItemType() + ", which were added after minecraft " + versionName(version);
				}
				if (item.getItemType().lastVersion < version) {
					return "The item " + item.getName() + " is a " + item.getItemType() + ", which is not available in minecraft " + versionName(version);
				}
			}
		}
		
		for (Recipe recipe : recipes) {
			if (recipe instanceof ShapedRecipe) {
				ShapedRecipe shaped = (ShapedRecipe) recipe;
				for (Ingredient ingredient : shaped.getIngredients()) {
					if (firstIngredientVersion(ingredient) > version) {
						return "The ingredient " + ingredient + " used for " + shaped.getResult() + " was added after minecraft " + versionName(version);
					}
					if (lastIngredientVersion(ingredient) < version) {
						return "The ingredient " + ingredient + " used for " + shaped.getResult() + " no longer exists in minecraft " + versionName(version);
					}
				}
			} else {
				ShapelessRecipe shapeless = (ShapelessRecipe) recipe;
				for (Ingredient ingredient : shapeless.getIngredients()) {
					if (firstIngredientVersion(ingredient) > version) {
						return "The ingredient " + ingredient + " used for " + shapeless.getResult() + " was added after minecraft " + versionName(version);
					}
					if (lastIngredientVersion(ingredient) < version) {
						return "The ingredient " + ingredient + " used for " + shapeless.getResult() + " no longer exists in minecraft " + versionName(version);
					}
				}
			}
			
			if (firstResultVersion(recipe.getResult()) > version) {
				return "The crafting recipe result " + recipe.getResult() + " was added after minecraft " + versionName(version);
			}
			if (lastResultVersion(recipe.getResult()) < version) {
				return "The crafting recipe result " + recipe.getResult() + " no longer exists in minecraft " + versionName(version);
			}
		}
		
		for (BlockDrop drop : blockDrops) {
			if (drop.getBlock().firstVersion > version || drop.getBlock().lastVersion < version) {
				return "There is a block drop for " + drop.getBlock() + ", but this block doesn't exist in minecraft " + versionName(version);
			}
		}
		
		for (EntityDrop drop : mobDrops) {
			if (drop.getEntityType().firstVersion > version || drop.getEntityType().lastVersion < version) {
				return "There is a mob drop for " + drop.getEntityType() + ", but this mob doesn't exist in minecraft " + versionName(version);
			}
		}
		
		for (CustomFuelRegistry fuelRegistry : fuelRegistries) {
			for (FuelEntry entry : fuelRegistry.getEntries()) {
				CIMaterial material = null;
				if (entry.getFuel() instanceof SimpleVanillaIngredient) {
					material = ((SimpleVanillaIngredient)entry.getFuel()).getType();
				} else if (entry.getFuel() instanceof DataVanillaIngredient) {
					material = ((DataVanillaIngredient)entry.getFuel()).getType();
				}
				if (material != null && (material.firstVersion > version || material.lastVersion < version)) {
					return "The fuel registry " + fuelRegistry.getName() + " uses " + material + ", which is not in this mc version";
				}
			}
		}
		
		for (CustomContainer container : containers) {
			if (
					container.getVanillaType().firstVersion > version ||
					container.getVanillaType().lastVersion < version
			) {
				return "The vanilla type of container " + container.getName() + " isn't available in this mc version";
			}
			
			for (CustomSlot slot : container.getSlots()) {
				SlotDisplay[] displays = {};
				if (slot instanceof DecorationCustomSlot) {
					DecorationCustomSlot decorationSlot = (DecorationCustomSlot) slot;
					displays = new SlotDisplay[] {
							decorationSlot.getDisplay()
					};
				} else if (slot instanceof FuelIndicatorCustomSlot) {
					FuelIndicatorCustomSlot indicatorSlot = (FuelIndicatorCustomSlot) slot;
					displays = new SlotDisplay[] {
							indicatorSlot.getDisplay(),
							indicatorSlot.getPlaceholder()
					};
				} else if (slot instanceof ProgressIndicatorCustomSlot) {
					ProgressIndicatorCustomSlot indicatorSlot = (ProgressIndicatorCustomSlot) slot;
					displays = new SlotDisplay[] {
							indicatorSlot.getDisplay(),
							indicatorSlot.getPlaceHolder()
					};
				}
				
				for (SlotDisplay display : displays) {
					if (display.getAmount() <= 0) {
						return "One of the slot displays an item with a stacksize of 0 or lower";
					} else if (display.getAmount() > 64) {
						return "One of the slot displays an item with a stacksize larger than 64";
					}
					
					CIMaterial material = null;
					if (display.getItem() instanceof SimpleVanillaDisplayItem) {
						material = ((SimpleVanillaDisplayItem) display.getItem()).getMaterial();
					} else if (display.getItem() instanceof DataVanillaDisplayItem) {
						if (version > VERSION1_12) {
							return "One of the slots uses an item with a datavalue, but those aren't used after mc 1.12 anymore";
						}
						material = ((DataVanillaDisplayItem) display.getItem()).getMaterial();
					}
					
					if (material != null) {
						if (material.firstVersion > version) {
							return "One of the slots uses " + material + ", which is not yet available in this mc version";
						} else if (material.lastVersion < version) {
							return "One of the slots uses " + material + ", which is no longer available in this mc version";
						}
					}
				}
			}
		}
		
		return null;
	}

	private static class DurabilityClaim {
		
		final short itemDamage;
		
		final String resourcePath;
		
		final BowTextures bowTextures;
		
		DurabilityClaim(short itemDamage, String resourcePath, BowTextures bowTextures) {
			this.itemDamage = itemDamage;
			this.resourcePath = resourcePath;
			this.bowTextures = bowTextures;
		}
	}
	
	private static class DurabilityClaimException extends Exception {
		
		private static final long serialVersionUID = 1932483847L;
		
		final CustomItemType exceededType;
		
		DurabilityClaimException(CustomItemType exceededType) {
			this.exceededType = exceededType;
		}
	}
	
	private Map<CustomItemType, List<DurabilityClaim>> assignDurabilities() throws DurabilityClaimException {
		Map<CustomItemType, List<DurabilityClaim>> resultMap = new EnumMap<>(CustomItemType.class);
		
		for (CustomItemType itemType : CustomItemType.values()) {
			short nextItemDamage = 1;
			List<DurabilityClaim> claims = new ArrayList<>();
			Map<NamedImage, Short> textureAssignments = new HashMap<>();
			
			for (CustomItem item : items) {
				if (item.getItemType() == itemType) {
					
					// Try to reuse its texture
					Short existingAssignment = textureAssignments.get(item.getTexture());
					if (item.getCustomModel() == null && existingAssignment != null) {
						item.setItemDamage(existingAssignment);
					} else {
						item.setItemDamage(nextItemDamage);
						claims.add(new DurabilityClaim(
								nextItemDamage, item.getResourcePath(),
								itemType == CustomItemType.BOW ? (BowTextures) item.getTexture() : null
						));
						
						if (item.getCustomModel() == null) {
							textureAssignments.put(item.getTexture(), nextItemDamage);
						}
						
						nextItemDamage++;
					}
				}
			}
			
			for (EditorProjectileCover cover : projectileCovers) {
				if (cover.itemType == itemType) {
					cover.itemDamage = nextItemDamage;
					claims.add(new DurabilityClaim(nextItemDamage, cover.getResourcePath(), null));
					nextItemDamage++;
				}
			}
			
			// TODO I'm not sure this is precise, but it should at least be close
			if (nextItemDamage > itemType.getMaxDurability()) {
				throw new DurabilityClaimException(itemType);
			}
			
			resultMap.put(itemType, claims);
		}
		
		return resultMap;
	}

	/**
	 * Export the item set for minecraft version 1.mcVersion with the old resourcepack format
	 * @param mcVersion The minecraft version to export for, after the 1.
	 * @return The error message if exporting failed, or null if the item set was exported successfully
	 */
	public String exportFor12(int mcVersion) {
		
		String error = validateExportVersion(mcVersion);
		if (error != null) {
			return error;
		}
		
		// Assign each item an internal item damage BEFORE exporting
		Map<CustomItemType, List<DurabilityClaim>> assignedDurabilities;
		try {
			assignedDurabilities = assignDurabilities();
		} catch (DurabilityClaimException claimTrouble) {
			return "Too many items have item type " + claimTrouble.exceededType;
		}
		
		try {
			ByteArrayBitOutput output = new ByteArrayBitOutput();
			export8(output);
			output.terminate();
			
			byte[] bytes = output.getBytes();
			
			// Write the .cis file, which stands for Custom Item Set
			File file = new File(Editor.getFolder() + "/" + fileName + ".cis");
			OutputStream fileOutput = Files.newOutputStream(file.toPath());
			fileOutput.write(bytes);
			fileOutput.flush();
			fileOutput.close();
			
			/*
			 * Write the .txt file, which can be used as alternative for the .cis file.
			 * It has a bigger file size and will be a bit slower to read, but it is useful
			 * for servers hosts like Aternos that do not allow users to upload (binary files).
			 * 
			 * It will only use alphabetic characters, which makes it possible to copy the data
			 * as text (although it still won't be readable by humans).
			 */
			byte[] textBytes = StringEncoder.encodeTextyBytes(bytes, true);
			File textFile = new File(Editor.getFolder() + "/" + fileName + ".txt");
			fileOutput = Files.newOutputStream(textFile.toPath());
			fileOutput.write(textBytes);
			fileOutput.flush();
			fileOutput.close();
			
			// Write the .zip file, which is the resourcepack
			ZipOutputStream zipOutput = new ZipOutputStream(
					new FileOutputStream(new File(Editor.getFolder() + "/" + fileName + ".zip")));

			// Custom textures
			for (NamedImage texture : textures) {
				String textureName = texture.getName();
				if (texture instanceof BowTextures) {
					textureName += "_standby";
					BowTextures bt = (BowTextures) texture;
					List<BowTextures.Entry> pullTextures = bt.getPullTextures();
					int index = 0;
					for (BowTextures.Entry pullTexture : pullTextures) {
						ZipEntry entry = new ZipEntry("assets/minecraft/textures/customitems/" + bt.getName()
								+ "_pulling_" + index++ + ".png");
						zipOutput.putNextEntry(entry);
						ImageIO.write(pullTexture.getTexture(), "PNG", new MemoryCacheImageOutputStream(zipOutput));
						zipOutput.closeEntry();
					}
				}
				ZipEntry entry = new ZipEntry("assets/minecraft/textures/customitems/" + textureName + ".png");
				zipOutput.putNextEntry(entry);
				ImageIO.write(texture.getImage(), "PNG", new MemoryCacheImageOutputStream(zipOutput));
				zipOutput.closeEntry();
			}
			
			exportOptifineArmor(zipOutput, VERSION1_12);

			// Custom item models
			for (CustomItem item : items) {
				ZipEntry entry = new ZipEntry("assets/minecraft/models/customitems/" + item.getName() + ".json");
				zipOutput.putNextEntry(entry);
				PrintWriter jsonWriter = new PrintWriter(zipOutput);
				byte[] customModel = item.getCustomModel();
				if (customModel != null) {
					zipOutput.write(customModel);
					zipOutput.flush();
				} else {
					String[] modelContent = getDefaultModel(item);
					for (String line : modelContent) {
						jsonWriter.println(line);
					}
					jsonWriter.flush();
				}
				zipOutput.closeEntry();
				if (item instanceof CustomBow) {
					CustomBow bow = (CustomBow) item;
					List<BowTextures.Entry> pullTextures = bow.getTexture().getPullTextures();
					String textureName = item.getTexture().getName() + "_pulling_";
					for (int index = 0; index < pullTextures.size(); index++) {
						entry = new ZipEntry("assets/minecraft/models/customitems/" + item.getName() + "_pulling_"
								+ index + ".json");
						zipOutput.putNextEntry(entry);
						jsonWriter = new PrintWriter(zipOutput);
						jsonWriter.println("{");
						jsonWriter.println("    " + Q + "parent" + Q + ": " + Q + "item/bow" + Q + ",");
						jsonWriter.println("    " + Q + "textures" + Q + ": {");
						jsonWriter.println(
								"        " + Q + "layer0" + Q + ": " + Q + "customitems/" + textureName + index + Q);
						jsonWriter.println("    }");
						jsonWriter.println("}");
						jsonWriter.flush();
						zipOutput.closeEntry();
					}
				} else if (item instanceof CustomShield) {
					CustomShield shield = (CustomShield) item;
					byte[] blockingModel = shield.getBlockingModel();
					entry = new ZipEntry("assets/minecraft/models/customitems/" + item.getName() + "_blocking.json");
					zipOutput.putNextEntry(entry);
					if (blockingModel != null) {
						zipOutput.write(blockingModel);
						zipOutput.flush();
					} else {
						String[] modelContent = getDefaultModelBlockingShield(item.getTexture().getName());
						jsonWriter = new PrintWriter(zipOutput);
						for (String line : modelContent) {
							jsonWriter.println(line);
						}
						jsonWriter.flush();
					}
				} // Don't bother custom trident models since tridents don't exist in old versions
			}
			
			// Projectile covers
			for (EditorProjectileCover cover : projectileCovers) {
				ZipEntry entry = new ZipEntry("assets/minecraft/models/customprojectiles/" + cover.name + ".json");
				zipOutput.putNextEntry(entry);
				cover.writeModel(zipOutput);
				zipOutput.flush();
			}

			// Now create the item model files for those models
			for (Entry<CustomItemType, List<DurabilityClaim>> entry : assignedDurabilities.entrySet()) {
				
				CustomItemType itemType = entry.getKey();
				List<DurabilityClaim> claims = entry.getValue();
				
				if (!claims.isEmpty()) {
					String modelName = itemType.getModelName12();
					String textureName = itemType.getTextureName12();
					ZipEntry zipEntry = new ZipEntry("assets/minecraft/models/item/" + modelName + ".json");
					zipOutput.putNextEntry(zipEntry);
					PrintWriter jsonWriter = new PrintWriter(zipOutput);

					if (itemType == CustomItemType.BOW) {
						// Begin of the json file
						jsonWriter.println("{");
						jsonWriter.println("    " + Q + "parent" + Q + ": " + Q + "item/generated" + Q + ",");
						jsonWriter.println("    " + Q + "textures" + Q + ": {");
						jsonWriter.println("        " + Q + "layer0" + Q + ": " + Q + "items/bow_standby" + Q);
						jsonWriter.println("    },");
						// Display
						jsonWriter.println("    " + Q + "display" + Q + ": {");
						jsonWriter.println("        " + Q + "thirdperson_righthand" + Q + ": {");
						jsonWriter.println("            " + Q + "rotation" + Q + ": [ -80, 260, -40 ],");
						jsonWriter.println("            " + Q + "translation" + Q + ": [ -1, -2, 2.5 ],");
						jsonWriter.println("            " + Q + "scale" + Q + ": [ 0.9, 0.9, 0.9 ]");
						jsonWriter.println("        },");
						jsonWriter.println("        " + Q + "thirdperson_lefthand" + Q + ": {");
						jsonWriter.println("            " + Q + "rotation" + Q + ": [ -80, -280, -40 ],");
						jsonWriter.println("            " + Q + "translation" + Q + ": [ -1, -2, 2.5 ],");
						jsonWriter.println("            " + Q + "scale" + Q + ": [ 0.9, 0.9, 0.9 ]");
						jsonWriter.println("        },");
						jsonWriter.println("        " + Q + "firstperson_righthand" + Q + ": {");
						jsonWriter.println("            " + Q + "rotation" + Q + ": [ 0, -90, 25 ],");
						jsonWriter.println("            " + Q + "translation" + Q + ": [ 1.13, 3.2, 1.13 ],");
						jsonWriter.println("            " + Q + "scale" + Q + ": [ 0.68, 0.68, 0.68 ]");
						jsonWriter.println("        },");
						jsonWriter.println("        " + Q + "firstperson_lefthand" + Q + ": {");
						jsonWriter.println("            " + Q + "rotation" + Q + ": [ 0, 90, -25 ],");
						jsonWriter.println("            " + Q + "translation" + Q + ": [ 1.13, 3.2, 1.13 ],");
						jsonWriter.println("            " + Q + "scale" + Q + ": [ 0.68, 0.68, 0.68 ]");
						jsonWriter.println("        }");
						jsonWriter.println("    },");
						// The interesting part...
						jsonWriter.println("    " + Q + "overrides" + Q + ": [");

						jsonWriter.println("        { " + Q + "predicate" + Q + ": { " + Q + "pulling" + Q + ": 1 }, "
								+ Q + "model" + Q + ": " + Q + "item/bow_pulling_0" + Q + "},");
						jsonWriter.println("        { " + Q + "predicate" + Q + ": { " + Q + "pulling" + Q + ": 1, " + Q
								+ "pull" + Q + ": 0.65 }, " + Q + "model" + Q + ": " + Q + "item/bow_pulling_1" + Q
								+ "},");
						jsonWriter.println("        { " + Q + "predicate" + Q + ": { " + Q + "pulling" + Q + ": 1, " + Q
								+ "pull" + Q + ": 0.9 }, " + Q + "model" + Q + ": " + Q + "item/bow_pulling_2" + Q
								+ "},");

						for (DurabilityClaim claim : claims) {
							jsonWriter.println("        { " + Q + "predicate" + Q + ": {" + Q + "damaged" + Q + ": 0, "
									+ Q + "damage" + Q + ": "
									+ (double) claim.itemDamage / itemType.getMaxDurability() + "}, " + Q
									+ "model" + Q + ": " + Q + claim.resourcePath + Q + "},");
							List<BowTextures.Entry> pullTextures = claim.bowTextures.getPullTextures();
							int counter = 0;
							for (BowTextures.Entry pullTexture : pullTextures) {
								jsonWriter.println("        { " + Q + "predicate" + Q + ": {" + Q + "damaged" + Q
										+ ": 0, " + Q + "damage" + Q + ": "
										+ (double) claim.itemDamage / itemType.getMaxDurability() + ", "
										+ Q + "pulling" + Q + ": 1, " + Q + "pull" + Q + ": " + pullTexture.getPull()
										+ "}, " + Q + "model" + Q + ": " + Q + claim.resourcePath
										+ "_pulling_" + counter++ + Q + "},");
							}
						}
						// End of the json file
						jsonWriter.println("        { \"predicate\": {\"damaged\": 1, \"damage\": 0}, \"model\": \"item/" + modelName + "\"},");
						jsonWriter.println("        { \"predicate\": {\"damaged\": 1, \"damage\": 0, \"pulling\": 1 }, \"model\": \"item/" + modelName + "_pulling_0\"},");
						jsonWriter.println("        { \"predicate\": {\"damaged\": 1, \"damage\": 0, \"pulling\": 1, \"pull\": 0.65 }, \"model\": \"item/" + modelName + "_pulling_1\"},");
						jsonWriter.println("        { \"predicate\": {\"damaged\": 1, \"damage\": 0, \"pulling\": 1, \"pull\": 0.9 }, \"model\": \"item/" + modelName + "_pulling_2\"}");
						jsonWriter.println("    ]");
						jsonWriter.println("}");
					} else if (itemType == CustomItemType.SHIELD) {
						
						// The beginning
						jsonWriter.println("{");
						jsonWriter.println("    \"parent\": \"builtin/entity\",");
						jsonWriter.println("    \"display\": {");
						
						// All the display stuff, it's copied from minecrafts default shield model
						jsonWriter.println("        \"thirdperson_righthand\": {");
						jsonWriter.println("            \"rotation\": [0,90,0],");
						jsonWriter.println("            \"translation\": [10.51,6,-4],");
						jsonWriter.println("            \"scale\": [1,1,1]");
						jsonWriter.println("        },");
						jsonWriter.println("        \"thirdperson_lefthand\": {");
						jsonWriter.println("            \"rotation\": [0,90,0],");
						jsonWriter.println("            \"translation\": [10.51,6,12],");
						jsonWriter.println("            \"scale\": [1,1,1]");
						jsonWriter.println("        },");
						jsonWriter.println("        \"firstperson_righthand\": {");
						jsonWriter.println("            \"rotation\": [0,180,5],");
						jsonWriter.println("            \"translation\": [-10,2,-10],");
						jsonWriter.println("            \"scale\": [1.25,1.25,1.25]");
						jsonWriter.println("        },");
						jsonWriter.println("        \"firstperson_lefthand\": {");
						jsonWriter.println("            \"rotation\": [0,180,5],");
						jsonWriter.println("            \"translation\": [10,0,-10],");
						jsonWriter.println("            \"scale\": [1.25,1.25,1.25]");
						jsonWriter.println("        },");
						jsonWriter.println("        \"gui\": {");
						jsonWriter.println("            \"rotation\": [15,-25,-5],");
						jsonWriter.println("            \"translation\": [2,3,0],");
						jsonWriter.println("            \"scale\": [0.65,0.65,0.65]");
						jsonWriter.println("        },");
						jsonWriter.println("        \"fixed\": {");
						jsonWriter.println("            \"rotation\": [0,180,0],");
						jsonWriter.println("            \"translation\": [-2,4,-5],");
						jsonWriter.println("            \"scale\": [0.5,0.5,0.5]");
						jsonWriter.println("        },");
						jsonWriter.println("        \"ground\": {");
						jsonWriter.println("            \"rotation\": [0,0,0],");
						jsonWriter.println("            \"translation\": [4,4,2],");
						jsonWriter.println("            \"scale\": [0.25,0.25,0.25]");
						jsonWriter.println("        }");
						jsonWriter.println("    }, \"overrides\": [");
						
						// The next entry is part of preserving vanilla shield blocking model
						jsonWriter.println("        { \"predicate\": { \"blocking\": 1 }, \"model\": \"item/shield_blocking\" },");
						
						// Now the part for the custom shield predicates...
						for (DurabilityClaim claim : claims) {
							jsonWriter.println("        { \"predicate\": { \"blocking\": 0, \"damaged\": 0, \"damage\": "
									+ (double) claim.itemDamage / itemType.getMaxDurability() + " }, \"model\": \"" + claim.resourcePath + "\" },");
							jsonWriter.println("        { \"predicate\": { \"blocking\": 1, \"damaged\": 0, \"damage\": "
									+ (double) claim.itemDamage / itemType.getMaxDurability() + " }, \"model\": \"" + claim.resourcePath + "_blocking\" },");
						}
						
						// The next ones are required to preserve the vanilla shield models
						jsonWriter.println("        { \"predicate\": { \"blocking\": 0, \"damaged\": 1, \"damage\": 0 }, \"model\": \"item/shield\" },");
						jsonWriter.println("        { \"predicate\": { \"blocking\": 1, \"damaged\": 1, \"damage\": 0 }, \"model\": \"item/shield_blocking\" }");
						
						// Now finish the json
						jsonWriter.println("    ]");
						jsonWriter.println("}");
					} else {
						// Begin of the json file
						jsonWriter.println("{");
						jsonWriter.println("    " + Q + "parent" + Q + ": " + Q + "item/handheld" + Q + ",");
						jsonWriter.println("    " + Q + "textures" + Q + ": {");
						jsonWriter.print("        " + Q + "layer0" + Q + ": " + Q + "items/" + textureName + Q);
						boolean isLeatherArmor = entry.getKey().isLeatherArmor();
						if (isLeatherArmor) {
							jsonWriter.print(",");
						}
						jsonWriter.println();
						if (isLeatherArmor) {
							jsonWriter.print("        " + Q + "layer1" + Q + ": " + Q + "items/" + textureName + "_overlay" + Q);
						}
						jsonWriter.println("    },");
						jsonWriter.println("    " + Q + "overrides" + Q + ": [");

						// Now the interesting part
						for (DurabilityClaim claim : claims) {
							jsonWriter.println("        { " + Q + "predicate" + Q + ": {" + Q + "damaged" + Q + ": 0, "
									+ Q + "damage" + Q + ": "
									+ (double) claim.itemDamage / itemType.getMaxDurability() + "}, " + Q
									+ "model" + Q + ": " + Q + claim.resourcePath + Q + "},");
						}

						// End of the json file
						jsonWriter.println("        { " + Q + "predicate" + Q + ": {" + Q + "damaged" + Q + ": 1, " + Q
								+ "damage" + Q + ": 0}, " + Q + "model" + Q + ": " + Q + "item/" + modelName + Q + "}");
						jsonWriter.println("    ]");
						jsonWriter.println("}");
					}
					jsonWriter.flush();
					zipOutput.closeEntry();
				}
			}

			// pack.mcmeta
			ZipEntry mcMeta = new ZipEntry("pack.mcmeta");
			zipOutput.putNextEntry(mcMeta);
			PrintWriter jsonWriter = new PrintWriter(zipOutput);
			jsonWriter.println("{");
			jsonWriter.println("    " + Q + "pack" + Q + ": {");
			jsonWriter.println("        " + Q + "pack_format" + Q + ": 3,");
			jsonWriter.println("        " + Q + "description" + Q + ": " + Q + "CustomItemSet" + Q);
			jsonWriter.println("    }");
			jsonWriter.println("}");
			jsonWriter.flush();
			zipOutput.closeEntry();

			zipOutput.close();
			return null;
		} catch (IOException ioex) {
			ioex.printStackTrace();
			return ioex.getMessage();
		}
	}

	@SuppressWarnings("unused")
	private void export1(BitOutput output) {
		output.addByte(ENCODING_1);

		// Items
		output.addInt(items.size());
		for (CustomItem item : items)
			item.export(output);

		// Recipes
		output.addInt(recipes.size());
		for (Recipe recipe : recipes)
			recipe.save(output);
		
		// There are no drops in this encoding
	}
	
	// Because ENCODING_2 didn't break anything in the plug-in, it's editor-only
	
	@SuppressWarnings("unused")
	private void export3(BitOutput output) {
		output.addByte(ENCODING_3);

		// Items
		output.addInt(items.size());
		for (CustomItem item : items)
			item.export(output);

		// Recipes
		output.addInt(recipes.size());
		for (Recipe recipe : recipes)
			recipe.save(output);
		
		// Drops
		output.addInt(blockDrops.size());
		for (BlockDrop drop : blockDrops)
			drop.save(output, result -> ((Result) result).save(output));
		
		output.addInt(mobDrops.size());
		for (EntityDrop drop : mobDrops)
			drop.save(output, result -> ((Result) result).save(output));
	}
	
	// ENCODING_4 is editor-only, so it doesn't have its own export method
	
	@SuppressWarnings("unused")
	private void export5(BitOutput output) {
		output.addByte(ENCODING_5);
		
		// Projectiles
		output.addInt(projectileCovers.size());
		for (EditorProjectileCover cover : projectileCovers)
			cover.export(output);
		
		output.addInt(projectiles.size());
		for (CIProjectile projectile : projectiles)
			projectile.toBits(output);

		// Items
		output.addInt(items.size());
		for (CustomItem item : items)
			item.export(output);

		// Recipes
		output.addInt(recipes.size());
		for (Recipe recipe : recipes)
			recipe.save(output);
		
		// Drops
		output.addInt(blockDrops.size());
		for (BlockDrop drop : blockDrops)
			drop.save(output, result -> ((Result) result).save(output));
		
		output.addInt(mobDrops.size());
		for (EntityDrop drop : mobDrops)
			drop.save(output, result -> ((Result) result).save(output));
	}
	
	// Add integrity checks
	@SuppressWarnings("unused")
	private void export6(BitOutput outerOutput) {
		outerOutput.addByte(ENCODING_6);
		
		ByteArrayBitOutput output = new ByteArrayBitOutput();
		
		// Projectiles
		output.addInt(projectileCovers.size());
		for (EditorProjectileCover cover : projectileCovers)
			cover.export(output);
		
		output.addInt(projectiles.size());
		for (CIProjectile projectile : projectiles)
			projectile.toBits(output);

		// Items
		output.addInt(items.size());

		// Tools can have non-tools as repair item, so the non-tools must be exported first.
		// This way, that all repair items are available once the tools are being loaded.
		for (CustomItem noTool : items)
			if (!(noTool instanceof CustomTool))
				noTool.export(output);

		for (CustomItem tool : items)
			if (tool instanceof CustomTool)
				tool.export(output);

		// Recipes
		output.addInt(recipes.size());
		for (Recipe recipe : recipes)
			recipe.save(output);
		
		// Drops
		output.addInt(blockDrops.size());
		for (BlockDrop drop : blockDrops)
			drop.save(output, result -> ((Result) result).save(output));
		
		output.addInt(mobDrops.size());
		for (EntityDrop drop : mobDrops)
			drop.save(output, result -> ((Result) result).save(output));
		
		// Finish the integrity stuff
		byte[] contentBytes = output.getBytes();
		outerOutput.addLong(hash(contentBytes));
		outerOutput.addByteArray(contentBytes);
	}
	
	// Add custom containers
	@SuppressWarnings("unused")
	private void export7(BitOutput outerOutput) {
		outerOutput.addByte(ENCODING_7);
		
		ByteArrayBitOutput output = new ByteArrayBitOutput();
		
		// Projectiles
		output.addInt(projectileCovers.size());
		for (EditorProjectileCover cover : projectileCovers)
			cover.export(output);
		
		output.addInt(projectiles.size());
		for (CIProjectile projectile : projectiles)
			projectile.toBits(output);

		// Items
		output.addInt(items.size());

		// Tools can have non-tools as repair item, so the non-tools must be exported first.
		// This way, that all repair items are available once the tools are being loaded.
		for (CustomItem noTool : items)
			if (!(noTool instanceof CustomTool))
				noTool.export(output);

		for (CustomItem tool : items)
			if (tool instanceof CustomTool)
				tool.export(output);

		// Recipes
		output.addInt(recipes.size());
		for (Recipe recipe : recipes)
			recipe.save(output);
		
		// Drops
		output.addInt(blockDrops.size());
		for (BlockDrop drop : blockDrops)
			drop.save(output, result -> ((Result) result).save(output));
		
		output.addInt(mobDrops.size());
		for (EntityDrop drop : mobDrops)
			drop.save(output, result -> ((Result) result).save(output));
		
		// Fuel registries
		output.addInt(fuelRegistries.size());
		for (CustomFuelRegistry registry : fuelRegistries)
			registry.save(output, scIngredient -> ((Ingredient)scIngredient).save(output));
		
		// Custom containers
		output.addInt(containers.size());
		for (CustomContainer container : containers) {
			container.save(output, 
					ingredient -> ((Ingredient)ingredient).save(output),
					result -> ((Result)result).save(output)
			);
		}
		
		// Finish the integrity stuff
		byte[] contentBytes = output.getBytes();
		outerOutput.addLong(hash(contentBytes));
		outerOutput.addByteArray(contentBytes);
	}
	
	// Add export time and remember names of deleted items
	private void export8(BitOutput outerOutput) {
		outerOutput.addByte(ENCODING_8);
		
		ByteArrayBitOutput output = new ByteArrayBitOutput();
		
		// Export time
		output.addLong(System.currentTimeMillis());
		
		// Projectiles
		output.addInt(projectileCovers.size());
		for (EditorProjectileCover cover : projectileCovers)
			cover.export(output);
		
		output.addInt(projectiles.size());
		for (CIProjectile projectile : projectiles)
			projectile.toBits(output);

		// Items
		output.addInt(items.size());

		// Tools can have non-tools as repair item, so the non-tools must be exported first.
		// This way, that all repair items are available once the tools are being loaded.
		for (CustomItem noTool : items)
			if (!(noTool instanceof CustomTool))
				noTool.export(output);

		for (CustomItem tool : items)
			if (tool instanceof CustomTool)
				tool.export(output);

		// Recipes
		output.addInt(recipes.size());
		for (Recipe recipe : recipes)
			recipe.save(output);
		
		// Drops
		output.addInt(blockDrops.size());
		for (BlockDrop drop : blockDrops)
			drop.save(output, result -> ((Result) result).save(output));
		
		output.addInt(mobDrops.size());
		for (EntityDrop drop : mobDrops)
			drop.save(output, result -> ((Result) result).save(output));
		
		// Fuel registries
		output.addInt(fuelRegistries.size());
		for (CustomFuelRegistry registry : fuelRegistries)
			registry.save(output, scIngredient -> ((Ingredient)scIngredient).save(output));
		
		// Custom containers
		output.addInt(containers.size());
		for (CustomContainer container : containers) {
			container.save(output, 
					ingredient -> ((Ingredient)ingredient).save(output),
					result -> ((Result)result).save(output)
			);
		}
		
		// Deleted item names
		output.addInt(deletedItems.size());
		for (String deletedItem : deletedItems) {
			output.addString(deletedItem);
		}
		
		// Finish the integrity stuff
		byte[] contentBytes = output.getBytes();
		outerOutput.addLong(hash(contentBytes));
		outerOutput.addByteArray(contentBytes);
	}

	public String save() {
		try {
			Editor.getFolder().mkdir();
			Editor.getBackupFolder().mkdir();
			File file = new File(Editor.getFolder() + "/" + fileName + ".cisb");// cisb stands for Custom Item Set
																				// Builder
			ByteArrayBitOutput output = new ByteArrayBitOutput();
			save8(output);
			output.terminate();
			byte[] bytes = output.getBytes();
			OutputStream mainOutput = Files.newOutputStream(file.toPath());
			mainOutput.write(bytes);
			mainOutput.flush();
			mainOutput.close();
			OutputStream backupOutput = Files.newOutputStream(
					new File(Editor.getBackupFolder() + "/" + fileName + " " + System.currentTimeMillis() + ".cisb").toPath());
			backupOutput.write(bytes);
			mainOutput.flush();
			backupOutput.close();
			return null;
		} catch (IOException ioex) {
			ioex.printStackTrace();
			return ioex.getMessage();
		}
		
	}

	@SuppressWarnings("unused")
	private void save1(BitOutput output) throws IOException {
		output.addByte(ENCODING_1);
		output.addInt(textures.size());
		for (NamedImage texture : textures)
			texture.save(output, false);
		output.addInt(items.size());

		// Save the normal items before the tools so that tools can use normal items as
		// repair item
		List<CustomItem> sorted = new ArrayList<CustomItem>(items.size());
		for (CustomItem item : items) {
			if (!(item instanceof CustomTool)) {
				sorted.add(item);
			}
		}
		for (CustomItem item : items) {
			if (item instanceof CustomTool) {
				sorted.add(item);
			}
		}
		for (CustomItem item : sorted)
			item.save1(output);

		output.addInt(recipes.size());
		for (Recipe recipe : recipes)
			recipe.save(output);
	}

	@SuppressWarnings("unused")
	private void save2(BitOutput output) throws IOException {
		output.addByte(ENCODING_2);
		output.addInt(textures.size());
		for (NamedImage texture : textures) {
			if (texture instanceof BowTextures)
				output.addByte(NamedImage.ENCODING_BOW);
			else
				output.addByte(NamedImage.ENCODING_SIMPLE);
			texture.save(output, false);
		}
		output.addInt(items.size());

		// Save the normal items before the tools so that tools can use normal items as
		// repair item
		List<CustomItem> sorted = new ArrayList<CustomItem>(items.size());
		for (CustomItem item : items) {
			if (!(item instanceof CustomTool)) {
				sorted.add(item);
			}
		}
		for (CustomItem item : items) {
			if (item instanceof CustomTool) {
				sorted.add(item);
			}
		}
		for (CustomItem item : sorted)
			item.save1(output);

		output.addInt(recipes.size());
		for (Recipe recipe : recipes)
			recipe.save(output);
	}
	
	@SuppressWarnings("unused")
	private void save3(BitOutput output) throws IOException {
		output.addByte(ENCODING_3);
		output.addInt(textures.size());
		for (NamedImage texture : textures) {
			if (texture instanceof BowTextures)
				output.addByte(NamedImage.ENCODING_BOW);
			else
				output.addByte(NamedImage.ENCODING_SIMPLE);
			texture.save(output, false);
		}
		output.addInt(items.size());

		// Save the normal items before the tools so that tools can use normal items as
		// repair item
		List<CustomItem> sorted = new ArrayList<CustomItem>(items.size());
		for (CustomItem item : items) {
			if (!(item instanceof CustomTool)) {
				sorted.add(item);
			}
		}
		for (CustomItem item : items) {
			if (item instanceof CustomTool) {
				sorted.add(item);
			}
		}
		for (CustomItem item : sorted)
			item.save1(output);

		output.addInt(recipes.size());
		for (Recipe recipe : recipes)
			recipe.save(output);
		
		output.addInt(blockDrops.size());
		for (BlockDrop drop : blockDrops)
			drop.save(output, result -> ((Result) result).save(output));
		
		output.addInt(mobDrops.size());
		for (EntityDrop drop : mobDrops)
			drop.save(output, result -> ((Result) result).save(output));
	}
	
	// Use CustomItem.save2 instead of CustomItem.save1
	@SuppressWarnings("unused")
	private void save4(BitOutput output) throws IOException {
		output.addByte(ENCODING_4);
		output.addInt(textures.size());
		for (NamedImage texture : textures) {
			if (texture instanceof BowTextures)
				output.addByte(NamedImage.ENCODING_BOW);
			else
				output.addByte(NamedImage.ENCODING_SIMPLE);
			texture.save(output, false);
		}
		output.addInt(items.size());

		// Save the normal items before the tools so that tools can use normal items as
		// repair item
		List<CustomItem> sorted = new ArrayList<CustomItem>(items.size());
		for (CustomItem item : items) {
			if (!(item instanceof CustomTool)) {
				sorted.add(item);
			}
		}
		for (CustomItem item : items) {
			if (item instanceof CustomTool) {
				sorted.add(item);
			}
		}
		for (CustomItem item : sorted)
			item.save2(output);

		output.addInt(recipes.size());
		for (Recipe recipe : recipes)
			recipe.save(output);
		
		output.addInt(blockDrops.size());
		for (BlockDrop drop : blockDrops)
			drop.save(output, result -> ((Result) result).save(output));
		
		output.addInt(mobDrops.size());
		for (EntityDrop drop : mobDrops)
			drop.save(output, result -> ((Result) result).save(output));
	}
	
	// Add projectiles
	@SuppressWarnings("unused")
	private void save5(BitOutput output) throws IOException {
		output.addByte(ENCODING_5);
		output.addInt(textures.size());
		for (NamedImage texture : textures) {
			if (texture instanceof BowTextures)
				output.addByte(NamedImage.ENCODING_BOW);
			else
				output.addByte(NamedImage.ENCODING_SIMPLE);
			texture.save(output, false);
		}
		
		output.addInt(projectileCovers.size());
		for (EditorProjectileCover cover : projectileCovers)
			cover.toBits(output);
		
		output.addInt(projectiles.size());
		for (CIProjectile projectile : projectiles)
			projectile.toBits(output);
		
		output.addInt(items.size());

		// Save the normal items before the tools so that tools can use normal items as
		// repair item
		List<CustomItem> sorted = new ArrayList<CustomItem>(items.size());
		for (CustomItem item : items) {
			if (!(item instanceof CustomTool)) {
				sorted.add(item);
			}
		}
		for (CustomItem item : items) {
			if (item instanceof CustomTool) {
				sorted.add(item);
			}
		}
		for (CustomItem item : sorted)
			item.save2(output);

		output.addInt(recipes.size());
		for (Recipe recipe : recipes)
			recipe.save(output);
		
		output.addInt(blockDrops.size());
		for (BlockDrop drop : blockDrops)
			drop.save(output, result -> ((Result) result).save(output));
		
		output.addInt(mobDrops.size());
		for (EntityDrop drop : mobDrops)
			drop.save(output, result -> ((Result) result).save(output));
	}
	
	// Add integrity check
	@SuppressWarnings("unused")
	private void save6(BitOutput outerOutput) throws IOException {
		outerOutput.addByte(ENCODING_6);
		
		// Prepare integrity
		ByteArrayBitOutput output = new ByteArrayBitOutput();
		
		output.addInt(textures.size());
		for (NamedImage texture : textures) {
			if (texture instanceof BowTextures)
				output.addByte(NamedImage.ENCODING_BOW);
			else
				output.addByte(NamedImage.ENCODING_SIMPLE);
			texture.save(output, false);
		}
		
		output.addInt(projectileCovers.size());
		for (EditorProjectileCover cover : projectileCovers)
			cover.toBits(output);
		
		output.addInt(projectiles.size());
		for (CIProjectile projectile : projectiles)
			projectile.toBits(output);
		
		output.addInt(items.size());

		// Save the normal items before the tools so that tools can use normal items as
		// repair item
		List<CustomItem> sorted = new ArrayList<CustomItem>(items.size());
		for (CustomItem item : items) {
			if (!(item instanceof CustomTool)) {
				sorted.add(item);
			}
		}
		for (CustomItem item : items) {
			if (item instanceof CustomTool) {
				sorted.add(item);
			}
		}
		for (CustomItem item : sorted)
			item.save2(output);

		output.addInt(recipes.size());
		for (Recipe recipe : recipes)
			recipe.save(output);
		
		output.addInt(blockDrops.size());
		for (BlockDrop drop : blockDrops)
			drop.save(output, result -> ((Result) result).save(output));
		
		output.addInt(mobDrops.size());
		for (EntityDrop drop : mobDrops)
			drop.save(output, result -> ((Result) result).save(output));
		
		// Finish the integrity work
		byte[] contentBytes = output.getBytes();
		outerOutput.addLong(hash(contentBytes));
		outerOutput.addByteArray(contentBytes);
	}
	
	// Add custom containers
	@SuppressWarnings("unused")
	private void save7(BitOutput outerOutput) throws IOException {
		outerOutput.addByte(ENCODING_7);
		
		// Prepare integrity
		ByteArrayBitOutput output = new ByteArrayBitOutput();
		
		output.addInt(textures.size());
		for (NamedImage texture : textures) {
			if (texture instanceof BowTextures)
				output.addByte(NamedImage.ENCODING_BOW);
			else
				output.addByte(NamedImage.ENCODING_SIMPLE);
			texture.save(output, false);
		}
		
		output.addInt(projectileCovers.size());
		for (EditorProjectileCover cover : projectileCovers)
			cover.toBits(output);
		
		output.addInt(projectiles.size());
		for (CIProjectile projectile : projectiles)
			projectile.toBits(output);
		
		output.addInt(items.size());

		// Save the normal items before the tools so that tools can use normal items as
		// repair item
		List<CustomItem> sorted = new ArrayList<CustomItem>(items.size());
		for (CustomItem item : items) {
			if (!(item instanceof CustomTool)) {
				sorted.add(item);
			}
		}
		for (CustomItem item : items) {
			if (item instanceof CustomTool) {
				sorted.add(item);
			}
		}
		for (CustomItem item : sorted)
			item.save2(output);

		output.addInt(recipes.size());
		for (Recipe recipe : recipes)
			recipe.save(output);
		
		output.addInt(blockDrops.size());
		for (BlockDrop drop : blockDrops)
			drop.save(output, result -> ((Result) result).save(output));
		
		output.addInt(mobDrops.size());
		for (EntityDrop drop : mobDrops)
			drop.save(output, result -> ((Result) result).save(output));
		
		output.addInt(fuelRegistries.size());
		for (CustomFuelRegistry registry : fuelRegistries)
			registry.save(output, scIngredient -> ((Ingredient)scIngredient).save(output));
		
		output.addInt(containers.size());
		for (CustomContainer container : containers) {
			container.save(output, 
					ingredient -> ((Ingredient)ingredient).save(output),
					result -> ((Result)result).save(output)
			);
		}
		
		// Finish the integrity work
		byte[] contentBytes = output.getBytes();
		outerOutput.addLong(hash(contentBytes));
		outerOutput.addByteArray(contentBytes);
	}
	
	// Add custom containers
	private void save8(BitOutput outerOutput) throws IOException {
		outerOutput.addByte(ENCODING_8);
		
		// BooleanArrayBitOutput requires more memory, but is also much faster
		// than ByteArrayBitOutput
		BooleanArrayBitOutput output = new BooleanArrayBitOutput();
		
		output.addInt(textures.size());
		for (NamedImage texture : textures) {
			if (texture instanceof BowTextures)
				output.addByte(NamedImage.ENCODING_BOW);
			else
				output.addByte(NamedImage.ENCODING_SIMPLE);
			texture.save(output, true);
		}
		
		output.addInt(armorTextures.size());
		for (Reference<ArmorTextures> textureRef : armorTextures) {
			textureRef.get().save(output);
		}
		
		output.addInt(projectileCovers.size());
		for (EditorProjectileCover cover : projectileCovers)
			cover.toBits(output);
		
		output.addInt(projectiles.size());
		for (CIProjectile projectile : projectiles)
			projectile.toBits(output);
		
		output.addInt(items.size());

		// Save the normal items before the tools so that tools can use normal items as
		// repair item
		List<CustomItem> sorted = new ArrayList<CustomItem>(items.size());
		for (CustomItem item : items) {
			if (!(item instanceof CustomTool)) {
				sorted.add(item);
			}
		}
		for (CustomItem item : items) {
			if (item instanceof CustomTool) {
				sorted.add(item);
			}
		}
		for (CustomItem item : sorted)
			item.save2(output);

		output.addInt(recipes.size());
		for (Recipe recipe : recipes)
			recipe.save(output);
		
		output.addInt(blockDrops.size());
		for (BlockDrop drop : blockDrops)
			drop.save(output, result -> ((Result) result).save(output));
		
		output.addInt(mobDrops.size());
		for (EntityDrop drop : mobDrops)
			drop.save(output, result -> ((Result) result).save(output));
		
		output.addInt(fuelRegistries.size());
		for (CustomFuelRegistry registry : fuelRegistries)
			registry.save(output, scIngredient -> ((Ingredient)scIngredient).save(output));
		
		output.addInt(containers.size());
		for (CustomContainer container : containers) {
			container.save(output, 
					ingredient -> ((Ingredient)ingredient).save(output),
					result -> ((Result)result).save(output)
			);
		}
		
		output.addInt(deletedItems.size());
		for (String deletedItem : deletedItems) {
			output.addString(deletedItem);
		}
		
		// Finish the integrity work
		byte[] contentBytes = output.getBytes();
		outerOutput.addLong(hash(contentBytes));
		outerOutput.addByteArray(contentBytes);
	}

	/**
	 * Attempts to add the specified texture to this item set. If the texture can be
	 * added, it will be added. If the texture can't be added, the reason is
	 * returned.
	 * 
	 * @param texture The texture that should be added to this item set
	 * @return The reason the texture could not be added, or null if the texture was
	 *         added successfully
	 */
	public String addTexture(NamedImage texture, boolean checkClass) {
		if (!bypassChecks()) {
			if (texture == null)
				return "Can't add null textures";
			if (checkClass && texture.getClass() != NamedImage.class)
				return "Use the right method for that class";
			String nameError = checkName(texture.getName());
			if (nameError != null)
				return nameError;
			if (hasTexture(texture.getName()))
				return "There is already a texture with that name";
		}
		textures.add(texture);
		return null;
	}

	/**
	 * Attempts to change the specified texture in this item set. If the texture can
	 * be changed, it will be changed. if the texture can't be changed, the reason
	 * is returned.
	 * 
	 * @param texture  The texture to change
	 * @param newName  The new name of the texture (possibly the old name)
	 * @param newImage The new image of the texture (possibly the old image)
	 * @return The reason the texture could not be changed, or null if the texture
	 *         changed successfully
	 */
	public String changeTexture(NamedImage texture, String newName, BufferedImage newImage, boolean checkClass) {
		if (!bypassChecks()) {
			if (texture == null)
				return "Can't change null textures";
			if (checkClass && texture.getClass() != NamedImage.class)
				return "Use the appropriate method for the class of that texture";
			String nameError = checkName(newName);
			if (nameError != null)
				return nameError;
			NamedImage sameName = getTextureByName(newName);
			if (sameName != null && sameName != texture)
				return "Another texture with that name already exists";
			if (newImage == null)
				return "You need to select an image";
			if (!textures.contains(texture))
				return "The previous texture is not in the list!";
		}
		texture.setName(newName);
		texture.setImage(newImage);
		return null;
	}

	/**
	 * Attempts to remove the specified texture from this item set. If the texture
	 * could not be removed, the reason is returned. If the texture could be
	 * removed, it will be removed.
	 * 
	 * @param texture The texture that should be removed from this set
	 * @return The reason the texture could not be removed, or null if the texture
	 *         was removed successfully.
	 */
	public String removeTexture(NamedImage texture) {
		if (!bypassChecks()) {
			boolean has = false;
			for (NamedImage current : textures) {
				if (current == texture) {
					has = true;
					break;
				}
			}
			if (!has)
				return "That texture is not in this item set.";
			for (CustomItem item : items)
				if (item.getTexture() == texture)
					return "That texture is used by " + item.getName();
		}
		textures.remove(texture);
		return null;
	}

	/**
	 * Attempts to add the specified bow texture to this item set. If the texture
	 * can be added, it will be added. If the texture can't be added, the reason is
	 * returned.
	 * 
	 * @param texture    The texture that should be added to this item set
	 * @param checkClass True if the class must be BowTextures.class, false if it
	 *                   can be a subclass as well
	 * @return The reason the texture could not be added, or null if it was added
	 *         succesfully
	 */
	public String addBowTexture(BowTextures texture, boolean checkClass) {
		if (!bypassChecks()) {
			if (texture == null)
				return "Can't add null textures";
			if (checkClass && texture.getClass() != BowTextures.class)
				return "Use the right method for this class";
			String nameError = checkName(texture.getName());
			if (nameError != null)
				return nameError;
			for (NamedImage current : textures)
				if (current.getName().equals(texture.getName()))
					return "There is already a texture with that name";
			List<BowTextures.Entry> pullEntries = texture.getPullTextures();
			for (BowTextures.Entry pullEntry : pullEntries) {
				if (pullEntry.getTexture() == null) {
					return "Pull " + pullEntry.getPull() + " doesn't have a texture";
				}
				if (pullEntry.getPull() < 0 || pullEntry.getPull() > 1) {
					return "All pulls must be between 0 and 1";
				}
			}
		}
		return addTexture(texture, false);
	}

	public String changeBowTexture(BowTextures current, String newName, BufferedImage newTexture,
			List<BowTextures.Entry> newPullTextures, boolean checkClass) {
		if (!bypassChecks()) {
			if (current == null)
				return "Can't change null textures";
			if (checkClass && current.getClass() != BowTextures.class)
				return "Use the right method for that class";
			for (BowTextures.Entry pullTexture : newPullTextures) {
				if (pullTexture.getTexture() == null) {
					return "There is no texture for pull " + pullTexture.getPull();
				}
				if (pullTexture.getPull() < 0 || pullTexture.getPull() > 1) {
					return "All pulls must be between 0 and 1";
				}
			}
		}
		String error = changeTexture(current, newName, newTexture, false);
		if (error == null) {
			current.setEntries(newPullTextures);
		}
		return error;
	}
	
	/**
	 * Attempts to add the specified armor piece to this item set. If the piece can be added,
	 * it will be added. If the armor piece can't be added, the reason is returned.
	 * 
	 * @param item The armor piece that should be added to this item set
	 * @return The reason the piece could not be added or null if it was added
	 *         successfully
	 */
	public String addArmor(CustomArmor item, boolean checkClass) {
		if (!bypassChecks()) {
			if (item == null)
				return "Can't add null items";
			if (checkClass && item.getClass() != CustomArmor.class)
				return "Use the appropriate method for that class";
			if (item.getRed() < 0 || item.getRed() > 255)
				return "Red (" + item.getRed() + ") is out of range";
			if (item.getGreen() < 0 || item.getGreen() > 255)
				return "Green (" + item.getGreen() + ") is out of range";
			if (item.getBlue() < 0 || item.getBlue() > 255)
				return "Blue (" + item.getBlue() + ") is out of range";
		}
		return addTool(item, false);
	}
	
	public String changeArmor(
			CustomArmor armor, CustomItemType newType, String newAlias, 
			String newDisplayName, String[] newLore, 
			AttributeModifier[] newAttributes, Enchantment[] newEnchantments, 
			boolean allowEnchanting, boolean allowAnvil, Ingredient repairItem, 
			long newDurability, NamedImage newTexture, int newRed, int newGreen, 
			int newBlue, boolean[] itemFlags, int entityHitDurabilityLoss, 
			int blockBreakDurabilityLoss, DamageResistances resistances, 
			byte[] newCustomModel, List<PotionEffect> playerEffects, 
			List<PotionEffect> targetEffects, 
			Collection<EquippedPotionEffect> newEquippedEffects, String[] commands, 
			ReplaceCondition[] conditions, ConditionOperation op, 
			ExtraItemNbt newExtraNbt, 
			ReadOnlyReference<ArmorTextures> newWornTexture, float newAttackRange,
			boolean checkClass
	) {
		if (!bypassChecks()) {
			if (armor == null)
				return "Can't change armor pieces that do not exist";
			if (checkClass && armor.getClass() != CustomArmor.class)
				return "Use the appropriate method for the class";
			if (armor.getRed() < 0 || armor.getRed() > 255)
				return "Red (" + armor.getRed() + ") is out of range";
			if (armor.getGreen() < 0 || armor.getGreen() > 255)
				return "Green (" + armor.getGreen() + ") is out of range";
			if (armor.getBlue() < 0 || armor.getBlue() > 255)
				return "Blue (" + armor.getBlue() + ") is out of range";
			if (resistances == null)
				return "The damage resistances can't be null";
		}
		String error = changeTool(
				armor, newType, newAlias, newDisplayName, newLore, newAttributes, 
				newEnchantments, allowEnchanting, allowAnvil, repairItem, 
				newDurability, newTexture, itemFlags, entityHitDurabilityLoss, 
				blockBreakDurabilityLoss, newCustomModel, playerEffects, 
				targetEffects, newEquippedEffects, commands, conditions, op, 
				newExtraNbt, newAttackRange, false
		);
		if (error == null) {
			armor.setRed(newRed);
			armor.setGreen(newGreen);
			armor.setBlue(newBlue);
			armor.setDamageResistances(resistances);
			armor.setWornTexture(newWornTexture);
			return null;
		} else {
			return error;
		}
	}
	
	/**
	 * Attempts to add the specified 3d helmet to this item set. If the helmet can be added,
	 * it will be added. If the helmet can't be added, the reason is returned.
	 * 
	 * @param helmet The helmet that should be added to this item set
	 * @return The reason the helmet could not be added or null if it was added
	 *         successfully
	 */
	public String addHelmet3D(CustomHelmet3D helmet, boolean checkClass) {
		if (!bypassChecks()) {
			if (helmet == null) return "Can't add null helmets";
			if (helmet.getCustomModel() == null) 
				return "3d helmets must have a custom model";
			if (checkClass && helmet.getClass() != CustomHelmet3D.class)
				return "Use the appropriate method for the class";
		}
		return addArmor(helmet, false);
	}
	
	/**
	 * Attempts to change the properties of the given 3d helmet. If the helmet can
	 * be changed, it will be changed. If not (because some validation error
	 * occurred), the helmet won't be changed, and the reason will be returned as
	 * string.
	 * @return The reason the helmet couldn't be changed, or null if it was changed
	 * successfully
	 */
	public String changeHelmet3D(
			CustomHelmet3D helmet, CustomItemType newType, String newAlias,
			String newDisplayName, String[] newLore, 
			AttributeModifier[] newAttributes, Enchantment[] newEnchantments, 
			boolean allowEnchanting, boolean allowAnvil, Ingredient repairItem, 
			long newDurability, NamedImage newTexture, boolean[] itemFlags, 
			int entityHitDurabilityLoss, int blockBreakDurabilityLoss, 
			DamageResistances resistances, byte[] newCustomModel, 
			List<PotionEffect> playerEffects, List<PotionEffect> targetEffects, 
			Collection<EquippedPotionEffect> newEquippedEffects, String[] commands,
			ReplaceCondition[] newReplaceConditions, 
			ConditionOperation newConditionOp, ExtraItemNbt newExtraNbt,
			float newAttackRange, boolean checkClass
	) {
		if (!bypassChecks()) {
			if (helmet == null) return "Can't change null helmets";
			if (checkClass && helmet.getClass() != CustomHelmet3D.class)
				return "Use the appropriate method for the class of this helmet";
			if (newCustomModel == null)
				return "3d helmets must have a custom model";
		}
		String error = changeArmor(
				helmet, newType, newAlias, newDisplayName, newLore, newAttributes, 
				newEnchantments, allowEnchanting, allowAnvil, repairItem, 
				newDurability, newTexture, 0, 0, 0, itemFlags, 
				entityHitDurabilityLoss, blockBreakDurabilityLoss, resistances, 
				newCustomModel, playerEffects, targetEffects, newEquippedEffects,
				commands, newReplaceConditions, newConditionOp, newExtraNbt, 
				// 3d helmets never use a worn texture
				null, newAttackRange, false
		);
		
		// CustomHelmet3D doesn't have any properties CustomArmor doesn't have
		return error;
	}

	/**
	 * Attempts to add the specified bow to this item set. If the bow can be added,
	 * it will be added. If the bow can't be added, the reason is returned.
	 * 
	 * @param item The bow that should be added to this item set
	 * @return The reason the bow could not be added or null if it was added
	 *         successfully
	 */
	public String addBow(CustomBow item, boolean checkClass) {
		if (!bypassChecks()) {
			if (item == null)
				return "Can't add null items";
			if (checkClass && item.getClass() != CustomBow.class)
				return "Use the appropriate method for that class";
			if (item.getTexture() == null)
				return "Every item needs a texture";
			List<BowTextures.Entry> pullTextures = item.getTexture().getPullTextures();
			for (BowTextures.Entry pullTexture : pullTextures) {
				if (pullTexture == null)
					return "One of the pull textures is undefined";
				if (pullTexture.getTexture() == null)
					return "The texture for pull " + pullTexture.getPull() + " is undefined.";
			}
		}
		return addTool(item, false);
	}

	public String changeBow(
			CustomBow bow, String newAlias, String newDisplayName, String[] newLore,
			AttributeModifier[] newAttributes, Enchantment[] newEnchantments, 
			double newDamageMultiplier, double newSpeedMultiplier,
			int newKnockbackStrength, boolean useGravity, boolean allowEnchanting, 
			boolean allowAnvil, Ingredient repairItem, long newDurability, 
			BowTextures newTextures, boolean[] itemFlags,
			int entityHitDurabilityLoss, int blockBreakDurabilityLoss, 
			int shootDurabilityLoss, byte[] newCustomModel, 
			List<PotionEffect> playerEffects, List<PotionEffect> targetEffects, 
			Collection<EquippedPotionEffect> newEquippedEffects, String[] commands, 
			ReplaceCondition[] conditions, ConditionOperation op, 
			ExtraItemNbt newExtraNbt, float newAttackRange, boolean checkClass
	) {
		if (!bypassChecks()) {
			if (bow == null)
				return "Can't change bows that do not exist";
			if (checkClass && bow.getClass() != CustomBow.class)
				return "Use the appropriate method for the class";
			List<BowTextures.Entry> pullTextures = newTextures.getPullTextures();
			for (BowTextures.Entry pullTexture : pullTextures) {
				if (pullTexture == null)
					return "One of the pull textures is undefined";
				if (pullTexture.getTexture() == null)
					return "The texture for pull " + pullTexture.getPull() + " is undefined.";
			}
			if (shootDurabilityLoss < 0)
				return "The shoot durability loss can't be negative";
		}
		String error = changeTool(
				bow, CustomItemType.BOW, newAlias, newDisplayName, newLore, 
				newAttributes, newEnchantments, allowEnchanting, allowAnvil, 
				repairItem, newDurability, newTextures, itemFlags,
				entityHitDurabilityLoss, blockBreakDurabilityLoss, newCustomModel, 
				playerEffects, targetEffects, newEquippedEffects, 
				commands, conditions, op, newExtraNbt, newAttackRange, false
		);
		if (error == null) {
			bow.setDamageMultiplier(newDamageMultiplier);
			bow.setSpeedMultiplier(newSpeedMultiplier);
			bow.setKnockbackStrength(newKnockbackStrength);
			bow.setGravity(useGravity);
			bow.setShootDurabilityLoss(shootDurabilityLoss);
			return null;
		} else {
			return error;
		}
	}
	
	public String addShield(CustomShield shield, boolean checkClass) {
		if (!bypassChecks()) {
			if (shield == null)
				return "Can't add null items";
			if (checkClass && shield.getClass() != CustomShield.class)
				return "Use the appropriate method for that class!";
			double th = shield.getThresholdDamage();
			if (th < 0)
				return "The threshold damage can't be negative";
			if (th != th)
				return "The threshold damage can't be NaN";
		}
		return addTool(shield, false);
	}
	
	public String changeShield(
			CustomShield shield, String newAlias, String newDisplayName, 
			String[] newLore, AttributeModifier[] newAttributes, 
			Enchantment[] newEnchantments, boolean allowEnchanting, 
			boolean allowAnvil, Ingredient repairItem, long newDurability, 
			NamedImage newImage, boolean[] itemFlags, 
			int entityHitDurabilityLoss, int blockBreakDurabilityLoss, 
			double thresholdDamage, byte[] newCustomModel, 
			byte[] newCustomBlockingModel, List<PotionEffect> playerEffects, 
			List<PotionEffect> targetEffects, 
			Collection<EquippedPotionEffect> newEquippedEffects, String[] commands, 
			ReplaceCondition[] conditions, ConditionOperation op, 
			ExtraItemNbt newExtraNbt, float newAttackRange, boolean checkClass
	) {
		if (!bypassChecks()) {
			if (shield == null)
				return "Can't change null items";
			if (checkClass && shield.getClass() != CustomShield.class)
				return "Use the appropriate method for that class!";
			double th = thresholdDamage;
			if (th < 0)
				return "The threshold damage can't be negative";
			if (th != th)
				return "The threshold damage can't be NaN";
		}
		String error = changeTool(
				shield, CustomItemType.SHIELD, newAlias, newDisplayName, newLore, 
				newAttributes, newEnchantments, allowEnchanting, allowAnvil, 
				repairItem, newDurability, newImage, itemFlags,
				entityHitDurabilityLoss, blockBreakDurabilityLoss, newCustomModel, 
				playerEffects, targetEffects, newEquippedEffects,
				commands, conditions, op, newExtraNbt, newAttackRange, false
		);
		if (error != null) {
			return error;
		} else {
			shield.setThresholdDamage(thresholdDamage);
			shield.setBlockingModel(newCustomBlockingModel);
			return null;
		}
	}

	/**
	 * Attempts to add the specified tool to this item set. If the tool can be
	 * added, it will be added. If the tool can't be added, the reason is returned.
	 * 
	 * @param item The tool that should be added to this item set
	 * @return The reason the tool could not be added, or null if the tool was added
	 *         succesfully
	 */
	public String addTool(CustomTool item, boolean checkClass) {
		if (!bypassChecks()) {
			if (item == null)
				return "Can't add null items";
			if (checkClass && item.getClass() != CustomTool.class)
				return "Use the appropriate method for that class";
			if (item.getRepairItem() instanceof CustomItemIngredient
					&& !(((CustomItemIngredient) item.getRepairItem()).getItem().getClass() == SimpleCustomItem.class))
				return "Only vanilla items and simple custom items are allowed as repair item.";
			if (item.allowAnvilActions() && item.getDisplayName().contains("§"))
				return "Items with color codes in their display name can not allow anvil actions";
			if (item.allowEnchanting() && item.getDefaultEnchantments().length > 0)
				return "You can't allow enchanting on items that have default enchantments";
		}
		return addItem(item);
	}
	
	public String addShears(CustomShears shears, boolean checkClass) {
		if (!bypassChecks()) {
			if (shears == null)
				return "Can't add null items";
			if (checkClass && shears.getClass() != CustomShears.class)
				return "Use the appropriate method for that class";
			if (shears.getShearDurabilityLoss() < 0)
				return "The shear durability loss must be a positive integer";
		}
		return addTool(shears, false);
	}
	
	public String addHoe(CustomHoe hoe, boolean checkClass) {
		if (!bypassChecks()) {
			if (hoe == null)
				return "Can't add null items";
			if (checkClass && hoe.getClass() != CustomHoe.class)
				return "Use the appropriate method for that class";
			if (hoe.getTillDurabilityLoss() < 0)
				return "The till durability loss must be a positive integer";
		}
		return addTool(hoe, false);
	}
	
	public String addTrident(CustomTrident trident, boolean checkClass) {
		if (!bypassChecks()) {
			if (trident == null)
				return "Can't add null items";
			if (checkClass && trident.getClass() != CustomTrident.class)
				return "Use the appropriate method for that class";
			if (trident.throwDurabilityLoss < 0)
				return "The throw durability loss must be a positive integer";
			if (trident.throwDamageMultiplier < 0)
				return "The throw damage multiplier must be a positive number";
			if (trident.speedMultiplier < 0)
				return "The speed multiplier must be a positive number";
		}
		return addTool(trident, false);
	}

	/**
	 * Attempts to change the specified tool in this item set. If the tool can be
	 * changed, it will be changed. If the tool can't be changed, the reason is
	 * returned.
	 * 
	 * @return The reason the tool could not be changed, or null if the tool was
	 *         changed successfully
	 */
	public String changeTool(
			CustomTool item, CustomItemType newType, String newAlias,
			String newDisplayName, String[] newLore, 
			AttributeModifier[] newAttributes, Enchantment[] newEnchantments, 
			boolean allowEnchanting, boolean allowAnvil, Ingredient repairItem, 
			long newDurability, NamedImage newImage, boolean[] itemFlags, 
			int entityHitDurabilityLoss, int blockBreakDurabilityLoss,
			byte[] newCustomModel, List<PotionEffect> playerEffects, 
			List<PotionEffect> targetEffects, 
			Collection<EquippedPotionEffect> newEquippedEffects, String[] commands, 
			ReplaceCondition[] conditions, ConditionOperation op, 
			ExtraItemNbt newExtraNbt, float newAttackRange, boolean checkClass
	) {
		if (!bypassChecks()) {
			if (checkClass && item.getClass() != CustomTool.class)
				return "Use the appropriate method to change this class";
			if (allowAnvil && newDisplayName.contains("§"))
				return "Items with color codes in their display name can not allow anvil actions";
			if (repairItem instanceof CustomItemIngredient
					&& !(((CustomItemIngredient) repairItem).getItem().getClass() == SimpleCustomItem.class))
				return "Only vanilla items and simple custom items are allowed as repair item.";
			if (allowEnchanting && newEnchantments.length > 0)
				return "You can't allow enchanting on items that have default enchantments";
			if (entityHitDurabilityLoss < 0)
				return "The entity hit durability loss can't be negative";
			if (blockBreakDurabilityLoss < 0)
				return "The block break durability loss can't be negative";
		}
		String error = changeItem(
				item, newType, newAlias, newDisplayName, newLore, newAttributes, 
				newEnchantments, newImage, itemFlags, newCustomModel,
				playerEffects, targetEffects, newEquippedEffects, 
				commands, conditions, op, newExtraNbt, newAttackRange
		);
		if (error == null) {
			item.setAllowEnchanting(allowEnchanting);
			item.setAllowAnvilActions(allowAnvil);
			item.setRepairItem(repairItem);
			item.setDurability(newDurability);
			item.setEntityHitDurabilityLoss(entityHitDurabilityLoss);
			item.setBlockBreakDurabilityLoss(blockBreakDurabilityLoss);
			return null;
		} else {
			return error;
		}
	}
	
	public String changeShears(
			CustomShears shears, String newAlias, String newDisplayName, 
			String[] newLore, AttributeModifier[] newAttributes, 
			Enchantment[] newEnchantments, boolean allowEnchanting,
			boolean allowAnvil, Ingredient repairItem, long newDurability, 
			NamedImage newImage, boolean[] itemFlags, int entityHitDurabilityLoss, 
			int blockBreakDurabilityLoss, int shearDurabilityLoss,
			byte[] newCustomModel, List<PotionEffect> playerEffects, 
			List<PotionEffect> targetEffects, 
			Collection<EquippedPotionEffect> newEquippedEffects, String[] commands, 
			ReplaceCondition[] conditions, ConditionOperation op, 
			ExtraItemNbt newExtraNbt, float newAttackRange, boolean checkClass
	) {
		if (!bypassChecks()) {
			if (shearDurabilityLoss < 0) {
				return "The shear durability loss must be positive";
			}
			if (checkClass && shears.getClass() != CustomShears.class) {
				return "Use the appropriate method to change this class";
			}
		}
		String error = changeTool(
				shears, CustomItemType.SHEARS, newAlias, newDisplayName, newLore, 
				newAttributes, newEnchantments, allowEnchanting, allowAnvil, 
				repairItem, newDurability, newImage, itemFlags,
				entityHitDurabilityLoss, blockBreakDurabilityLoss, newCustomModel, 
				playerEffects, targetEffects, newEquippedEffects, 
				commands, conditions, op, newExtraNbt, newAttackRange, false
		);
		if (error == null) {
			shears.setShearDurabilityLoss(shearDurabilityLoss);
			return null;
		} else {
			return error;
		}
	}
	
	public String changeHoe(
			CustomHoe hoe, CustomItemType newType, String newAlias,
			String newDisplayName, String[] newLore, 
			AttributeModifier[] newAttributes, Enchantment[] newEnchantments, 
			boolean allowEnchanting, boolean allowAnvil, Ingredient repairItem, 
			long newDurability, NamedImage newImage, boolean[] itemFlags, 
			int entityHitDurabilityLoss, int blockBreakDurabilityLoss, 
			int tillDurabilityLoss, byte[] newCustomModel, 
			List<PotionEffect> playerEffects, List<PotionEffect> targetEffects, 
			Collection<EquippedPotionEffect> newEquippedEffects, String[] commands, 
			ReplaceCondition[] conditions, ConditionOperation op, 
			ExtraItemNbt newExtraNbt, float newAttackRange, boolean checkClass
	) {
		if (!bypassChecks()) {
			if (tillDurabilityLoss < 0) {
				return "The till durability loss must be positive";
			}
			if (checkClass && hoe.getClass() != CustomHoe.class) {
				return "Use the appropriate method to change this class";
			}
		}
		String error = changeTool(
				hoe, newType, newAlias, newDisplayName, newLore, newAttributes,
				newEnchantments, allowEnchanting, allowAnvil, repairItem, 
				newDurability, newImage, itemFlags, entityHitDurabilityLoss, 
				blockBreakDurabilityLoss, newCustomModel, playerEffects, 
				targetEffects, newEquippedEffects, commands, conditions, op, 
				newExtraNbt, newAttackRange, false
		);
		if (error == null) {
			hoe.setTillDurabilityLoss(tillDurabilityLoss);
			return null;
		} else {
			return error;
		}
	}
	
	public String changeTrident(
			CustomTrident trident, CustomItemType newType, String newAlias,
			String newDisplayName, String[] newLore, 
			AttributeModifier[] newAttributes, Enchantment[] newEnchantments, 
			boolean allowEnchanting, boolean allowAnvil, 
			double throwDamageMultiplier, double throwSpeedMultiplier, 
			Ingredient repairItem, long newDurability, NamedImage newImage, 
			boolean[] itemFlags, int entityHitDurabilityLoss, 
			int blockBreakDurabilityLoss, int throwDurabilityLoss, 
			byte[] newCustomModel, byte[] newCustomInHandModel, 
			byte[] newCustomThrowingModel, List<PotionEffect> playerEffects, 
			List<PotionEffect> targetEffects, 
			Collection<EquippedPotionEffect> newEquippedEffects, String[] commands, 
			ReplaceCondition[] conditions, ConditionOperation op, 
			ExtraItemNbt newExtraNbt, float newAttackRange, boolean checkClass
	) {
		if (!bypassChecks()) {
			if (trident == null)
				return "Can't change null items";
			if (checkClass && trident.getClass() != CustomTrident.class)
				return "Use the appropriate method for that class";
			if (throwDurabilityLoss < 0)
				return "The throw durability loss must be a positive integer";
			if (throwDamageMultiplier < 0)
				return "The throw damage multiplier must be a positive number";
			if (throwSpeedMultiplier < 0)
				return "The speed multiplier must be a positive number";
		}
		
		String error = changeTool(
				trident, newType, newAlias, newDisplayName, newLore, newAttributes,
				newEnchantments, allowEnchanting, allowAnvil, repairItem, newDurability, newImage, itemFlags,
				entityHitDurabilityLoss, blockBreakDurabilityLoss, newCustomModel, 
				playerEffects, targetEffects, newEquippedEffects,
				commands, conditions, op, newExtraNbt, newAttackRange, false
		);
		if (error == null) {
			trident.throwDurabilityLoss = throwDurabilityLoss;
			trident.throwDamageMultiplier = throwDamageMultiplier;
			trident.speedMultiplier = throwSpeedMultiplier;
			trident.customInHandModel = newCustomInHandModel;
			trident.customThrowingModel = newCustomThrowingModel;
			return null;
		} else {
			return error;
		}
	}
	
	/**
	 * Attempts to add the given wand to this item set. If the wand was added successfully, this method will
	 * return null. If not, the reason why the wand wasn't added will be returned.
	 * @param wand The wand that should be added to this item set
	 * @return null if the wand was added successfully, or the reason it wasn't
	 */
	public String addWand(CustomWand wand) {
		if (!bypassChecks()) {
			if (wand == null)
				return "Can't add null wands";
			if (wand.projectile == null)
				return "You need to select a projectile";
			if (!projectiles.contains(wand.projectile))
				return "The selected projectile is not in the list of projectiles";
			if (wand.cooldown < 1)
				return "The cooldown must be a positive integer";
			String chargesError = validateCharges(wand.charges);
			if (chargesError != null)
				return chargesError;
			if (wand.amountPerShot <= 0)
				return "The amount per shot must be a positive integer";
		}
		return addItem(wand);
	}

	public String addPocketContainer(CustomPocketContainer toAdd) {
		if (!bypassChecks()) {
			if (toAdd == null)
				return "Can't add null pocket containers";
			if (toAdd.getContainers() == null)
				return "The container collection can't be null";
			if (toAdd.getContainers().isEmpty())
				return "You need to select at least 1 container";
			if (!containers.containsAll(toAdd.getContainers()))
				return "Not all selected containers are in the list of containers";
		}
		return addItem(toAdd);
	}
	
	/**
	 * Attempts to change the properties of the given wand to the given values.
	 * @return null if the wand was changed successfully, or the reason it wasn't
	 */
	public String changeWand(
			CustomWand original, CustomItemType newType, String newAlias,
			String newDisplayName, String[] newLore, 
			AttributeModifier[] newAttributes, Enchantment[] newEnchantments, 
			NamedImage newImage, boolean[] itemFlags, byte[] newCustomModel, 
			List<PotionEffect> playerEffects, List<PotionEffect> targetEffects, 
			Collection<EquippedPotionEffect> newEquippedEffects, String[] commands, 
			ReplaceCondition[] conditions, ConditionOperation op, 
			CIProjectile newProjectile, int newCooldown, WandCharges newCharges, 
			int newAmountPerShot, ExtraItemNbt newExtraNbt, float newAttackRange
	) {
		if (!bypassChecks()) {
			if (original == null)
				return "Can't change null items";
			if (newProjectile == null)
				return "You must select a projectile";
			if (!projectiles.contains(newProjectile))
				return "The selected projectile is not in the list of projectiles";
			if (newCooldown < 1)
				return "The cooldown must be a positive integer";
			String chargesError = validateCharges(newCharges);
			if (chargesError != null)
				return chargesError;
			if (newAmountPerShot <= 0)
				return "The amount per shot must be a positive integer";
		}
		String error = changeItem(
				original, newType, newAlias, newDisplayName, newLore, newAttributes, 
				newEnchantments, newImage, itemFlags, newCustomModel, playerEffects,
				targetEffects, newEquippedEffects, commands, conditions, op,
				newExtraNbt, newAttackRange
		);
		if (error == null) {
			original.projectile = newProjectile;
			original.cooldown = newCooldown;
			original.charges = newCharges;
			original.amountPerShot = newAmountPerShot;
		}
		return error;
	}

	public String changePocketContainer(
		CustomPocketContainer original, CustomItemType newType, String newAlias,
		String newDisplayName, String[] newLore,
		AttributeModifier[] newAttributes, Enchantment[] newEnchantments,
		NamedImage newImage, boolean[] itemFlags, byte[] newCustomModel,
		List<PotionEffect> playerEffects, List<PotionEffect> targetEffects,
		Collection<EquippedPotionEffect> newEquippedEffects, String[] commands,
		ReplaceCondition[] conditions, ConditionOperation op,
		ExtraItemNbt newExtraNbt, float newAttackRange, Collection<CustomContainer> newContainers
	) {
		if (!bypassChecks()) {
			if (original == null)
				return "Can't change null items";
			if (newContainers == null)
				return "The collection of containers can't be null";
			if (newContainers.isEmpty())
				return "You need to select at least 1 container";
			if (!containers.containsAll(newContainers))
				return "The selected container is not in the list of containers";
		}
		String error = changeItem(
				original, newType, newAlias, newDisplayName, newLore, newAttributes, newEnchantments,
				newImage, itemFlags, newCustomModel, playerEffects, targetEffects, newEquippedEffects,
				commands, conditions, op, newExtraNbt, newAttackRange
		);
		if (error == null) {
			original.setContainers(newContainers);
		}
		return error;
	}
	
	private String validateProjectile(CIProjectile projectile) {
		if (projectile == null)
			return "It must have a projectile";
		
		// This is not equivalent to damage < 0 because this also deals with NaN
		if (!(projectile.damage >= 0))
			return "The damage of the projectile must be at least 0.0";
		
		if (!(projectile.minLaunchAngle >= 0))
			return "The minimum launch angle can't be negative";
		if (!(projectile.maxLaunchAngle >= 0))
			return "The maximum launch angle can't be negative";
		if (projectile.minLaunchAngle > projectile.maxLaunchAngle)
			return "The minimum launch angle can't be greater than the maximum launch angle";
		
		if (!(projectile.minLaunchSpeed >= 0))
			return "The minimum launch speed can't be negative";
		if (!(projectile.maxLaunchSpeed >= 0))
			return "The maximum launch speed can't be negative";
		if (projectile.minLaunchSpeed > projectile.maxLaunchSpeed)
			return "The minimum launch speed can't be greater than the maximum launch speed";
		
		if (projectile.cover != null && !projectileCovers.contains(projectile.cover))
			return "The projectile cover is not in the list of projectile covers";
		
		if (projectile.inFlightEffects.size() > Byte.MAX_VALUE)
			return "Currently, (only) " + Byte.MAX_VALUE + " flight effects are allowed";
		for (ProjectileEffects effects : projectile.inFlightEffects) {
			String effectsError = effects.validate();
			if (effectsError != null)
				return effectsError;
			effectsError = validateProjectileEffects(effects.effects);
			if (effectsError != null)
				return effectsError;
		}
		return validateProjectileEffects(projectile.impactEffects);
	}
	
	private String validateProjectileEffects(Collection<ProjectileEffect> effects) {
		if (effects.size() > Byte.MAX_VALUE)
			return "Currently, only " + Byte.MAX_VALUE + " effects are allowed per wave";
		for (ProjectileEffect effect : effects) {
			String error = effect.validate();
			if (error != null)
				return error;
		}
		return null;
	}
	
	private String validateCharges(WandCharges charges) {
		
		// Note that it is allowed that the charges are null
		if (charges != null) {
			if (charges.maxCharges < 2)
				return "If the wand uses charges, the number of charges must be at least 2";
			if (charges.rechargeTime <= 0)
				return "If the wand uses charges, the recharge time must be a positive integer";
		}
		
		return null;
	}

	private String validateSlotDisplay(SlotDisplay display, String slotType, String displayType, boolean allowNull) {
		if (display == null) {
		    if (allowNull) {
		    	return null;
			} else {
				return "There is a " + slotType + " slot without " + displayType;
			}
		}
		if (display.getAmount() < 1) {
			return "There is a " + slotType + " " + displayType + " slot with an amount smaller than 1";
		}
		if (display.getAmount() > 64) {
			return "There is a " + slotType + " " + displayType + " slot with an amount greater than 64";
		}
		if (display.getDisplayName() == null) {
			return "There is a " + slotType + " " + displayType + " with a null display name";
		}
		if (display.getLore() == null) {
			return "There is a " + slotType + " " + displayType + " with a null lore";
		}
		for (String line : display.getLore()) {
			if (line == null) {
				return "There is a " + slotType + " " + displayType + " with a null line in its lore";
			}
		}

		return null;
	}
	
	private String validateSlot(CustomSlot slot, 
			Iterable<CustomSlot> allSlots) {
		
		if (slot == null) {
			return "A slot is null";
		}
		if (slot instanceof DecorationCustomSlot) {
			DecorationCustomSlot decorationSlot = (DecorationCustomSlot) slot;
			SlotDisplay display = decorationSlot.getDisplay();
			String displayError = validateSlotDisplay(
					display,
					"decoration",
					"display",
					false
			);
			if (displayError != null) {
				return displayError;
			}
		} else if (slot instanceof FuelCustomSlot) {
			FuelCustomSlot fuelSlot = (FuelCustomSlot) slot;

			String placeholderError = validateSlotDisplay(
					fuelSlot.getPlaceholder(),
					"fuel",
					"placeholder",
					true
			);
			if (placeholderError != null) {
				return placeholderError;
			}

			for (CustomSlot otherSlot : allSlots) {
				if (otherSlot != fuelSlot && otherSlot instanceof FuelCustomSlot) {
					FuelCustomSlot otherFuelSlot = (FuelCustomSlot) otherSlot;
					if (otherFuelSlot.getName().equals(fuelSlot.getName())) {
						return "There are multiple fuel slots with name " + fuelSlot.getName();
					}
				}
			}
		} else if (slot instanceof FuelIndicatorCustomSlot) {
			FuelIndicatorCustomSlot indicator = (FuelIndicatorCustomSlot) slot;
			String displayError = validateSlotDisplay(
					indicator.getDisplay(),
					"fuel indicator",
					"display",
					false
			);
			if (displayError != null) {
				return displayError;
			}

			String placeholderError = validateSlotDisplay(
					indicator.getPlaceholder(),
					"fuel indicator",
					"place holder",
					false
			);
			if (placeholderError != null) {
				return placeholderError;
			}

			if (indicator.getDomain().getBegin() < 0) {
				return "The indicator " + indicator.getFuelSlotName() + " starts before 0%";
			} else if (indicator.getDomain().getEnd() > 100) {
				return "The indicator " + indicator.getFuelSlotName() + " ends after 100%";
			}
			boolean foundFuelSlot = false;
			outerLoop:
			for (CustomSlot otherSlot : allSlots) {
				if (otherSlot instanceof FuelCustomSlot) {
					FuelCustomSlot fuelSlot = (FuelCustomSlot) otherSlot;
					if (fuelSlot.getName().equals(indicator.getFuelSlotName())) {
						foundFuelSlot = true;
						break outerLoop;
					}
				}
			}
			if (!foundFuelSlot) {
				return "There is a fuel indicator slot with name " + indicator.getFuelSlotName() + ", but no fuel slot with that name";
			}
		} else if (slot instanceof InputCustomSlot) {
			InputCustomSlot inputSlot = (InputCustomSlot) slot;
			String placeholderError = validateSlotDisplay(
					inputSlot.getPlaceholder(),
					"input",
					"place holder",
					true
			);
			if (placeholderError != null) {
				return placeholderError;
			}

			for (CustomSlot otherSlot : allSlots) {
				if (otherSlot != slot && otherSlot instanceof InputCustomSlot) {
					InputCustomSlot otherInputSlot = (InputCustomSlot) otherSlot;
					if (otherInputSlot.getName().equals(inputSlot.getName())) {
						return "There are multiple input slots with name " + inputSlot.getName();
					}
				}
			}
		} else if (slot instanceof OutputCustomSlot) {
			OutputCustomSlot outputSlot = (OutputCustomSlot) slot;
			String placeHolderError = validateSlotDisplay(
					outputSlot.getPlaceholder(),
					"output",
					"place holder",
					true
			);
			if (placeHolderError != null) {
				return placeHolderError;
			}

			for (CustomSlot otherSlot : allSlots) {
				if (otherSlot != slot && otherSlot instanceof OutputCustomSlot) {
					OutputCustomSlot otherOutputSlot = (OutputCustomSlot) otherSlot;
					if (outputSlot.getName().equals(otherOutputSlot.getName())) {
						return "There are multiple output slots with name " + outputSlot.getName();
					}
				}
			}
		} else if (slot instanceof ProgressIndicatorCustomSlot) {
			ProgressIndicatorCustomSlot indicator = (ProgressIndicatorCustomSlot) slot;

			String displayError = validateSlotDisplay(
					indicator.getDisplay(),
					"progress indicator",
					"display",
					false
			);
			if (displayError != null) {
				return displayError;
			}

			String placeHolderError = validateSlotDisplay(
					indicator.getPlaceHolder(),
					"progress indicator",
					"place holder",
					false
			);
			if (placeHolderError != null) {
				return placeHolderError;
			}

			if (indicator.getDomain().getBegin() < 0) {
				return "There is a crafting progress indicator that starts before 0%";
			} else if (indicator.getDomain().getEnd() > 100) {
				return "There is a crafting progress indicator that ends after 100%";
			}
		} else if (slot instanceof StorageCustomSlot) {
			StorageCustomSlot storageSlot = (StorageCustomSlot) slot;
			String placeHolderError = validateSlotDisplay(
			        storageSlot.getPlaceHolder(),
					"storage",
					"place holder",
					true
			);
			if (placeHolderError != null) {
				return placeHolderError;
			}

		} else if (!(slot instanceof EmptyCustomSlot)){
			return "Unknown custom slot class: " + slot.getClass();
		}
		
		return null;
	}
	
	private String validateContainerRecipe(
			ContainerRecipe recipe, Iterable<CustomSlot> slots
	) {
		inputLoop:
		for (InputEntry entry : recipe.getInputs()) {
			for (CustomSlot slot : slots) {
				if (slot instanceof InputCustomSlot) {
					InputCustomSlot inputSlot = (InputCustomSlot) slot;
					if (inputSlot.getName().equals(entry.getInputSlotName())) {
						continue inputLoop;
					}
				}
			}
			return "One of the recipes needs an input slot with name " 
					+ entry.getInputSlotName() + ", but no such slot was found";
		}
	
		outputLoop:
		for (OutputEntry entry : recipe.getOutputs()) {
			for (CustomSlot slot : slots) {
				if (slot instanceof OutputCustomSlot) {
					OutputCustomSlot outputSlot = (OutputCustomSlot) slot;
					if (outputSlot.getName().equals(entry.getOutputSlotName())) {
						continue outputLoop;
					}
				}
			}
			return "One of the recipes needs an output slot with name " + entry.getOutputSlotName() + ", but no such slot was found";
		}
		
		if (recipe.getDuration() < 0) {
			return "One of the recipes has a negative duration";
		}
		
		return null;
	}

	private String addItem(CustomItem item) {
		if (!bypassChecks()) {
			if (item == null)
				return "Can't add null items";
			String nameError = checkName(item.getName());
			if (nameError != null)
				return nameError;
			if (item.getTexture() == null)
				return "Every item needs a texture";
			if (item.getAttributes() == null)
				return "Attributes are null";
			if (item.getAttributes().length > Byte.MAX_VALUE)
				return "Too many attribute modifiers";
			for (AttributeModifier att : item.getAttributes()) {
				if (att.getAttribute() == null) {
					return "An attribute modifier has no attribute";
				}
				if (att.getOperation() == null) {
					return "An attribute modifier has no operation";
				}
				if (att.getSlot() == null) {
					return "An attribute modifier has no slot";
				}
			}
			if (item.getDefaultEnchantments() == null)
				return "Default enchantments are null";
			if (item.getDefaultEnchantments().length > Byte.MAX_VALUE)
				return "Too many default enchantments";
			if (item.getLore() == null)
				return "The lore is null";
			if (item.getLore().length > Byte.MAX_VALUE)
				return "Too much lore";
			for (Enchantment enchantment : item.getDefaultEnchantments())
				if (enchantment.getType() == null)
					return "An enchantment has no type";
			for (PotionEffect effect : item.getPlayerEffects())
				if (effect.getEffect() == null)
					return "A player on-hit effect has no status effect";
			for (PotionEffect effect : item.getTargetEffects())
				if (effect.getEffect() == null)
					return "A target on-hit effect has no status effect";
			if (item.getCommands() == null)
				return "The commands can't be null";
			if (item.getCommands().length > Byte.MAX_VALUE)
				return "Too many commands";
			if (item.getTargetEffects() == null)
				return "The target effects can't be null";
			if (item.getTargetEffects().size() > Byte.MAX_VALUE)
				return "Too many target effects";
			if (item.getPlayerEffects() == null)
				return "The player effects can't be null";
			if (item.getPlayerEffects().size() > Byte.MAX_VALUE)
				return "Too many player effects";
			for (CustomItem current : items)
				if (current.getName().equals(item.getName()))
					return "There is already a custom item with that name";
			for (String deletedItem : deletedItems) {
				if (item.getName().equals(deletedItem)) {
					return "There is a deleted custom item with that name";
				}
			}
			if(item.getReplaceConditions() == null)
				return "Replace conditions can't be null";
			if (item.getReplaceConditions().length > 1 && item.getConditionOperator() == ConditionOperation.NONE)
				return "There are multiple replace conditions but no operator has been specified";
			if (item.getAttackRange() < 0)
				return "The attack range can't be negative";
			if (item.getAttackRange() > 50)
				return "The attack range shouldn't be larger than 50";
			if (Float.isNaN(item.getAttackRange()))
				return "The attack range shouldn't be NaN";
		}
		items.add(item);
		return null;
	}

	/**
	 * Attempts to add the specified simple item to this item set. If the item can be
	 * added, it will be added. If the item can't be added, the reason is returned.
	 * 
	 * @param item The simple item that should be added to this set
	 * @return The reason the item could not be added, or null if the item was added
	 *         successfully
	 */
	public String addSimpleItem(SimpleCustomItem item) {
		if (!bypassChecks()) {
			if (item == null)
				return "item is null";
			if (item.getMaxStacksize() < 1 || item.getMaxStacksize() > 64)
				return "The maximum stacksize (" + item.getMaxStacksize() + ") is out of range";
		}
		return addItem(item);
	}
	
	public String changeSimpleItem(
			SimpleCustomItem item, CustomItemType newType, String newAlias,
			String newDisplayName, String[] newLore, 
			AttributeModifier[] newAttributes, Enchantment[] newEnchantments, 
			NamedImage newImage, int newStacksize, boolean[] newItemFlags, 
			byte[] newCustomModel, List<PotionEffect> playerEffects, 
			List<PotionEffect> targetEffects, 
			Collection<EquippedPotionEffect> newEquippedEffects, String[] commands, 
			ReplaceCondition[] conditions, ConditionOperation op, 
			ExtraItemNbt newExtraNbt, float newAttackRange, boolean checkClass
	) {
		if (!bypassChecks()) {
			if (checkClass && item.getClass() != SimpleCustomItem.class)
				return "Use the right method for the class";
			if (newStacksize < 1 || newStacksize > 64)
				return "The maximum stacksize (" + newStacksize + ") is out of range";
			if (newStacksize < item.getMaxStacksize())
				return "You can't decrease the stacksize";
		}

		String error = changeItem(
				item, newType, newAlias, newDisplayName, newLore, newAttributes,
				newEnchantments, newImage, newItemFlags, newCustomModel, 
				playerEffects, targetEffects, newEquippedEffects, commands, 
				conditions, op, newExtraNbt, newAttackRange
		);
		
		if (error != null) {
			return error;
		}
		
		item.setMaxStacksize(newStacksize);
		return null;
	}

	/**
	 * Attempts to change the specified item in this item set. If the item can be
	 * changed, it will be changed. If the item can't be changed, the reason is
	 * returned.
	 * 
	 * @return null if the item was changed successfully or the reason the item
	 *         could not be changed
	 */
	private String changeItem(
			CustomItem item, CustomItemType newType, String newAlias,
			String newDisplayName, String[] newLore, 
			AttributeModifier[] newAttributes, Enchantment[] newEnchantments, 
			NamedImage newImage, boolean[] itemFlags, byte[] newCustomModel, 
			List<PotionEffect> newPlayerEffects, List<PotionEffect> newTargetEffects, 
			Collection<EquippedPotionEffect> newEquippedEffects, String[] newCommands, 
			ReplaceCondition[] conditions, ConditionOperation op, 
			ExtraItemNbt newExtraNbt, float newAttackRange
	) {
		if (!bypassChecks()) {
			if (item == null)
				return "Can't change null items";
			if (newImage == null)
				return "Every item needs a texture";
			if (newAlias == null)
				return "The alias can be empty, but not null";
			if (newAttributes == null)
				return "Attributes are null";
			if (itemFlags == null)
				return "The item flags are null";
			if (itemFlags.length != ItemFlag.values().length)
				return "The length of the item flags is incorrect";
			if (newAttributes.length > Byte.MAX_VALUE)
				return "Too many attribute modifiers";
			for (AttributeModifier att : newAttributes) {
				if (att.getAttribute() == null) {
					return "An attribute modifier has no attribute";
				}
				if (att.getOperation() == null) {
					return "An attribute modifier has no operation";
				}
				if (att.getSlot() == null) {
					return "An attribute modifier has no slot";
				}
			}
			if (newEnchantments == null)
				return "Default enchantments are null";
			if (newEnchantments.length > Byte.MAX_VALUE)
				return "Too many default enchantments";
			if (newCommands == null)
				return "The commands can't be null";
			if (newCommands.length > Byte.MAX_VALUE)
				return "Too many commands";
			if (newTargetEffects == null)
				return "The target effects can't be null";
			if (newTargetEffects.size() > Byte.MAX_VALUE)
				return "Too many target effects";
			if (newPlayerEffects == null)
				return "The player effects can't be null";
			if (newPlayerEffects.size() > Byte.MAX_VALUE)
				return "Too many player effects";
			if (newEquippedEffects == null)
				return "The held/equipped effects can be empty, but not null";
			if (newLore == null)
				return "The lore is null";
			if (newLore.length > Byte.MAX_VALUE)
				return "Too much lore";
			for (Enchantment enchantment : newEnchantments)
				if (enchantment.getType() == null)
					return "An enchantment has no type";
			
			for (PotionEffect effect : newPlayerEffects)
				if (effect.getEffect() == null)
					return "A player on-hit effect has no status effect";
			for (PotionEffect effect : newTargetEffects)
				if (effect.getEffect() == null)
					return "A target on-hit effect has no status effect";
			if (!items.contains(item))
				return "There is no previous item!";
			if (!textures.contains(newImage))
				return "The specified texture is not in the texture list!";
			if(conditions == null)
				return "The replace conditions cannot be null";
			if(conditions.length > 1 && op == ConditionOperation.NONE)
				return "There are multiple replace conditions but no operator has been specified";
			if (newExtraNbt == null)
				return "The extra nbt can't be null (but can be empty)";
			if (newAttackRange < 0)
				return "The attack range can't be negative";
			if (newAttackRange > 50)
				return "The attack range shouldn't be larger than 50";
			if (Float.isNaN(newAttackRange))
				return "The attack range shouldn't be NaN";
		}
		item.setItemType(newType);
		item.setAlias(newAlias);
		item.setDisplayName(newDisplayName);
		item.setLore(newLore);
		item.setAttributes(newAttributes);
		item.setDefaultEnchantments(newEnchantments);
		item.setTexture(newImage);
		item.setItemFlags(itemFlags);
		item.setCustomModel(newCustomModel);
		item.setPlayerEffects(newPlayerEffects);
		item.setTargetEffects(newTargetEffects);
		item.setEquippedEffects(newEquippedEffects);
		item.setCommands(newCommands);
		item.setConditions(conditions);
		item.setConditionOperator(op);
		item.setExtraNbt(newExtraNbt);
		item.setAttackRange(newAttackRange);
		return null;
	}

	private CustomItem responsibleItem(SlotDisplay slotDisplay) {
		if (slotDisplay != null && slotDisplay.getItem() instanceof CustomItemDisplayItem) {
			return (CustomItem) ((CustomItemDisplayItem) slotDisplay.getItem()).getItem();
		} else {
			return null;
		}
	}

	public static boolean hasRemainingCustomItem(SCIngredient ingredient, CustomItem toCheck) {
		if (ingredient == null || ingredient instanceof NoIngredient) {
			return false;
		}

		Result result = ((Ingredient) ingredient).getRemainingItem();
		if (result instanceof CustomItemResult) {
			return ((CustomItemResult) result).getItem() == toCheck;
		} else {
			return false;
		}
	}

	/**
	 * Attempts to remove the specified item from this set. If the item could not be
	 * removed, the reason is returned. If the item can be removed, it will be
	 * removed and null will be returned.
	 * 
	 * @param item The item that should be removed from this ItemSet
	 * @return The reason the item could not be removed, or null if the item was
	 *         removed successfully.
	 */
	public String removeItem(CustomItem item) {
		if (!bypassChecks()) {
			for (Recipe recipe : recipes) {
				if (recipe.getResult() instanceof CustomItemResult
						&& ((CustomItemResult) recipe.getResult()).getItem() == item)
					return "At least one of your recipes has this item as result.";
				if (recipe.requires(item))
					return "At least one of your recipes has this item as an ingredient or remaining ingredient.";
			}
			for (CustomItem current : items) {
				if (current instanceof CustomTool) {
					CustomTool tool = (CustomTool) current;
					if (tool.getRepairItem() instanceof CustomItemIngredient) {
						CustomItemIngredient ingredient = (CustomItemIngredient) tool.getRepairItem();
						if (ingredient.getItem() == item) {
							return "The tool " + tool.getName() + " has this item as repair item.";
						}
					}
					if (hasRemainingCustomItem(tool.getRepairItem(), item)) {
						return "The tool " + tool.getName() + " has this item as remaining repair item";
					}
				}
			}
			for (EntityDrop drop : mobDrops) {
				for (OutputTable.Entry entry : drop.getDrop().getDropTable().getEntries()) {
					if (entry.getResult() instanceof CustomItemResult) {
						CustomItemResult result = (CustomItemResult) entry.getResult();
						if (result.getItem() == item) {
							return "There is a mob drop for " + drop.getEntityType() + " that can drop this item.";
						}
					}
				}
			}
			for (BlockDrop drop : blockDrops) {
				for (OutputTable.Entry entry : drop.getDrop().getDropTable().getEntries()) {
					if (entry.getResult() instanceof CustomItemResult) {
						CustomItemResult result = (CustomItemResult) entry.getResult();
						if (result.getItem() == item) {
							return "There is a block drop for " + drop.getBlock() + " that can drop this item.";
						}
					}
				}
			}
			for (CustomFuelRegistry registry : fuelRegistries) {
				for (FuelEntry entry : registry.getEntries()) {
					if (entry.getFuel() instanceof CustomItemIngredient) {
						
						CustomItemIngredient ingredient = (CustomItemIngredient) entry.getFuel();
						if (ingredient.getItem() == item) {
							return "The fuel registry " + registry.getName() + " uses this item as fuel";
						}
					}
					if (hasRemainingCustomItem(entry.getFuel(), item)) {
						return "The fuel registry " + registry.getName() + " uses this item as remaining fuel";
					}
				}
			}
			for (CustomContainer container : containers) {
			    // Protect the selection icon of custom containers
				if (responsibleItem(container.getSelectionIcon()) == item) {
					return "This item is used as selection icon of container " + container.getName();
				}

				// Protect the slot displays of custom containers
				for (int x = 0; x < 9; x++) {
					for (int y = 0; y < container.getHeight(); y++) {
						CustomSlot slot = container.getSlot(x, y);
						if (slot instanceof DecorationCustomSlot) {
							DecorationCustomSlot decoration = (DecorationCustomSlot) slot;
							if (responsibleItem(decoration.getDisplay()) == item) {
								return "This item is used as decoration in container " + container.getName();
							}
						} else if (slot instanceof FuelCustomSlot) {
							FuelCustomSlot fuelSlot = (FuelCustomSlot) slot;
							if (responsibleItem(fuelSlot.getPlaceholder()) == item) {
								return "This item is used as fuel placeholder in container " + container.getName();
							}
						} else if (slot instanceof FuelIndicatorCustomSlot) {
							FuelIndicatorCustomSlot indicatorSlot = (FuelIndicatorCustomSlot) slot;
							if (responsibleItem(indicatorSlot.getPlaceholder()) == item) {
								return "This item is used as a fuel indicator placeholder in container " + container.getName();
							}
							if (responsibleItem(indicatorSlot.getDisplay()) == item) {
								return "This item is used as a fuel indicator display in container " + container.getName();
							}
						} else if (slot instanceof InputCustomSlot) {
							InputCustomSlot inputSlot = (InputCustomSlot) slot;
							if (responsibleItem(inputSlot.getPlaceholder()) == item) {
								return "This item is used as input placeholder in container " + container.getName();
							}
						} else if (slot instanceof OutputCustomSlot) {
							OutputCustomSlot outputSlot = (OutputCustomSlot) slot;
							if (responsibleItem(outputSlot.getPlaceholder()) == item) {
								return "This item is used as output placeholder in container " + container.getName();
							}
						} else if (slot instanceof ProgressIndicatorCustomSlot) {
							ProgressIndicatorCustomSlot indicatorSlot = (ProgressIndicatorCustomSlot) slot;
							if (responsibleItem(indicatorSlot.getPlaceHolder()) == item) {
								return "This item is used as progress indicator placeholder in container " + container.getName();
							}
							if (responsibleItem(indicatorSlot.getDisplay()) == item) {
								return "This item is used as progress indicator display in container " + container.getName();
							}
						} else if (slot instanceof StorageCustomSlot) {
							StorageCustomSlot storageSlot = (StorageCustomSlot) slot;
							if (responsibleItem(storageSlot.getPlaceHolder()) == item) {
								return "This item is used as storage placeholder in container " + container.getName();
							}
						}
					}
				}

				// Protect the recipes of custom containers
				for (ContainerRecipe recipe : container.getRecipes()) {
					for (InputEntry input : recipe.getInputs()) {
						if (input.getIngredient() instanceof CustomItemIngredient) {
							CustomItemIngredient customIngredient = (CustomItemIngredient) input.getIngredient();
							if (customIngredient.getItem() == item) {
								return "The container " + container.getName() + " has this item as input of a recipe";
							}
						}

						if (hasRemainingCustomItem(input.getIngredient(), item)) {
							return "The container " + container.getName() + " has this item as a remaining input of a recipe";
						}
					}
					for (OutputEntry output : recipe.getOutputs()) {
						for (OutputTable.Entry tableEntry : output.getOutputTable().getEntries()) {
							if (tableEntry.getResult() instanceof CustomItemResult) {
								CustomItemResult customResult = (CustomItemResult) tableEntry.getResult();
								if (customResult.getItem() == item) {
									return "The container " + container.getName() + " has this item as output of a recipe";
								}
							}
						}
					}
				}
			}
		}
		if (!items.remove(item)) {
			return "This item is not in the item set";
		}
		
		deletedItems.add(item.getName());
		return null;
	}

	/**
	 * Attempts to add a shaped recipe with the specified ingredients and result
	 * to this ItemSet. If the recipe can be added, it will be added. If the recipe
	 * can't be added, the reason is returned.
	 * 
	 * @param ingredients The ingredients of the recipe to add
	 * @param result      The result of the recipe to add
	 * @return The reason why the recipe can't be added, or null if the recipe was
	 *         added successfully
	 */
	public String addShapedRecipe(Ingredient[] ingredients, Result result) {
		if (!bypassChecks()) {
			for (Recipe recipe : recipes)
				if (recipe.hasConflictingShapedIngredients(ingredients))
					return "The ingredients of another recipe conflict with these ingredients.";
		}
		recipes.add(new ShapedRecipe(ingredients, result));
		return null;
	}

	/**
	 * Attempts to change the ingredients and result of the specified ShapedRecipe.
	 * If the recipe can be changed, it will be changed. If the recipe can't be
	 * changed, the reason is returned.
	 * 
	 * @param previous    The recipe to change
	 * @param ingredients The new ingredients for the recipe
	 * @param result      The new result for the recipe
	 * @return The reason the recipe can't be changed, or null if the recipe changed
	 *         succesfully.
	 */
	public String changeShapedRecipe(ShapedRecipe previous, Ingredient[] ingredients, Result result) {
		if (!bypassChecks()) {
			boolean has = false;
			for (Recipe recipe : recipes) {
				if (recipe == previous) {
					has = true;
					break;
				} else if (recipe.hasConflictingShapedIngredients(ingredients)) {
					return "Another shaped recipe (" + recipe.getResult() + ") has conflicting ingredients";
				}
			}
			if (!has)
				return "That recipe is not in this item set";
		}
		previous.setIngredients(ingredients);
		previous.setResult(result);
		return null;
	}

	/**
	 * Attempts to add a shapeless recipe with the specified ingredients and result
	 * to this set. If the recipe can be added, it will be added. If the recipe
	 * can't be added, the reason is returned.
	 * 
	 * @param ingredients The ingredients of the shapeless recipe
	 * @param result      The result of the shapeless recipe
	 * @return The reason the recipe could not be added, or null if the recipe was
	 *         added successfully
	 */
	public String addShapelessRecipe(Ingredient[] ingredients, Result result) {
		if (!bypassChecks()) {
			if (ingredients.length == 0)
				return "Recipes must have at least 1 ingredient";
			if (ingredients.length > 9)
				return "Recipes can have at most 9 ingredients";
			for (Recipe recipe : recipes)
				if (recipe.hasConflictingShapelessIngredients(ingredients))
					return "Another shapeless recipe (" + recipe.getResult() + ") has conflicting ingredients";
		}
		recipes.add(new ShapelessRecipe(result, ingredients));
		return null;
	}

	/**
	 * Attempts to change the ingredients and result of the specified shapeless
	 * recipe. If the recipe can be changed, it will be changed. If the recipe can't
	 * be changed, the reason is returned.
	 * 
	 * @param previous       The shapeless recipe to change
	 * @param newIngredients The new ingredients of the recipe
	 * @param newResult      The new result of the recipe
	 * @return The reason the recipe could not be changed, or null if the recipe was
	 *         changed successfully.
	 */
	public String changeShapelessRecipe(ShapelessRecipe previous, Ingredient[] newIngredients, Result newResult) {
		if (!bypassChecks()) {
			if (newIngredients.length == 0)
				return "Recipes must have at least 1 ingredient";
			if (newIngredients.length > 9)
				return "Recipes can have at most 9 ingredients";
			boolean has = false;
			for (Recipe recipe : recipes) {
				if (recipe == previous) {
					has = true;
					break;
				} else if (recipe.hasConflictingShapelessIngredients(newIngredients)) {
					return "Another shapeless recipe (" + recipe.getResult() + ") has conflicting ingredients";
				}
			}
			if (!has)
				return "That recipe is not in this item set";
		}
		previous.setIngredients(newIngredients);
		previous.setResult(newResult);
		return null;
	}

	public void removeRecipe(Recipe recipe) {
		if (!recipes.remove(recipe) && !bypassChecks()) {
			throw new IllegalArgumentException("The given recipe was not in the recipe list!");
		}
	}
	
	private String validateDrop(Drop d) {
		if (d == null) {
			return "The drop is null";
		}
		if (d.getDropTable() == null) {
			return "The drop table is null";
		}
		return d.getDropTable().validate();
	}
	
	public String addBlockDrop(BlockDrop drop) {
		if (!bypassChecks()) {
			if (drop == null)
				return "The blockDrop is null";
			if (drop.getBlock() == null)
				return "The block is null";
			String dropError = validateDrop(drop.getDrop());
			if (dropError != null)
				return dropError;
		}
		blockDrops.add(drop);
		return null;
	}
	
	public String changeBlockDrop(BlockDrop old, BlockType newBlock, Drop newDrop) {
		if (!bypassChecks()) {
			if (old == null)
				return "The old blockDrop is null";
			if (!blockDrops.contains(old)) {
				return "The old blockDrop was not in this item set";
			}
			if (newBlock == null)
				return "The new block is null";
			String dropError = validateDrop(newDrop);
			if (dropError != null)
				return dropError;
		}
		old.setBlock(newBlock);
		old.setDrop(newDrop);
		return null;
	}
	
	public void removeBlockDrop(BlockDrop drop) {
		if (!blockDrops.remove(drop) && !bypassChecks()) {
			throw new IllegalArgumentException("The drop " + drop + " was not in the block drop list!");
		}
	}
	
	public String addMobDrop(EntityDrop drop) {
		if (!bypassChecks()) {
			if (drop == null)
				return "The mob drop is null";
			if (drop.getEntityType() == null)
				return "The entity type is null";
			String dropError = validateDrop(drop.getDrop());
			if (dropError != null)
				return dropError;
			if (mobDrops.contains(drop))
				return "That mob drop is already in the mob drop list";
		}
		mobDrops.add(drop);
		return null;
	}
	
	public String changeMobDrop(EntityDrop old, CIEntityType newType, String newRequiredName, Drop newDrop) {
		if (!bypassChecks()) {
			if (newType == null)
				return "The selected entity type is null";
			String dropError = validateDrop(newDrop);
			if (dropError != null)
				return dropError;
			if (!mobDrops.contains(old))
				return "The mob drop you are changing is not in the mob drop list";
		}
		old.setEntityType(newType);
		old.setRequiredName(newRequiredName);
		old.setDrop(newDrop);
		return null;
	}
	
	public void removeMobDrop(EntityDrop drop) {
		if (!mobDrops.remove(drop) && !bypassChecks()) {
			throw new IllegalArgumentException("The drop " + drop + " was not in the mob drop list!");
		}
	}
	
	private String addProjectileCover(EditorProjectileCover cover) {
		if (!bypassChecks()) {
			if (cover == null)
				return "The projectile cover can't be null";
			String nameError = checkName(cover.name);
			if (nameError != null)
				return nameError;
			if (cover.itemType == null)
				return "The internal item type can't be null";
			if (!cover.itemType.canServe(Category.PROJECTILE_COVER))
				return "The selected internal item type can't be used as projectile cover";
			if (hasProjectileCover(cover.name))
				return "There is already a projectile cover with that name";
			if (projectileCovers.contains(cover))
				return "That projectile cover is already in the list of projectile covers";
		}
		projectileCovers.add(cover);
		return null;
	}
	
	private String changeProjectileCover(EditorProjectileCover original, CustomItemType newType,
			String newName) {
		if (!bypassChecks()) {
			if (original == null)
				return "Can't change null projectile covers";
			if (!projectileCovers.contains(original))
				return "The projectile cover to change is not in the list of projectile covers";
			if (newType == null)
				return "The internal item type can't be null";
			if (!newType.canServe(Category.PROJECTILE_COVER))
				return "This internal item type can't be used as projectile cover";
			String nameError = checkName(newName);
			if (nameError != null)
				return nameError;
			EditorProjectileCover sameName = getProjectileCoverByName(newName);
			if (sameName != null && sameName != original)
				return "There is another projectile cover with that name";
		}
		
		original.itemType = newType;
		original.name = newName;
		return null;
	}
	
	/**
	 * Attempts to change the properties of the given sphere projectile cover to the given values.
	 * If the given values are valid, the properties of the given projectile cover will be set to those
	 * values.
	 * If not, the projectile cover will not be changed and a human-readable reason why the values are
	 * invalid will be returned.
	 * @param original The old projectile cover
	 * @param newType The new internal item type
	 * @param newName The new name
	 * @param newTexture The new texture
	 * @param newSlotsPerAxis The new value for the slotsPerAxis field
	 * @param newScale The new scale
	 * @return null if the cover was changed successfully, or the reason it wasn't
	 */
	public String changeSphereProjectileCover(SphereProjectileCover original, CustomItemType newType, 
			String newName, NamedImage newTexture, int newSlotsPerAxis, double newScale) {
		
		if (!bypassChecks()) {
			if (newTexture == null)
				return "You must select a texture";
			if (!textures.contains(newTexture))
				return "The selected texture is not in the list of textures";
			if (newSlotsPerAxis <= 0)
				return "The slots per axis must be a positive integer";
			if (newSlotsPerAxis > 50)
				return "The slots per axis can't be larger than 50";
			if (!(newScale > 0))
				return "The scale must be greater than zero";
		}
		
		String error = changeProjectileCover(original, newType, newName);
		if (error == null) {
			original.texture = newTexture;
			original.slotsPerAxis = newSlotsPerAxis;
			original.scale = newScale;
		}
		return error;
	}
	
	/**
	 * Attempts to add the given projectile cover to this item set. 
	 * If the given cover can be added, it will be added and this method will return null.
	 * If the given cover can't be added, it won't be added and this method will return a String
	 * containing a human-readable message why it couldn't be added.
	 * @param cover The projectile cover to add to this item set
	 * @return null if added successfully, or the reason it was not added successfully
	 */
	public String addSphereProjectileCover(SphereProjectileCover cover) {
		if (!bypassChecks()) {
			if (cover == null)
				return "Can't add null covers";
			if (cover.texture == null)
				return "You must select a texture";
			if (!textures.contains(cover.texture))
				return "The selected texture is not in the textures list";
			
			// This is NOT equivalent to <= 0.0 because this also handles NaN
			if (!(cover.scale > 0.0))
				return "The scale must be greater than 0";
			
			if (cover.slotsPerAxis <= 0)
				return "The slots per axis must be a positive integer";
			if (cover.slotsPerAxis > 50)
				return "The slots per axis must not be larger than 50 (would get expensive)";
		}
		return addProjectileCover(cover);
	}
	
	/**
	 * Attempts to change the properties of the given custom projectile cover to the given values. If those
	 * values are valid, the properties of the projectile cover will be changed to those values and this
	 * method will return null.
	 * If not, the projectile cover won't be changed and a human-readable reason why they aren't valid
	 * will be returned.
	 * @param original The projectile cover to change
	 * @param newType The new internal item type
	 * @param newName The new name
	 * @param newModel The new item model
	 * @return null if the projectile cover was changed successfully, or the reason it wasn't
	 */
	public String changeCustomProjectileCover(CustomProjectileCover original, CustomItemType newType, 
			String newName, byte[] newModel) {
		
		if (!bypassChecks()) {
			if (newModel == null)
				return "You must select a model";
		}
		
		String error = changeProjectileCover(original, newType, newName);
		if (error == null) {
			original.model = newModel;
		}
		return error;
	}
	
	/**
	 * Attempts to add the given projectile cover to this item set. 
	 * If the given cover can be added, it will be added and this method will return null.
	 * If the given cover can't be added, it won't be added and this method will return a String
	 * containing a human-readable message why it couldn't be added.
	 * @param cover The projectile cover to add to this item set
	 * @return null if added successfully, or the reason it was not added successfully
	 */
	public String addCustomProjectileCover(CustomProjectileCover cover) {
		if (!bypassChecks()) {
			if (cover == null)
				return "Can't add null covers";
			if (cover.model == null)
				return "You must select a model";
		}
		return addProjectileCover(cover);
	}
	
	/**
	 * Attempts to remove the given projectile cover from this item set. If it can be removed, it will be
	 * removed and this method will return null.
	 * If it can't be removed (for instance because it is still in use), this method will return the reason
	 * it can't be removed.
	 * @param cover The projectile cover to remove
	 * @return null if the projectile cover was removed, or the reason it can't be removed
	 */
	public String removeProjectileCover(EditorProjectileCover cover) {
		if (!bypassChecks()) {
			if (cover == null)
				return "Can't delete null";
			for (CIProjectile projectile : projectiles)
				if (projectile.cover == cover)
					return "The projectile " + projectile.name + " still uses this cover";
		}
		if (!projectileCovers.remove(cover) && !bypassChecks())
			return "The given projectile cover wasn't in the list of projectile covers";
		else
			return null;
	}
	
	/**
	 * Attempts to add the given projectile to the collection of projectiles of this item set. If the
	 * projectile can be added, it will be added. If not, the reason will be returned as human-readable
	 * String.
	 * @param projectile The projectile that should be added
	 * @return null if the projectile was added successfully, or the reason it couldn't be added
	 */
	public String addProjectile(CIProjectile projectile) {
		if (!bypassChecks()) {
			String error = validateProjectile(projectile);
			if (error != null)
				return error;
			if (getProjectileByName(projectile.name) != null)
				return "There is already a projectile with that name";
		}
		projectiles.add(projectile);
		return null;
	}
	
	/**
	 * Attempts to change the properties of the given projectile to the given parameter values. If the
	 * projectile was changed successfully, null will be returned. If the projectile couldn't be changed,
	 * it won't be changed and a human-readable error message will be returned.
	 * @return null if the projectile changed successfully, or the reason it couldn't be changed
	 */
	public String changeProjectile(CIProjectile original, String newName, float newDamage,
			float newMinLaunchAngle, float newMaxLaunchAngle, 
			float newMinLaunchSpeed, float newMaxLaunchSpeed, float newGravity, int newMaxLifeTime, 
			Collection<ProjectileEffects> newFlightEffects, Collection<ProjectileEffect> newImpactEffects,
			ProjectileCover newCover) {
		if (!bypassChecks()) {
			String error = validateProjectile(new CIProjectile(newName, newDamage, 
					newMinLaunchAngle, newMaxLaunchAngle, newMinLaunchSpeed, newMaxLaunchSpeed, newGravity, newMaxLifeTime,
					newFlightEffects, newImpactEffects, newCover));
			if (error != null)
				return error;
			if (!projectiles.contains(original))
				return "The projectile to change is not in the list of projectiles";
			CIProjectile sameName = getProjectileByName(newName);
			if (sameName != null && sameName != original)
				return "There is already a projectile with that name";
		}
		original.name = newName;
		original.damage = newDamage;
		original.minLaunchAngle = newMinLaunchAngle;
		original.maxLaunchAngle = newMaxLaunchAngle;
		original.minLaunchSpeed = newMinLaunchSpeed;
		original.maxLaunchSpeed = newMaxLaunchSpeed;
		original.gravity = newGravity;
		original.maxLifeTime = newMaxLifeTime;
		original.inFlightEffects = newFlightEffects;
		original.impactEffects = newImpactEffects;
		original.cover = newCover;
		return null;
	}
	
	/**
	 * Attempts to remove the given projectile from the list of projectiles. If the projectile can be
	 * removed, it will be removed and null will be returned. If it can't be removed, the reason will be
	 * returned.
	 * @param toRemove The projectile to remove from this item set
	 * @return null if the projectile was removed, or the reason it wasn't removed
	 */
	public String removeProjectile(CIProjectile toRemove) {
		if (!bypassChecks()) {
			for (CustomItem item : items) {
				if (item instanceof CustomWand && ((CustomWand) item).projectile == toRemove)
					return "The wand " + item.getName() + " is still using this projectile";
				// TODO Also check for guns once they are added
			}
		}
		if (!projectiles.remove(toRemove) && !bypassChecks())
			return "The projectile to remove was not in the list of projectiles";
		return null;
	}
	
	/**
	 * Attempts to add the given fuel registry to the list of fuel registries. If
	 * the fuel registry can be added, it will be added. If not, the reason why not
	 * will be returned as string.
	 * @param toAdd The fuel registry to be added
	 * @return null if the fuel registry was added successfully, or the reason if
	 * it was not added successfully
	 */
	public String addFuelRegistry(CustomFuelRegistry toAdd) {
		if (!bypassChecks()) {
			for (CustomFuelRegistry existing : fuelRegistries) {
				if (existing.getName().equals(toAdd.getName())) {
					return "There is already a fuel registry with name " + toAdd.getName();
				}
			}
		}
		fuelRegistries.add(toAdd);
		return null;
	}
	
	/**
	 * Attempts to change the name and entries of the given fuel registry. If it is
	 * changed successfully, this method returns null. If it couldn't be changed,
	 * the reason will be returned.
	 * @param toModify The fuel registry that is to be modified
	 * @param newName The new name of the fuel registry
	 * @param newEntries The new entries of the fuel registry
	 * @return The reason the fuel registry coudldn't be changed, or null if it was
	 * changed successfully
	 */
	public String changeFuelRegistry(CustomFuelRegistry toModify, String newName, 
			Collection<FuelEntry> newEntries) {
		if (!bypassChecks()) {
			for (CustomFuelRegistry existing : fuelRegistries) {
				if (existing != toModify && existing.getName().equals(newName)) {
					return "There exists another fuel registry with name " + newName;
				}
			}
		}
		
		toModify.setName(newName);
		toModify.setEntries(newEntries);
		return null;
	}
	
	/**
	 * Attempts to remove the given fuel registry from the list of fuel registries.
	 * If it was removed successfully, null will be returned. If not, the reason
	 * why it couldn't be removed will be returned as string.
	 * @param toRemove The fuel registry that should be removed
	 * @return The reason why the fuel registry couldn't be removed, or null if it
	 * was removed successfully
	 */
	public String removeFuelRegistry(CustomFuelRegistry toRemove) {
		if (!bypassChecks()) {
			for (CustomContainer container : containers) {
				for (int x = 0; x < 9; x++) {
					for (int y = 0; y < container.getHeight(); y++) {
						
						CustomSlot slot = container.getSlot(x, y);
						if (slot instanceof FuelCustomSlot) {
							FuelCustomSlot fuel = (FuelCustomSlot) slot;
							if (fuel.getRegistry() == toRemove) {
								return "This fuel registry is in use by container " + container.getName();
							}
						}
					}
				}
			}
		}
		
		if (fuelRegistries.remove(toRemove) || bypassChecks()) {
			return null;
		} else {
			return "The fuel registry to remove wasn't in the list of fuel registries";
		}
	}
	
	/**
	 * Attempts to add the given container to the list of containers. Returns null
	 * if it was added successfully and returns the reason as string if not.
	 * @param toAdd The container to be added to the list of containers
	 * @return The reason the container couldn't be added, or null if it was added
	 * successfully
	 */
	public String addContainer(CustomContainer toAdd) {
		if (!bypassChecks()) {
			
			for (CustomContainer existing : containers) {
				if (existing.getName().equals(toAdd.getName())) {
					return "There is already a container with name " + toAdd.getName();
				}
			}
			
			if (toAdd.getSelectionIcon() == null) {
				return "You need to choose a selection icon";
			}
			
			if (toAdd.getHeight() < 1) {
				return "The height must be a positive integer";
			} else if (toAdd.getHeight() > 6) {
				return "The height can be at most 6";
			}
			
			if (toAdd.getFuelMode() == null) {
				return "You must choose a fuel mode";
			}
			if (toAdd.getVanillaType() == null) {
				return "You must choose a vanilla type";
			}
			
			for (CustomSlot slot : toAdd.getSlots()) {
				String slotError = validateSlot(slot, toAdd.getSlots());
				if (slotError != null) {
					return slotError;
				}
			}
			
			for (ContainerRecipe recipe : toAdd.getRecipes()) {
				String recipeError = validateContainerRecipe(recipe, toAdd.getSlots());
				if (recipeError != null) {
					return recipeError;
				}
			}
		}
		
		containers.add(toAdd);
		return null;
	}
	
	/**
	 * Attempts to modify the given container (set all its properties to the values
	 * passed to the rest of the parameters). If the container was changed
	 * successfully, null will be returned. Otherwise the reason it couldn't be
	 * changed will be returned.
	 * @param toModify The container to be modified
     * @param newSelectionIcon The (new) selection icon for the container
	 * @param newRecipes The (new) recipes for the container
	 * @param newFuelMode The (new) fuel mode for the container
	 * @param newSlots The (new) slots for the container
	 * @param newVanillaType The (new) vanilla type of the container
	 * @param becomesPersistent Whether or not the container should get 
	 * persistent storage
	 * @return The reason the container couldn't be changed, or null if it was
	 * changed successfully
	 */
	public String changeContainer(CustomContainer toModify,
			SlotDisplay newSelectionIcon, Collection<ContainerRecipe> newRecipes,
			FuelMode newFuelMode, CustomSlot[][] newSlots, 
			VanillaContainerType newVanillaType, boolean becomesPersistent) {
		
		if (!bypassChecks()) {
			
			if (newSelectionIcon == null) {
				return "You need to choose a selection icon";
			}
			
			if (newSlots[0].length < 1) {
				return "The height must be a positive integer";
			} else if (newSlots[0].length > 6) {
				return "The height can be at most 6";
			}
			
			if (newFuelMode == null) {
				return "You must choose a fuel mode";
			}
			if (newVanillaType == null) {
				return "You must choose a vanilla type";
			}
			
			for (CustomSlot slot : CustomContainer.slotIterable(newSlots)) {
				String slotError = validateSlot(slot, CustomContainer.slotIterable(newSlots));
				if (slotError != null) {
					return slotError;
				}
			}
			
			for (ContainerRecipe recipe : newRecipes) {
				String recipeError = validateContainerRecipe(recipe, CustomContainer.slotIterable(newSlots));
				if (recipeError != null) {
					return recipeError;
				}
			}
		}
		
		toModify.setSelectionIcon(newSelectionIcon);
		toModify.getRecipes().clear();
		toModify.getRecipes().addAll(newRecipes);
		toModify.setFuelMode(newFuelMode);
		toModify.setVanillaType(newVanillaType);
		toModify.setPersistentStorage(becomesPersistent);
		toModify.resize(newSlots[0].length);
		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < newSlots[x].length; y++) {
				toModify.setSlot(newSlots[x][y], x, y);
			}
		}
		
		return null;
	}
	
	/**
	 * Removes the given container from the list of containers.
	 * @param toRemove The container to be removed
	 * @return null if the container was removed successfully, or a string
	 * indicating that the given container wasn't in the list of containers
	 */
	public String removeContainer(CustomContainer toRemove) {
	    for (CustomItem item : items) {
	    	if (item instanceof CustomPocketContainer) {
	    		for (CustomContainer container : ((CustomPocketContainer) item).getContainers()) {
	    			if (container == toRemove) {
						return "This container is still used by the pocket container " + item.getName();
					}
				}
			}
		}
		if (containers.remove(toRemove)) {
			return null;
		} else {
			return "This container wasn't in the list of containers";
		}
	}
	
	public void addArmorTextures(ArmorTextures toAdd) throws ValidationException {
		for (Reference<ArmorTextures> existing : armorTextures) {
			if (existing.get().getName().equals(toAdd.getName())) {
				throw new ValidationException(
						"There is already a worn armor texture "
						+ "with name " + toAdd.getName()
				);
			}
		}
		
		armorTextures.add(new Reference<>(toAdd));
	}
	
	public void changeArmorTextures(
			Reference<ArmorTextures> toModify, ArmorTextures newProperties
	) throws ValidationException {
		for (Reference<ArmorTextures> existing : armorTextures) {
			if (existing != toModify && 
					existing.get().getName().equals(newProperties.getName())) {
				throw new ValidationException("There is a different worn armor "
						+ "texture with name " + existing.get().getName()
				);
			}
		}
		
		toModify.set(newProperties);
	}
	
	public String removeArmorTextures(Reference<ArmorTextures> toRemove) {
		for (CustomItem item : items) {
			if (item instanceof CustomArmor) {
				CustomArmor armor = (CustomArmor) item;
				if (armor.getWornTexture() != null && armor.getWornTexture().get() == toRemove.get()) {
					return "The armor " + armor.getName() + " still uses this worn texture";
				}
			}
		}
		
		if (armorTextures.remove(toRemove)) {
			return null;
		} else {
			return "This armor texture wasn't in the list of armor textures";
		}
	}

	/**
	 * Do not modify this collection directly!
	 * 
	 * @return The CustomItem collection of this ItemSet
	 */
	public Collection<CustomItem> getBackingItems() {
		return items;
	}

	/**
	 * Do not modify this collection directly!
	 * 
	 * @return The NamedImage collection of this ItemSet
	 */
	public Collection<NamedImage> getBackingTextures() {
		return textures;
	}

	/**
	 * Do not modify this collection directly!
	 * 
	 * @return The Recipe collection of this ItemSet
	 */
	public Collection<Recipe> getBackingRecipes() {
		return recipes;
	}
	
	/**
	 * Do not modify this collection directly!
	 * @return The mob drop collection of this ItemSet
	 */
	public Collection<EntityDrop> getBackingMobDrops(){
		return mobDrops;
	}
	
	/**
	 * Do not modify this collection directly!
	 * @return The block drop collection of this ItemSet
	 */
	public Collection<BlockDrop> getBackingBlockDrops(){
		return blockDrops;
	}
	
	/**
	 * Do not modify this collection directly!
	 * @return The projectile collection of this ItemSet
	 */
	public Collection<CIProjectile> getBackingProjectiles(){
		return projectiles;
	}
	
	/**
	 * Do not modify this collection directly!
	 * @return The projectile cover collection of this ItemSet
	 */
	public Collection<EditorProjectileCover> getBackingProjectileCovers(){
		return projectileCovers;
	}
	
	public Collection<CustomFuelRegistry> getBackingFuelRegistries() {
		return fuelRegistries;
	}
	
	public Collection<CustomContainer> getBackingContainers() {
		return containers;
	}
	
	public Collection<Reference<ArmorTextures>> getBackingArmorTextures() {
		return armorTextures;
	}

	@Override
	public CustomItem getCustomItemByName(String name) {
		for (CustomItem item : items)
			if (item.getName().equals(name))
				return item;
		return null;
	}
	
	public NamedImage getTextureByName(String name) {
		for (NamedImage texture : textures) {
			if (texture.getName().equals(name)) {
				return texture;
			}
		}
		return null;
	}
	
	public EditorProjectileCover getProjectileCoverByName(String name) {
		for (EditorProjectileCover cover : projectileCovers)
			if (cover.name.equals(name))
				return cover;
		return null;
	}
	
	public boolean hasCustomItem(String name) {
		return getCustomItemByName(name) != null;
	}
	
	public boolean hasTexture(String name) {
		return getTextureByName(name) != null;
	}
	
	public boolean hasProjectileCover(String name) {
		return getProjectileCoverByName(name) != null;
	}
	
	public CIProjectile getProjectileByName(String name) {
		for (CIProjectile projectile : projectiles)
			if (projectile.name.equals(name))
				return projectile;
		return null;
	}
	
	public boolean hasProjectile(String name) {
		return getProjectileByName(name) != null;
	}
	
	public CustomFuelRegistry getFuelRegistryByName(String name) {
		for (CustomFuelRegistry registry : fuelRegistries)
			if (registry.getName().equals(name))
				return registry;
		return null;
	}
	
	public CustomContainer getContainerByName(String name) {
		
		for (CustomContainer container : containers) {
			if (container.getName().equals(name)) {
				return container;
			}
		}
		
		return null;
	}
	
	public ReadOnlyReference<ArmorTextures> getArmorTexture(String name) {
		for (Reference<ArmorTextures> armorTexture : armorTextures) {
			if (armorTexture.get().getName().equals(name)) {
				return new ReadOnlyReference<>(armorTexture);
			}
		}
		
		return null;
	}
	
	private CustomItemResult createCustomItemResult(String itemName, byte amount) {
		CustomItem item = getCustomItemByName(itemName);
		return new CustomItemResult(item, amount);
	}
}
