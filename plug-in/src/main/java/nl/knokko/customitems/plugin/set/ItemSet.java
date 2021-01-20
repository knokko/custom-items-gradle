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
package nl.knokko.customitems.plugin.set;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import nl.knokko.core.plugin.item.ItemHelper;
import nl.knokko.core.plugin.item.UnknownMaterialException;
import nl.knokko.customitems.container.CustomContainer;
import nl.knokko.customitems.container.fuel.CustomFuelRegistry;
import nl.knokko.customitems.damage.DamageResistances;
import nl.knokko.customitems.drops.BlockDrop;
import nl.knokko.customitems.drops.BlockType;
import nl.knokko.customitems.drops.CIEntityType;
import nl.knokko.customitems.drops.Drop;
import nl.knokko.customitems.drops.EntityDrop;
import nl.knokko.customitems.effect.EffectType;
import nl.knokko.customitems.effect.EquippedPotionEffect;
import nl.knokko.customitems.effect.PotionEffect;
import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.customitems.encoding.SetEncoding;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.AttributeModifier.Attribute;
import nl.knokko.customitems.item.AttributeModifier.Operation;
import nl.knokko.customitems.item.AttributeModifier.Slot;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.item.CustomItemType;
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
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.container.ContainerInfo;
import nl.knokko.customitems.plugin.recipe.CustomRecipe;
import nl.knokko.customitems.plugin.recipe.ShapedCustomRecipe;
import nl.knokko.customitems.plugin.recipe.ShapelessCustomRecipe;
import nl.knokko.customitems.plugin.recipe.ingredient.CustomIngredient;
import nl.knokko.customitems.plugin.recipe.ingredient.DataVanillaIngredient;
import nl.knokko.customitems.plugin.recipe.ingredient.Ingredient;
import nl.knokko.customitems.plugin.recipe.ingredient.NoIngredient;
import nl.knokko.customitems.plugin.recipe.ingredient.SimpleVanillaIngredient;
import nl.knokko.customitems.plugin.set.item.BooleanRepresentation;
import nl.knokko.customitems.plugin.set.item.CustomArmor;
import nl.knokko.customitems.plugin.set.item.CustomBow;
import nl.knokko.customitems.plugin.set.item.CustomHelmet3D;
import nl.knokko.customitems.plugin.set.item.CustomHoe;
import nl.knokko.customitems.plugin.set.item.CustomItem;
import nl.knokko.customitems.plugin.set.item.CustomItemNBT;
import nl.knokko.customitems.plugin.set.item.CustomShears;
import nl.knokko.customitems.plugin.set.item.CustomShield;
import nl.knokko.customitems.plugin.set.item.CustomTool;
import nl.knokko.customitems.plugin.set.item.CustomTrident;
import nl.knokko.customitems.plugin.set.item.CustomWand;
import nl.knokko.customitems.plugin.set.item.SimpleCustomItem;
import nl.knokko.customitems.plugin.util.ItemUtils;
import nl.knokko.customitems.projectile.CIProjectile;
import nl.knokko.customitems.projectile.ProjectileCover;
import nl.knokko.customitems.trouble.IntegrityException;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.StringEncoder;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BooleanArrayBitOutput;
import nl.knokko.util.bits.ByteArrayBitInput;

public class ItemSet implements ItemSetBase {

	private CustomRecipe[] recipes;

	private final Map<String, ContainerInfo> containerInfo;

	private CustomItem[] items;
	
	private ProjectileCover[] projectileCovers;
	private CIProjectile[] projectiles;
	
	private Collection<CustomFuelRegistry> fuelRegistries;
	private Collection<CustomContainer> containers;
	
	private BlockDrop[][] blockDropMap;
	private EntityDrop[][] mobDropMap;
	
	private String[] deletedItems;
	
	private final Collection<String> errors;

	public ItemSet() {
		containerInfo = new HashMap<>();
		
		items = new CustomItem[0];
		recipes = new CustomRecipe[0];
		
		projectileCovers = new ProjectileCover[0];
		projectiles = new CIProjectile[0];
		
		containers = new ArrayList<>(0);
		
		blockDropMap = new BlockDrop[BlockType.AMOUNT][0];
		mobDropMap = new EntityDrop[CIEntityType.AMOUNT][0];
		
		errors = new ArrayList<>();
		deletedItems = new String[0];
	}

	public ItemSet(BitInput input) throws UnknownEncodingException, IntegrityException, UnknownMaterialException {
		containerInfo = new HashMap<>();
		
		byte encoding = input.readByte();
		
		// Note that ENCODING_2 and ENCODING_4 are editor-only
		if (encoding == SetEncoding.ENCODING_1)
			load1(input);
		else if (encoding == SetEncoding.ENCODING_3)
			load3(input);
		else if (encoding == SetEncoding.ENCODING_5)
			load5(input);
		else if (encoding == SetEncoding.ENCODING_6)
			load6(input);
		else if (encoding == SetEncoding.ENCODING_7)
			load7(input);
		else if (encoding == SetEncoding.ENCODING_8)
			load8(input);
		else
			throw new UnknownEncodingException("ItemSet", encoding);
		
		errors = new ArrayList<>(0);
	}
	
	private void addCustomContainer(CustomContainer toAdd) {
		containerInfo.put(toAdd.getName(), new ContainerInfo(toAdd));
		containers.add(toAdd);
	}
	
	public void addError(String error) {
		errors.add(error);
	}
	
	public Iterable<String> getErrors() {
		return errors;
	}
	
	public boolean hasErrors() {
		return !errors.isEmpty();
	}

	private void load1(BitInput input) throws UnknownEncodingException, UnknownMaterialException {
		// We don't store the export time of hash in this version, so use back-up
		CustomItemsPlugin.getInstance().setExportTime(generateFakeExportTime());
		
		// Items
		int itemSize = input.readInt();
		items = new CustomItem[itemSize];
		for (int counter = 0; counter < itemSize; counter++)
			register(loadItem(input), counter);

		// Recipes
		int recipeAmount = input.readInt();
		recipes = new CustomRecipe[recipeAmount];
		for (int counter = 0; counter < recipeAmount; counter++)
			register(loadRecipe(input), counter);
		
		// There are no drops in this encoding
		blockDropMap = new BlockDrop[BlockType.AMOUNT][0];
		mobDropMap = new EntityDrop[CIEntityType.AMOUNT][0];
		
		// There are no custom containers in this encoding
		containers = new ArrayList<>(0);
		fuelRegistries = new ArrayList<>(0);
		
		// Deleted items are not remembered in this encoding
		deletedItems = new String[0];
	}

	// Just like the ItemSet of Editor doesn't have export2, this doesn't have load2

	private void load3(BitInput input) throws UnknownEncodingException, UnknownMaterialException {
		// We don't store the export time of hash in this version, so use back-up
		CustomItemsPlugin.getInstance().setExportTime(generateFakeExportTime());
				
		// Items
		int itemSize = input.readInt();
		items = new CustomItem[itemSize];
		for (int counter = 0; counter < itemSize; counter++)
			register(loadItem(input), counter);

		// Recipes
		int recipeAmount = input.readInt();
		recipes = new CustomRecipe[recipeAmount];
		for (int counter = 0; counter < recipeAmount; counter++)
			register(loadRecipe(input), counter);
		
		int numBlockDrops = input.readInt();
		blockDropMap = new BlockDrop[BlockType.AMOUNT][0];
		for (int counter = 0; counter < numBlockDrops; counter++) {
			register(BlockDrop.load(
					input, this::createCustomItemResultByName,
					() -> loadResult(input), this::getItem
			));
		}
		
		int numMobDrops = input.readInt();
		mobDropMap = new EntityDrop[CIEntityType.AMOUNT][0];
		for (int counter = 0; counter < numMobDrops; counter++) {
			register(EntityDrop.load(
					input, this::createCustomItemResultByName,
					() -> loadResult(input), this::getItem
			));
		}
		
		// There are no custom containers in this encoding
		containers = new ArrayList<>(0);
		fuelRegistries = new ArrayList<>(0);
		
		// Deleted items are not remembered in this encoding
		deletedItems = new String[0];
	}
	
	// Since ENCODING_4 is editor-only, there is no load4 method
	
	private void load5(BitInput input) throws UnknownEncodingException, UnknownMaterialException {
		// We don't store the export time of hash in this version, so use back-up
		CustomItemsPlugin.getInstance().setExportTime(generateFakeExportTime());
				
		// Projectiles
		int numProjectileCovers = input.readInt();
		projectileCovers = new ProjectileCover[numProjectileCovers];
		for (int index = 0; index < numProjectileCovers; index++) {
			PluginProjectileCover cover = new PluginProjectileCover(input);
			projectileCovers[index] = cover;
		}
		
		int numProjectiles = input.readInt();
		projectiles = new CIProjectile[numProjectiles];
		for (int index = 0; index < numProjectiles; index++)
			projectiles[index] = CIProjectile.fromBits(input, this);
		
		// Notify the projectiles that all projectiles are loaded
		for (CIProjectile projectile : projectiles)
			projectile.afterProjectilesAreLoaded(this);
		
		// Items
		int itemSize = input.readInt();
		items = new CustomItem[itemSize];
		for (int counter = 0; counter < itemSize; counter++)
			register(loadItem(input), counter);

		// Recipes
		int recipeAmount = input.readInt();
		recipes = new CustomRecipe[recipeAmount];
		for (int counter = 0; counter < recipeAmount; counter++)
			register(loadRecipe(input), counter);
		
		int numBlockDrops = input.readInt();
		blockDropMap = new BlockDrop[BlockType.AMOUNT][0];
		for (int counter = 0; counter < numBlockDrops; counter++) {
			register(BlockDrop.load(
					input, this::createCustomItemResultByName,
					() -> loadResult(input), this::getItem
			));
		}
		
		int numMobDrops = input.readInt();
		mobDropMap = new EntityDrop[CIEntityType.AMOUNT][0];
		for (int counter = 0; counter < numMobDrops; counter++) {
			register(EntityDrop.load(
					input, this::createCustomItemResultByName,
					() -> loadResult(input), this::getItem
			));
		}
		
		// There are no custom containers in this encoding
		containers = new ArrayList<>(0);
		fuelRegistries = new ArrayList<>(0);
		
		// Deleted items are not remembered in this encoding
		deletedItems = new String[0];
	}
	
	private void load6(BitInput rawInput) throws UnknownEncodingException, IntegrityException, UnknownMaterialException {
		
		long expectedHash = rawInput.readLong();
		byte[] content;
		try {
			// Catch undefined behavior
			content = rawInput.readByteArray();
		} catch (Throwable t) {
			throw new IntegrityException(t);
		}
		long actualHash = hash(content);
		if (expectedHash != actualHash) {
			throw new IntegrityException(expectedHash, actualHash);
		}
		
		BitInput input = new ByteArrayBitInput(content);
		
		// We don't store the export time in this encoding, but we do store a hash
		// Make it negative to prevent collisions with real export times
		CustomItemsPlugin.getInstance().setExportTime(-Math.abs(actualHash));
		
		// Projectiles
		int numProjectileCovers = input.readInt();
		projectileCovers = new ProjectileCover[numProjectileCovers];
		for (int index = 0; index < numProjectileCovers; index++) {
			PluginProjectileCover cover = new PluginProjectileCover(input);
			projectileCovers[index] = cover;
		}
		
		int numProjectiles = input.readInt();
		projectiles = new CIProjectile[numProjectiles];
		for (int index = 0; index < numProjectiles; index++)
			projectiles[index] = CIProjectile.fromBits(input, this);
		
		// Notify the projectiles that all projectiles are loaded
		for (CIProjectile projectile : projectiles)
			projectile.afterProjectilesAreLoaded(this);
		
		// Items
		int itemSize = input.readInt();
		items = new CustomItem[itemSize];
		for (int counter = 0; counter < itemSize; counter++)
			register(loadItem(input), counter);

		// Recipes
		int recipeAmount = input.readInt();
		recipes = new CustomRecipe[recipeAmount];
		for (int counter = 0; counter < recipeAmount; counter++)
			register(loadRecipe(input), counter);
		
		int numBlockDrops = input.readInt();
		blockDropMap = new BlockDrop[BlockType.AMOUNT][0];
		for (int counter = 0; counter < numBlockDrops; counter++) {
			register(BlockDrop.load(
					input, this::createCustomItemResultByName,
					() -> loadResult(input), this::getItem
			));
		}
		
		int numMobDrops = input.readInt();
		mobDropMap = new EntityDrop[CIEntityType.AMOUNT][0];
		for (int counter = 0; counter < numMobDrops; counter++) {
			register(EntityDrop.load(
					input, this::createCustomItemResultByName,
					() -> loadResult(input), this::getItem
			));
		}
		
		// There are no custom containers in this encoding
		containers = new ArrayList<>(0);
		fuelRegistries = new ArrayList<>(0);
		
		// Deleted items are not remembered in this encoding
		deletedItems = new String[0];
	}
	
	private void load7(BitInput rawInput) throws UnknownEncodingException, IntegrityException, UnknownMaterialException {
		
		long expectedHash = rawInput.readLong();
		byte[] content;
		try {
			// Catch undefined behavior
			content = rawInput.readByteArray();
		} catch (Throwable t) {
			throw new IntegrityException(t);
		}
		long actualHash = hash(content);
		if (expectedHash != actualHash) {
			throw new IntegrityException(expectedHash, actualHash);
		}
		
		BitInput input = new ByteArrayBitInput(content);
		
		// We don't store the export time in this encoding, but we do store a hash
		// Make it negative to prevent collisions with real export times
		CustomItemsPlugin.getInstance().setExportTime(-Math.abs(actualHash));
		
		// Projectiles
		int numProjectileCovers = input.readInt();
		projectileCovers = new ProjectileCover[numProjectileCovers];
		for (int index = 0; index < numProjectileCovers; index++) {
			PluginProjectileCover cover = new PluginProjectileCover(input);
			projectileCovers[index] = cover;
		}
		
		int numProjectiles = input.readInt();
		projectiles = new CIProjectile[numProjectiles];
		for (int index = 0; index < numProjectiles; index++)
			projectiles[index] = CIProjectile.fromBits(input, this);
		
		// Notify the projectiles that all projectiles are loaded
		for (CIProjectile projectile : projectiles)
			projectile.afterProjectilesAreLoaded(this);
		
		// Items
		int itemSize = input.readInt();
		items = new CustomItem[itemSize];
		for (int counter = 0; counter < itemSize; counter++)
			register(loadItem(input), counter);

		// Recipes
		int recipeAmount = input.readInt();
		recipes = new CustomRecipe[recipeAmount];
		for (int counter = 0; counter < recipeAmount; counter++)
			register(loadRecipe(input), counter);
		
		int numBlockDrops = input.readInt();
		blockDropMap = new BlockDrop[BlockType.AMOUNT][0];
		for (int counter = 0; counter < numBlockDrops; counter++) {
			register(BlockDrop.load(
					input, this::createCustomItemResultByName,
					() -> loadResult(input), this::getItem
			));
		}
		
		int numMobDrops = input.readInt();
		mobDropMap = new EntityDrop[CIEntityType.AMOUNT][0];
		for (int counter = 0; counter < numMobDrops; counter++) {
			register(EntityDrop.load(
					input, this::createCustomItemResultByName,
					() -> loadResult(input), this::getItem
			));
		}
		
		// Custom containers & fuel registries
		int numFuelRegistries = input.readInt();
		fuelRegistries = new ArrayList<>(numFuelRegistries);
		for (int counter = 0; counter < numFuelRegistries; counter++) {
			fuelRegistries.add(loadFuelRegistry(input));
		}
		
		int numContainers = input.readInt();
		containers = new ArrayList<>(numContainers);
		for (int counter = 0; counter < numContainers; counter++) {
			addCustomContainer(loadContainer(input));
		}
		
		// There are no deleted items in this encoding
		deletedItems = new String[0];
	}
	
	private void load8(BitInput rawInput) throws UnknownEncodingException, IntegrityException, UnknownMaterialException {
		
		long expectedHash = rawInput.readLong();
		byte[] content;
		try {
			// Catch undefined behavior
			content = rawInput.readByteArray();
		} catch (Throwable t) {
			throw new IntegrityException(t);
		}
		long actualHash = hash(content);
		if (expectedHash != actualHash) {
			throw new IntegrityException(expectedHash, actualHash);
		}
		
		BitInput input = new ByteArrayBitInput(content);
		
		CustomItemsPlugin.getInstance().setExportTime(input.readLong());
		
		// Projectiles
		int numProjectileCovers = input.readInt();
		projectileCovers = new ProjectileCover[numProjectileCovers];
		for (int index = 0; index < numProjectileCovers; index++) {
			PluginProjectileCover cover = new PluginProjectileCover(input);
			projectileCovers[index] = cover;
		}
		
		int numProjectiles = input.readInt();
		projectiles = new CIProjectile[numProjectiles];
		for (int index = 0; index < numProjectiles; index++)
			projectiles[index] = CIProjectile.fromBits(input, this);
		
		// Notify the projectiles that all projectiles are loaded
		for (CIProjectile projectile : projectiles)
			projectile.afterProjectilesAreLoaded(this);
		
		// Items
		int itemSize = input.readInt();
		items = new CustomItem[itemSize];
		for (int counter = 0; counter < itemSize; counter++)
			register(loadItem(input), counter);

		// Recipes
		int recipeAmount = input.readInt();
		recipes = new CustomRecipe[recipeAmount];
		for (int counter = 0; counter < recipeAmount; counter++)
			register(loadRecipe(input), counter);
		
		int numBlockDrops = input.readInt();
		blockDropMap = new BlockDrop[BlockType.AMOUNT][0];
		for (int counter = 0; counter < numBlockDrops; counter++) {
			register(BlockDrop.load(
					input, this::createCustomItemResultByName,
					() -> loadResult(input), this::getItem
			));
		}
		
		int numMobDrops = input.readInt();
		mobDropMap = new EntityDrop[CIEntityType.AMOUNT][0];
		for (int counter = 0; counter < numMobDrops; counter++) {
			register(EntityDrop.load(
					input, this::createCustomItemResultByName,
					() -> loadResult(input), this::getItem
			));
		}
		
		// Custom containers & fuel registries
		int numFuelRegistries = input.readInt();
		fuelRegistries = new ArrayList<>(numFuelRegistries);
		for (int counter = 0; counter < numFuelRegistries; counter++) {
			fuelRegistries.add(loadFuelRegistry(input));
		}
		
		int numContainers = input.readInt();
		containers = new ArrayList<>(numContainers);
		for (int counter = 0; counter < numContainers; counter++) {
			addCustomContainer(loadContainer(input));
		}
		
		// Deleted items
		int numDeletedItems = input.readInt();
		deletedItems = new String[numDeletedItems];
		for (int index = 0; index < numDeletedItems; index++) {
			deletedItems[index] = input.readString();
		}
	}
	
	private CustomFuelRegistry loadFuelRegistry(BitInput input) throws UnknownEncodingException {
		return CustomFuelRegistry.load(input, () -> loadIngredient(input));
	}
	
	private CustomContainer loadContainer(BitInput input) throws UnknownEncodingException {
		return CustomContainer.load(input, 
				this::getCustomItemByName, this::getFuelRegistryByName, 
				() -> loadIngredient(input),
				() -> loadResult(input)
		);
	}
	
	private ItemStack createCustomItemResultByName(String itemName, Byte amount) {
		return getItem(itemName).create(amount);
	}
	
	private CustomItem loadItem(BitInput input) throws UnknownEncodingException {
		
		BooleanArrayBitOutput rememberBits = new BooleanArrayBitOutput();
		
		class BitInputTracker extends BitInput {
			
			@Override
			public boolean readDirectBoolean() {
				boolean result = input.readDirectBoolean();
				rememberBits.addBoolean(result);
				return result;
			}
			
			@Override
			public byte readDirectByte() {
				byte result = input.readDirectByte();
				rememberBits.addByte(result);
				return result;
			}

			@Override
			public void increaseCapacity(int booleans) {
				input.increaseCapacity(booleans);
			}

			@Override
			public void terminate() {
				input.terminate();
			}

			@Override
			public void skip(long amount) {
				input.skip(amount);
			}
		}
		
		CustomItem result = loadItemInternal(new BitInputTracker(), 
				this::loadIngredient, this::getProjectileByName
		);
		result.setBooleanRepresentation(new BooleanRepresentation(rememberBits.getBytes()));
		return result;
	}
	
	public static CustomItem loadOldItem(BooleanRepresentation representation) {
		BitInput input = new ByteArrayBitInput(representation.getAsBytes());
		try {
			return loadItemInternal(input, bitInput -> {
				byte encoding = input.readByte();
				if (encoding == RecipeEncoding.Ingredient.NONE) {
					// No more data to skip
				} else if (encoding == RecipeEncoding.Ingredient.VANILLA_SIMPLE) {
					// Skip material name
					input.readJavaString();
				} else if (encoding == RecipeEncoding.Ingredient.VANILLA_DATA) {
					// Skip material name and data value
					input.readJavaString();
					input.readNumber((byte) 4, false);
				} else if (encoding == RecipeEncoding.Ingredient.CUSTOM) {
					// Skip custom item name
					input.readJavaString();
				} else {
					throw new UnknownEncodingException("BRIngredient", encoding);
				}
				return null;
			}, name -> null);
		} catch (UnknownEncodingException unknownEncoding) {
			Bukkit.getLogger().log(Level.SEVERE, 
					"Encountered an unknown encoding while deserializing the" +
					"boolean representation stored in the nbt of an item stack"
			);
			return null;
		}
	}

	private static CustomItem loadItemInternal(
			BitInput input, LoadIngredient loadIngredient, 
			ProjectileByName getProjectileByName
	) throws UnknownEncodingException {
		byte encoding = input.readByte();
		switch (encoding) {
		case ItemEncoding.ENCODING_SIMPLE_1 : return loadSimpleItem1(input);
		case ItemEncoding.ENCODING_SIMPLE_2 : return loadSimpleItem2(input);
		case ItemEncoding.ENCODING_SIMPLE_3 : return loadSimpleItem3(input);
		case ItemEncoding.ENCODING_SIMPLE_4 : return loadSimpleItem4(input);
		case ItemEncoding.ENCODING_SIMPLE_5 : return loadSimpleItem5(input);
		case ItemEncoding.ENCODING_SIMPLE_6 : return loadSimpleItem6(input);
		case ItemEncoding.ENCODING_SIMPLE_9 : return loadSimpleItem9(input);
		case ItemEncoding.ENCODING_TOOL_2 : return loadTool2(input);
		case ItemEncoding.ENCODING_TOOL_3 : return loadTool3(input, loadIngredient);
		case ItemEncoding.ENCODING_TOOL_4 : return loadTool4(input, loadIngredient);
		case ItemEncoding.ENCODING_TOOL_5 : return loadTool5(input, loadIngredient);
		case ItemEncoding.ENCODING_TOOL_6 : return loadTool6(input, loadIngredient);
		case ItemEncoding.ENCODING_SHEAR_5 : return loadShear5(input, loadIngredient);
		case ItemEncoding.ENCODING_SHEAR_6 : return loadShear6(input, loadIngredient);
		case ItemEncoding.ENCODING_BOW_3 : return loadBow3(input, loadIngredient);
		case ItemEncoding.ENCODING_BOW_4 : return loadBow4(input, loadIngredient);
		case ItemEncoding.ENCODING_BOW_5 : return loadBow5(input, loadIngredient);
		case ItemEncoding.ENCODING_BOW_6 : return loadBow6(input, loadIngredient);
		case ItemEncoding.ENCODING_ARMOR_4 : return loadArmor4(input, loadIngredient);
		case ItemEncoding.ENCODING_ARMOR_5 : return loadArmor5(input, loadIngredient);
		case ItemEncoding.ENCODING_ARMOR_6 : return loadArmor6(input, loadIngredient);
		case ItemEncoding.ENCODING_ARMOR_7 : return loadArmor7(input, loadIngredient);
		case ItemEncoding.ENCODING_ARMOR_8 : return loadArmor8(input, loadIngredient);
		case ItemEncoding.ENCODING_SHIELD_6 : return loadShield6(input, loadIngredient);
		case ItemEncoding.ENCODING_SHIELD_7 : return loadShield7(input, loadIngredient);
		case ItemEncoding.ENCODING_TRIDENT_7 : return loadTrident7(input, loadIngredient);
		case ItemEncoding.ENCODING_TRIDENT_8 : return loadTrident8(input, loadIngredient);
		case ItemEncoding.ENCODING_HOE_5 : return loadHoe5(input, loadIngredient);
		case ItemEncoding.ENCODING_HOE_6 : return loadHoe6(input, loadIngredient);
		case ItemEncoding.ENCODING_WAND_8: return loadWand8(input, getProjectileByName);
		case ItemEncoding.ENCODING_HELMET3D_9: return loadHelmet3d9(input, loadIngredient);
		case ItemEncoding.ENCODING_TOOL_9 : return loadTool9(input, loadIngredient);
		case ItemEncoding.ENCODING_SHEAR_9 : return loadShear9(input, loadIngredient);
		case ItemEncoding.ENCODING_BOW_9 : return loadBow9(input, loadIngredient);
		case ItemEncoding.ENCODING_ARMOR_9 : return loadArmor9(input, loadIngredient);
		case ItemEncoding.ENCODING_SHIELD_9 : return loadShield9(input, loadIngredient);
		case ItemEncoding.ENCODING_TRIDENT_9 : return loadTrident9(input, loadIngredient);
		case ItemEncoding.ENCODING_HOE_9 : return loadHoe9(input, loadIngredient);
		case ItemEncoding.ENCODING_WAND_9: return loadWand9(input, getProjectileByName);
		default : throw new UnknownEncodingException("Item", encoding);
	}
	}

	private static CustomItem loadSimpleItem1(BitInput input) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[0];
		return new SimpleCustomItem(
				itemType, damage, name, "", displayName, lore, attributes, 
				new Enchantment[0], 64, ItemFlag.getDefaultValues(), 
				new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
				new String[] {}, new ReplaceCondition[] {}, ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}

	private static CustomItem loadSimpleItem2(BitInput input) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		return new SimpleCustomItem(
				itemType, damage, name, "", displayName, lore, attributes, 
				new Enchantment[0], 64, ItemFlag.getDefaultValues(), 
				new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
				new String[] {}, new ReplaceCondition[] {}, ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}

	private static CustomItem loadSimpleItem3(BitInput input) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		return new SimpleCustomItem(
				itemType, damage, name, "", displayName, lore, attributes, 
				defaultEnchantments, 64, ItemFlag.getDefaultValues(), 
				new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
				new String[] {}, new ReplaceCondition[] {}, ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}

	private static CustomItem loadSimpleItem4(BitInput input) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		int stackSize = input.readByte();
		return new SimpleCustomItem(
				itemType, damage, name, "", displayName, lore, attributes, 
				defaultEnchantments, stackSize, ItemFlag.getDefaultValues(), 
				new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
				new String[] {}, new ReplaceCondition[] {}, ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}

	private static CustomItem loadSimpleItem5(BitInput input) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		int stackSize = input.readByte();
		boolean[] itemFlags = input.readBooleans(6);
		return new SimpleCustomItem(
				itemType, damage, name, "", displayName, lore, attributes, 
				defaultEnchantments, stackSize, itemFlags, new ArrayList<>(), 
				new ArrayList<>(), new ArrayList<>(), new String[] {},
				new ReplaceCondition[] {}, ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}
	
	private static CustomItem loadSimpleItem6(BitInput input) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		int stackSize = input.readByte();
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
		return new SimpleCustomItem(
				itemType, damage, name, "", displayName, lore, attributes, 
				defaultEnchantments, stackSize, itemFlags, playerEffects, 
				targetEffects, new ArrayList<>(), commands, 
				new ReplaceCondition[] {}, ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}
	
	private static CustomItem loadSimpleItem9(
			BitInput input
	) throws UnknownEncodingException {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String alias = input.readString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		int stackSize = input.readByte();
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
		
		return new SimpleCustomItem(
				itemType, damage, name, alias, displayName, lore, attributes, 
				defaultEnchantments, stackSize, itemFlags, playerEffects, 
				targetEffects, equippedEffects, commands, conditions, op,
				extraNbt, attackRange
		);
	}

	private static CustomItem loadTool2(BitInput input) throws UnknownEncodingException {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		int durability = input.readInt();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		return new CustomTool(
				itemType, damage, name, "", displayName, lore, attributes, 
				new Enchantment[0], durability, allowEnchanting, allowAnvil, 
				new NoIngredient(), ItemFlag.getDefaultValues(),
				CustomToolDurability.defaultEntityHitDurabilityLoss(itemType),
				CustomToolDurability.defaultBlockBreakDurabilityLoss(itemType), 
				new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 
				new String[] {}, new ReplaceCondition[] {}, ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}

	private static CustomItem loadTool3(
			BitInput input, LoadIngredient loadIngredient
	) throws UnknownEncodingException {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		int durability = input.readInt();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = loadIngredient.apply(input);
		return new CustomTool(
				itemType, damage, name, "", displayName, lore, attributes, 
				new Enchantment[0], durability, allowEnchanting, allowAnvil, 
				repairItem, ItemFlag.getDefaultValues(),
				CustomToolDurability.defaultEntityHitDurabilityLoss(itemType),
				CustomToolDurability.defaultBlockBreakDurabilityLoss(itemType), 
				new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
				new String[] {}, new ReplaceCondition[] {}, ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}

	private static CustomItem loadTool4(
			BitInput input, LoadIngredient loadIngredient
	) throws UnknownEncodingException {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = loadIngredient.apply(input);
		return new CustomTool(
				itemType, damage, name, "", displayName, lore, attributes, 
				defaultEnchantments, durability, allowEnchanting, allowAnvil, 
				repairItem, ItemFlag.getDefaultValues(),
				CustomToolDurability.defaultEntityHitDurabilityLoss(itemType),
				CustomToolDurability.defaultBlockBreakDurabilityLoss(itemType), 
				new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 
				new String[] {}, new ReplaceCondition[] {}, ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}

	private static CustomItem loadTool5(
			BitInput input, LoadIngredient loadIngredient
	) throws UnknownEncodingException {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = loadIngredient.apply(input);
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		return new CustomTool(
				itemType, damage, name, "", displayName, lore, attributes, 
				defaultEnchantments, durability, allowEnchanting, allowAnvil, 
				repairItem, itemFlags, entityHitDurabilityLoss, 
				blockBreakDurabilityLoss, new ArrayList<>(), new ArrayList<>(), 
				new ArrayList<>(), new String[] {}, new ReplaceCondition[] {}, 
				ConditionOperation.NONE, new ExtraItemNbt(), 1f
		);
	}
	
	private static CustomItem loadTool6(
			BitInput input, LoadIngredient loadIngredient
	) throws UnknownEncodingException {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = loadIngredient.apply(input);
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
		return new CustomTool(
				itemType, damage, name, "", displayName, lore, attributes, 
				defaultEnchantments, durability, allowEnchanting, allowAnvil, 
				repairItem, itemFlags, entityHitDurabilityLoss, 
				blockBreakDurabilityLoss, playerEffects, targetEffects, 
				new ArrayList<>(), commands, 
				new ReplaceCondition[] {}, ConditionOperation.NONE, 
				new ExtraItemNbt(), 1f
		);
	}
	
	private static CustomItem loadTool9(
			BitInput input, LoadIngredient loadIngredient
	) throws UnknownEncodingException {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String alias = input.readString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = loadIngredient.apply(input);
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
		
		return new CustomTool(
				itemType, damage, name, alias, displayName, lore, attributes, 
				defaultEnchantments, durability, allowEnchanting, allowAnvil, 
				repairItem, itemFlags, entityHitDurabilityLoss, blockBreakDurabilityLoss, 
				playerEffects, targetEffects, equippedEffects,
				commands, conditions, op, extraNbt, attackRange
		);
	}
	
	private static CustomItem loadHoe5(
			BitInput input, LoadIngredient loadIngredient
			) throws UnknownEncodingException {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = loadIngredient.apply(input);
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		int tillDurabilityLoss = input.readInt();
		return new CustomHoe(
				itemType, damage, name, "", displayName, lore, attributes, 
				defaultEnchantments, durability, allowEnchanting, allowAnvil, 
				repairItem, itemFlags, entityHitDurabilityLoss, 
				blockBreakDurabilityLoss, tillDurabilityLoss, new ArrayList<>(), 
				new ArrayList<>(), new ArrayList<>(), new String[] {}, 
				new ReplaceCondition[] {}, ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}
	
	private static CustomItem loadHoe6(
			BitInput input, LoadIngredient loadIngredient
	) throws UnknownEncodingException {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = loadIngredient.apply(input);
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
		return new CustomHoe(
				itemType, damage, name, "", displayName, lore, attributes, 
				defaultEnchantments, durability, allowEnchanting, allowAnvil, 
				repairItem, itemFlags, entityHitDurabilityLoss, 
				blockBreakDurabilityLoss, tillDurabilityLoss, playerEffects, 
				targetEffects, new ArrayList<>(), commands, 
				new ReplaceCondition[] {}, ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}
	
	private static CustomItem loadHoe9(
			BitInput input, LoadIngredient loadIngredient
	) throws UnknownEncodingException {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String alias = input.readString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = loadIngredient.apply(input);
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
		
		return new CustomHoe(
				itemType, damage, name, alias, displayName, lore, attributes, 
				defaultEnchantments, durability, allowEnchanting, allowAnvil, 
				repairItem, itemFlags, entityHitDurabilityLoss, 
				blockBreakDurabilityLoss, tillDurabilityLoss, playerEffects, 
				targetEffects, equippedEffects, commands, conditions, op,
				extraNbt, attackRange
		);
	}
	
	private static CustomItem loadShear5(
			BitInput input, LoadIngredient loadIngredient
	) throws UnknownEncodingException {
		CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = loadIngredient.apply(input);
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		int shearDurabilityLoss = input.readInt();
		return new CustomShears(
				damage, name, "", displayName, lore, attributes, 
				defaultEnchantments, durability, allowEnchanting, allowAnvil, 
				repairItem, itemFlags, entityHitDurabilityLoss, blockBreakDurabilityLoss, 
				shearDurabilityLoss, new ArrayList<>(), new ArrayList<>(), 
				new ArrayList<>(), new String[] {}, 
				new ReplaceCondition[] {}, ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}
	
	private static CustomItem loadShear6(
			BitInput input, LoadIngredient loadIngredient
	) throws UnknownEncodingException {
		CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = loadIngredient.apply(input);
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
		return new CustomShears(
				damage, name, "", displayName, lore, attributes, 
				defaultEnchantments, durability, allowEnchanting, allowAnvil, 
				repairItem, itemFlags, entityHitDurabilityLoss, 
				blockBreakDurabilityLoss, shearDurabilityLoss, playerEffects, 
				targetEffects, new ArrayList<>(), commands, 
				new ReplaceCondition[] {}, ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}
	
	private static CustomItem loadShear9(
			BitInput input, LoadIngredient loadIngredient
	) throws UnknownEncodingException {
		short damage = input.readShort();
		String name = input.readJavaString();
		String alias = input.readString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = loadIngredient.apply(input);
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

		return new CustomShears(
				damage, name, alias, displayName, lore, attributes, 
				defaultEnchantments, durability, allowEnchanting, allowAnvil, 
				repairItem, itemFlags, entityHitDurabilityLoss, 
				blockBreakDurabilityLoss, shearDurabilityLoss, playerEffects, 
				targetEffects, equippedEffects, commands, conditions, op,
				extraNbt, attackRange
		);
	}

	private static CustomItem loadArmor4(
			BitInput input, LoadIngredient loadIngredient
	) throws UnknownEncodingException {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = loadIngredient.apply(input);
		Color color;
		if (itemType.isLeatherArmor()) {
			color = Color.fromRGB(input.readByte() & 0xFF, input.readByte() & 0xFF, input.readByte() & 0xFF);
		} else {
			color = null;
		}
		return new CustomArmor(
				itemType, damage, name, "", displayName, lore, attributes, 
				defaultEnchantments, durability, allowEnchanting, allowAnvil, 
				repairItem, color, ItemFlag.getDefaultValues(), 0, 0,
				new DamageResistances(), new ArrayList<>(), new ArrayList<>(), 
				new ArrayList<>(), new String[] {}, 
				new ReplaceCondition[] {}, ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}

	private static CustomItem loadArmor5(
			BitInput input, LoadIngredient loadIngredient
	) throws UnknownEncodingException {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = loadIngredient.apply(input);
		Color color;
		if (itemType.isLeatherArmor()) {
			color = Color.fromRGB(input.readByte() & 0xFF, input.readByte() & 0xFF, input.readByte() & 0xFF);
		} else {
			color = null;
		}
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		return new CustomArmor(
				itemType, damage, name, "", displayName, lore, attributes, 
				defaultEnchantments, durability, allowEnchanting, allowAnvil, 
				repairItem, color, itemFlags, entityHitDurabilityLoss,
				blockBreakDurabilityLoss, new DamageResistances(), new ArrayList<>(), 
				new ArrayList<>(), new ArrayList<>(), new String[] {}, 
				new ReplaceCondition[] {}, ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}

	private static CustomItem loadArmor6(
			BitInput input, LoadIngredient loadIngredient
	) throws UnknownEncodingException {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = loadIngredient.apply(input);
		Color color;
		if (itemType.isLeatherArmor()) {
			color = Color.fromRGB(input.readByte() & 0xFF, input.readByte() & 0xFF, input.readByte() & 0xFF);
		} else {
			color = null;
		}
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		DamageResistances damageResistances = DamageResistances.load12(input);
		return new CustomArmor(
				itemType, damage, name, "", displayName, lore, attributes, 
				defaultEnchantments, durability, allowEnchanting, allowAnvil, 
				repairItem, color, itemFlags, entityHitDurabilityLoss,
				blockBreakDurabilityLoss, damageResistances, new ArrayList<>(), 
				new ArrayList<>(), new ArrayList<>(), new String[] {}, 
				new ReplaceCondition[] {}, ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}
	
	private static CustomItem loadArmor7(
			BitInput input, LoadIngredient loadIngredient
	) throws UnknownEncodingException {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = loadIngredient.apply(input);
		Color color;
		if (itemType.isLeatherArmor()) {
			color = Color.fromRGB(input.readByte() & 0xFF, input.readByte() & 0xFF, input.readByte() & 0xFF);
		} else {
			color = null;
		}
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		DamageResistances damageResistances = DamageResistances.load14(input);
		return new CustomArmor(
				itemType, damage, name, "", displayName, lore, attributes, 
				defaultEnchantments, durability, allowEnchanting, allowAnvil, 
				repairItem, color, itemFlags, entityHitDurabilityLoss,
				blockBreakDurabilityLoss, damageResistances, new ArrayList<>(), 
				new ArrayList<>(), new ArrayList<>(), new String[] {}, 
				new ReplaceCondition[] {}, ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}
	
	private static CustomItem loadArmor8(
			BitInput input, LoadIngredient loadIngredient
	) throws UnknownEncodingException {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = loadIngredient.apply(input);
		Color color;
		if (itemType.isLeatherArmor()) {
			color = Color.fromRGB(input.readByte() & 0xFF, input.readByte() & 0xFF, input.readByte() & 0xFF);
		} else {
			color = null;
		}
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		DamageResistances damageResistances = DamageResistances.load14(input);
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
		return new CustomArmor(
				itemType, damage, name, "", displayName, lore, attributes, 
				defaultEnchantments, durability, allowEnchanting, allowAnvil, 
				repairItem, color, itemFlags, entityHitDurabilityLoss,
				blockBreakDurabilityLoss, damageResistances, playerEffects, 
				targetEffects, new ArrayList<>(), commands, 
				new ReplaceCondition[] {}, ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}
	
	private static CustomItem loadArmor9(
			BitInput input, LoadIngredient loadIngredient
	) throws UnknownEncodingException {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String alias = input.readString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = loadIngredient.apply(input);
		Color color;
		if (itemType.isLeatherArmor()) {
			color = Color.fromRGB(input.readByte() & 0xFF, input.readByte() & 0xFF, input.readByte() & 0xFF);
		} else {
			color = null;
		}
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		DamageResistances damageResistances = DamageResistances.load14(input);
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
		
		// Discard the worn texture name, because the plug-in doesn't really care
		if (input.readBoolean()) {
			input.readString();
		}
		float attackRange = input.readFloat();
		
		return new CustomArmor(
				itemType, damage, name, alias, displayName, lore, attributes, 
				defaultEnchantments, durability, allowEnchanting, allowAnvil, 
				repairItem, color, itemFlags, entityHitDurabilityLoss,
				blockBreakDurabilityLoss, damageResistances, playerEffects, 
				targetEffects, equippedEffects, commands, conditions, op,
				extraNbt, attackRange
		);
	}
	
	private static CustomItem loadHelmet3d9(
			BitInput input, LoadIngredient loadIngredient
	) throws UnknownEncodingException {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String alias = input.readString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = loadIngredient.apply(input);
		Color color;
		if (itemType.isLeatherArmor()) {
			color = Color.fromRGB(input.readByte() & 0xFF, input.readByte() & 0xFF, input.readByte() & 0xFF);
		} else {
			color = null;
		}
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		DamageResistances damageResistances = DamageResistances.load14(input);
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
		// Discard the flag for the worn texture
		input.readBoolean();
		float attackRange = input.readFloat();
		
		return new CustomHelmet3D(
				itemType, damage, name, alias, displayName, lore, attributes, 
				defaultEnchantments, durability, allowEnchanting, allowAnvil, 
				repairItem, color, itemFlags, entityHitDurabilityLoss,
				blockBreakDurabilityLoss, damageResistances, playerEffects, 
				targetEffects, equippedEffects, commands, conditions, op,
				extraNbt, attackRange
		);
	}

	private static CustomBow loadBow3(
			BitInput input, LoadIngredient loadIngredient
	) throws UnknownEncodingException {
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
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
		Ingredient repairItem = loadIngredient.apply(input);
		return new CustomBow(
				damage, name, "", displayName, lore, attributes, new Enchantment[0], 
				durability, damageMultiplier, speedMultiplier, knockbackStrength, 
				gravity, allowEnchanting, allowAnvil, repairItem,
				ItemFlag.getDefaultValues(), 0, 0, 1, new ArrayList<>(), 
				new ArrayList<>(), new ArrayList<>(), new String[] {}, 
				new ReplaceCondition[] {}, ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}

	private static CustomBow loadBow4(
			BitInput input, LoadIngredient loadIngredient
	) throws UnknownEncodingException {
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
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
		Ingredient repairItem = loadIngredient.apply(input);
		return new CustomBow(
				damage, name, "", displayName, lore, attributes, defaultEnchantments, 
				durability, damageMultiplier, speedMultiplier, knockbackStrength, 
				gravity, allowEnchanting, allowAnvil, repairItem,
				ItemFlag.getDefaultValues(), 0, 0, 1, new ArrayList<>(), 
				new ArrayList<>(), new ArrayList<>(), new String[] {}, 
				new ReplaceCondition[] {}, ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}

	private static CustomBow loadBow5(
			BitInput input, LoadIngredient loadIngredient
	) throws UnknownEncodingException {
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
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
		Ingredient repairItem = loadIngredient.apply(input);
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		int shootDurabilityLoss = input.readInt();
		return new CustomBow(
				damage, name, "", displayName, lore, attributes, 
				defaultEnchantments, durability, damageMultiplier, speedMultiplier, 
				knockbackStrength, gravity, allowEnchanting, allowAnvil, repairItem,
				itemFlags, entityHitDurabilityLoss, blockBreakDurabilityLoss, 
				shootDurabilityLoss, new ArrayList<>(), new ArrayList<>(), 
				new ArrayList<>(), new String[] {}, 
				new ReplaceCondition[] {}, ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}
	
	private static CustomBow loadBow6(
			BitInput input, LoadIngredient loadIngredient
	) throws UnknownEncodingException {
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
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
		Ingredient repairItem = loadIngredient.apply(input);
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
		return new CustomBow(
				damage, name, "", displayName, lore, attributes, 
				defaultEnchantments, durability, damageMultiplier, speedMultiplier, 
				knockbackStrength, gravity, allowEnchanting, allowAnvil, repairItem,
				itemFlags, entityHitDurabilityLoss, blockBreakDurabilityLoss, 
				shootDurabilityLoss, playerEffects, targetEffects, new ArrayList<>(), 
				commands, new ReplaceCondition[] {}, ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}
	
	private static CustomBow loadBow9(
			BitInput input, LoadIngredient loadIngredient
			) throws UnknownEncodingException {
		short damage = input.readShort();
		String name = input.readJavaString();
		String alias = input.readString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
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
		Ingredient repairItem = loadIngredient.apply(input);
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
		
		return new CustomBow(
				damage, name, alias, displayName, lore, attributes, 
				defaultEnchantments, durability, damageMultiplier, speedMultiplier, 
				knockbackStrength, gravity, allowEnchanting, allowAnvil, repairItem,
				itemFlags, entityHitDurabilityLoss, blockBreakDurabilityLoss, 
				shootDurabilityLoss, playerEffects, targetEffects, 
				equippedEffects, commands, conditions, op, extraNbt, attackRange
		);
	}
	
	private static CustomItem loadShield6(
			BitInput input, LoadIngredient loadIngredient
	) throws UnknownEncodingException {
		CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = loadIngredient.apply(input);
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		double durabilityThreshold = input.readDouble();
		return new CustomShield(
				damage, name, "", displayName, lore, attributes, defaultEnchantments, 
				durability, allowEnchanting, allowAnvil, repairItem, itemFlags, 
				entityHitDurabilityLoss, blockBreakDurabilityLoss, 
				durabilityThreshold, new ArrayList<>(), new ArrayList<>(), 
				new ArrayList<>(), new String[] {}, 
				new ReplaceCondition[] {}, ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}
	
	private static CustomItem loadShield7(
			BitInput input, LoadIngredient loadIngredient
	) throws UnknownEncodingException {
		CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = loadIngredient.apply(input);
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		double durabilityThreshold = input.readDouble();
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
		return new CustomShield(
				damage, name, "", displayName, lore, attributes, defaultEnchantments, 
				durability, allowEnchanting, allowAnvil, repairItem, itemFlags, 
				entityHitDurabilityLoss, blockBreakDurabilityLoss, 
				durabilityThreshold, playerEffects, targetEffects, 
				new ArrayList<>(), commands, 
				new ReplaceCondition[] {}, ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}
	
	private static CustomItem loadShield9(
			BitInput input, LoadIngredient loadIngredient
	) throws UnknownEncodingException {
		short damage = input.readShort();
		String name = input.readJavaString();
		String alias = input.readString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = loadIngredient.apply(input);
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		double durabilityThreshold = input.readDouble();
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
		
		return new CustomShield(
				damage, name, alias, displayName, lore, attributes, 
				defaultEnchantments, durability, allowEnchanting, allowAnvil, 
				repairItem, itemFlags, entityHitDurabilityLoss, 
				blockBreakDurabilityLoss, durabilityThreshold, 
				playerEffects, targetEffects, equippedEffects, 
				commands, conditions, op, extraNbt, attackRange
		);
	}
	
	private static CustomItem loadTrident7(
			BitInput input, LoadIngredient loadIngredient
	) throws UnknownEncodingException {
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = loadIngredient.apply(input);
		boolean[] itemFlags = input.readBooleans(6);
		int entityHitDurabilityLoss = input.readInt();
		int blockBreakDurabilityLoss = input.readInt();
		int throwDurabilityLoss = input.readInt();
		double throwDamageMultiplier = input.readDouble();
		double speedMultiplier = input.readDouble();
		return new CustomTrident(
				damage, name, "", displayName, lore, attributes, 
				defaultEnchantments, durability, allowEnchanting, allowAnvil, 
				throwDamageMultiplier, speedMultiplier, repairItem, itemFlags, 
				entityHitDurabilityLoss, blockBreakDurabilityLoss, 
				throwDurabilityLoss, new ArrayList<>(), new ArrayList<>(), 
				new ArrayList<>(), new String[] {}, 
				new ReplaceCondition[] {}, ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}
	
	private static CustomItem loadTrident8(
			BitInput input, LoadIngredient loadIngredient
	) throws UnknownEncodingException {
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = loadIngredient.apply(input);
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
		return new CustomTrident(
				damage, name, "", displayName, lore, attributes, 
				defaultEnchantments, durability, allowEnchanting, allowAnvil, 
				throwDamageMultiplier, speedMultiplier, repairItem, itemFlags, 
				entityHitDurabilityLoss, blockBreakDurabilityLoss, 
				throwDurabilityLoss, playerEffects, targetEffects, 
				new ArrayList<>(), commands, 
				new ReplaceCondition[] {}, ConditionOperation.NONE,
				new ExtraItemNbt(), 1f
		);
	}
	
	private static CustomItem loadTrident9(
			BitInput input, LoadIngredient loadIngredient
	) throws UnknownEncodingException {
		short damage = input.readShort();
		String name = input.readJavaString();
		String alias = input.readString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
		long durability = input.readLong();
		boolean allowEnchanting = input.readBoolean();
		boolean allowAnvil = input.readBoolean();
		Ingredient repairItem = loadIngredient.apply(input);
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
		
		return new CustomTrident(
				damage, name, alias, displayName, lore, attributes, 
				defaultEnchantments, durability, allowEnchanting, allowAnvil, 
				throwDamageMultiplier, speedMultiplier, repairItem, itemFlags, 
				entityHitDurabilityLoss, blockBreakDurabilityLoss, 
				throwDurabilityLoss, playerEffects, targetEffects, 
				equippedEffects, commands, conditions, op, extraNbt, attackRange
		);
	}
	
	private static CustomItem loadWand8(
			BitInput input, ProjectileByName getProjectileByName
	) {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
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
		
		String projectileName = input.readString();
		int cooldown = input.readInt();
		WandCharges charges;
		if (input.readBoolean())
			charges = WandCharges.fromBits(input);
		else
			charges = null;
		int amountPerShot = input.readInt();
		
		CIProjectile projectile = getProjectileByName.apply(projectileName);
		
		return new CustomWand(
				itemType, damage, name, "", displayName, lore, attributes, 
				defaultEnchantments, itemFlags, playerEffects, targetEffects, 
				new ArrayList<>(), commands, new ReplaceCondition[] {}, 
				ConditionOperation.NONE, projectile, cooldown, charges,
				amountPerShot, new ExtraItemNbt(), 1f
		);
	}
	
	private static CustomItem loadWand9(
			BitInput input, ProjectileByName getProjectileByName
	) throws UnknownEncodingException {
		CustomItemType itemType = CustomItemType.valueOf(input.readJavaString());
		short damage = input.readShort();
		String name = input.readJavaString();
		String alias = input.readString();
		String displayName = input.readJavaString();
		String[] lore = new String[input.readByte() & 0xFF];
		for (int index = 0; index < lore.length; index++)
			lore[index] = input.readJavaString();
		AttributeModifier[] attributes = new AttributeModifier[input.readByte() & 0xFF];
		for (int index = 0; index < attributes.length; index++)
			attributes[index] = loadAttribute2(input);
		Enchantment[] defaultEnchantments = new Enchantment[input.readByte() & 0xFF];
		for (int index = 0; index < defaultEnchantments.length; index++)
			defaultEnchantments[index] = new Enchantment(EnchantmentType.valueOf(input.readString()), input.readInt());
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
		
		String projectileName = input.readString();
		int cooldown = input.readInt();
		WandCharges charges;
		if (input.readBoolean())
			charges = WandCharges.fromBits(input);
		else
			charges = null;
		int amountPerShot = input.readInt();
		
		CIProjectile projectile = getProjectileByName.apply(projectileName);
		ExtraItemNbt extraNbt = ExtraItemNbt.load(input);
		float attackRange = input.readFloat();
		
		return new CustomWand(
				itemType, damage, name, alias, displayName, lore, attributes, 
				defaultEnchantments, itemFlags, playerEffects, targetEffects, 
				equippedEffects, commands, conditions, op, projectile, cooldown, 
				charges, amountPerShot, extraNbt, attackRange
		);
	}

	private static AttributeModifier loadAttribute2(BitInput input) {
		return new AttributeModifier(Attribute.valueOf(input.readJavaString()), Slot.valueOf(input.readJavaString()),
				Operation.values()[(int) input.readNumber((byte) 2, false)], input.readDouble());
	}

	private void register(CustomItem item, int index) {
		items[index] = item;
	}

	private CustomRecipe loadRecipe(BitInput input) throws UnknownEncodingException, UnknownMaterialException {
		byte encoding = input.readByte();
		ItemStack result = loadResult(input);
		if (encoding == RecipeEncoding.SHAPED_RECIPE)
			return loadShapedRecipe(input, result);
		if (encoding == RecipeEncoding.SHAPELESS_RECIPE)
			return loadShapelessRecipe(input, result);
		throw new UnknownEncodingException("Recipe", encoding);
	}

	private CustomRecipe loadShapelessRecipe(BitInput input, ItemStack result) throws UnknownEncodingException {
		Ingredient[] ingredients = new Ingredient[(int) input.readNumber((byte) 4, false)];
		for (int index = 0; index < ingredients.length; index++)
			ingredients[index] = loadIngredient(input);
		return new ShapelessCustomRecipe(ingredients, result);
	}

	private CustomRecipe loadShapedRecipe(BitInput input, ItemStack result) throws UnknownEncodingException {
		Ingredient[] ingredients = new Ingredient[9];
		for (int index = 0; index < ingredients.length; index++)
			ingredients[index] = loadIngredient(input);
		return new ShapedCustomRecipe(result, ingredients);
	}

	@SuppressWarnings("deprecation")
	private ItemStack loadResult(BitInput input) throws UnknownEncodingException, UnknownMaterialException {
		byte encoding = input.readByte();
		byte amount = (byte) (1 + input.readNumber((byte) 6, false));
		if (encoding == RecipeEncoding.Result.VANILLA_SIMPLE)
			return ItemHelper.createStack(input.readJavaString(), amount);
		if (encoding == RecipeEncoding.Result.VANILLA_DATA) {
			ItemStack stack = ItemHelper.createStack(input.readJavaString(), amount);
			MaterialData data = stack.getData();
			data.setData((byte) input.readNumber((byte) 4, false));
			stack.setData(data);
			stack.setDurability(data.getData());
			return stack;
		}
		if (encoding == RecipeEncoding.Result.CUSTOM)
			return getItem(input.readJavaString()).create(amount);
		if (encoding == RecipeEncoding.Result.COPIED) {
			String encoded = input.readString();
			String serialized = StringEncoder.decode(encoded);
			
			YamlConfiguration helperConfig = new YamlConfiguration();
			try {
				helperConfig.loadFromString(serialized);
				ItemStack result = helperConfig.getItemStack("TheItemStack");
				if (result == null) {
					this.addError("A copied item stack result is invalid");
				} else {
					return result;
				}
			} catch (InvalidConfigurationException invalidConfig) {
				this.addError("A copied item stack result is invalid");
			}
			
			// I'm not sure how to handle this...
			return null;
		}
		throw new UnknownEncodingException("Result", encoding);
	}

	private Ingredient loadIngredient(BitInput input) throws UnknownEncodingException {
		byte encoding = input.readByte();
		if (encoding == RecipeEncoding.Ingredient.NONE)
			return new NoIngredient();
		if (encoding == RecipeEncoding.Ingredient.VANILLA_SIMPLE)
			return new SimpleVanillaIngredient(CIMaterial.valueOf(input.readJavaString()));
		if (encoding == RecipeEncoding.Ingredient.VANILLA_DATA)
			return new DataVanillaIngredient(CIMaterial.valueOf(input.readJavaString()),
					(byte) input.readNumber((byte) 4, false));
		if (encoding == RecipeEncoding.Ingredient.CUSTOM)
			return new CustomIngredient(getItem(input.readJavaString()));
		throw new UnknownEncodingException("Ingredient", encoding);
	}
	
	private static ReplaceCondition loadReplaceCondition(BitInput input) {
		return new ReplaceCondition(ReplacementCondition.valueOf(input.readJavaString()), input.readJavaString(), 
				ReplacementOperation.valueOf(input.readJavaString()), input.readInt(), input.readJavaString());
	}

	private void register(CustomRecipe recipe, int index) {
		recipes[index] = recipe;
	}
	
	private void register(BlockDrop drop) {
		BlockDrop[] old = blockDropMap[drop.getBlock().ordinal()];
		BlockDrop[] newDrops = Arrays.copyOf(old, old.length + 1);
		newDrops[old.length] = drop;
		blockDropMap[drop.getBlock().ordinal()] = newDrops;
	}
	
	private static final BlockDrop[] NO_BLOCK_DROPS = {};
	
	private static final Drop[] NO_DROPS = {};
	
	public BlockDrop[] getDrops(CIMaterial block) {
		if (block == null)
			return NO_BLOCK_DROPS;
		BlockType blockType = BlockType.fromBukkitMaterial(block);
		if (blockType != null) {
			return blockDropMap[blockType.ordinal()];
		} else {
			return NO_BLOCK_DROPS;
		}
	}
	
	private void register(EntityDrop drop) {
		EntityDrop[] old = mobDropMap[drop.getEntityType().ordinal()];
		EntityDrop[] newDrops = Arrays.copyOf(old, old.length + 1);
		newDrops[old.length] = drop;
		mobDropMap[drop.getEntityType().ordinal()] = newDrops;
	}
	
	public Drop[] getDrops(Entity entity) {
		CIEntityType entityType;
		if (entity instanceof Player) {
			Player player = (Player) entity;
			
			// The first check attempts to prevent the need for the possibly expensive second check
			if (player.hasMetadata("NPC") || !Bukkit.getOnlinePlayers().contains(player)) {
				entityType = CIEntityType.NPC;
			} else {
				entityType = CIEntityType.PLAYER;
			}
		} else {
			entityType = CIEntityType.fromBukkitEntityType(entity.getType());
		}
		
		if (entityType != null) {
			EntityDrop[] entityDrops = mobDropMap[entityType.ordinal()];
			int counter = 0;
			for (EntityDrop drop : entityDrops) {
				if (drop.getRequiredName() == null || drop.getRequiredName().equals(entity.getName())) {
					counter++;
				}
			}
			if (counter == 0) {
				return NO_DROPS;
			}
			
			Drop[] drops = new Drop[counter];
			counter = 0;
			for (EntityDrop drop : entityDrops) {
				if (drop.getRequiredName() == null || drop.getRequiredName().equals(entity.getName())) {
					drops[counter++] = drop.getDrop();
				}
			}
			
			return drops;
		} else {
			return NO_DROPS;
		}
	}

	public CustomRecipe[] getRecipes() {
		return recipes;
	}

	public CustomItem getItem(String name) {
		// TODO Perhaps improve performance
		for (CustomItem item : items)
			if (item.getName().equals(name))
				return item;
		return null;
	}
	
	public CustomItem getItem(ItemStack item) {
		if (ItemUtils.isEmpty(item)) {
			return null;
		}
		
		String[] pItemName = {null};
		CustomItemNBT.readOnly(item, nbt -> {
			if (nbt.hasOurNBT()) {
				pItemName[0] = nbt.getName();
			}
		});
		
		String itemName = pItemName[0];
		if (itemName == null) return null;
		
		return getCustomItemByName(itemName);
	}
	
	public ContainerInfo getContainerInfo(CustomContainer container) {
		return getContainerInfo(container.getName());
	}
	
	public ContainerInfo getContainerInfo(String containerName) {
		return containerInfo.get(containerName);
	}
	
	public Iterable<CustomContainer> getContainers() {
		return containers;
	}

	/**
	 * Don't modify this array, only read it!
	 * 
	 * @return The array containing all currently loaded custom items
	 */
	public CustomItem[] getBackingItems() {
		return items;
	}
	
	public int getNumItems() {
		return getBackingItems().length;
	}
	
	public int getNumRecipes() {
		return recipes.length;
	}
	
	public int getNumProjectiles() {
		return projectiles.length;
	}
	
	public int getNumContainers() {
		return containers.size();
	}

	@Override
	public CustomItem getCustomItemByName(String name) {
		return getItem(name);
	}

	@Override
	public CIProjectile getProjectileByName(String name) {
		for (CIProjectile projectile : projectiles)
			if (projectile.name.equals(name))
				return projectile;
		return null;
	}

	@Override
	public ProjectileCover getProjectileCoverByName(String name) {
		for (ProjectileCover cover : projectileCovers)
			if (cover.name.equals(name))
				return cover;
		return null;
	}
	
	public CustomFuelRegistry getFuelRegistryByName(String name) {
		for (CustomFuelRegistry fuelRegistry : fuelRegistries)
			if (fuelRegistry.getName().equals(name))
				return fuelRegistry;
		return null;
	}
	
	private long generateFakeExportTime() {
		/*
		 * Unfortunately, we don't know the real export time (when an older version
		 * of the editor was used). Luckily, the export time is mostly just a 
		 * unique identifier for the version of an item set.
		 * 
		 * When using this method, the chance of generating the same 'id' more than
		 * once is very small.
		 * 
		 * The primary drawback is that this will also differ each time the server
		 * is restarted, so it will cause some unnecessary work. Anyway, we only
		 * need this to support old versions of the editor, so users can just
		 * use a newer editor to avoid this.
		 */
		return (long) (-1_000_000_000_000_000L * Math.random());
	}
	
	public boolean isItemDeleted(String customItemName) {
		for (String deletedItem : deletedItems) {
			if (deletedItem.equals(customItemName)) {
				return true;
			}
		}
		
		return false;
	}
	
	@FunctionalInterface
	private static interface ThrowingFunction<Input,Output,Failure extends Exception> {
		
		Output apply(Input input) throws Failure;
	}
	
	@FunctionalInterface
	private static interface LoadIngredient extends 
			ThrowingFunction<BitInput, Ingredient, UnknownEncodingException> {}
	
	@FunctionalInterface
	private static interface ProjectileByName extends Function<String, CIProjectile> {}
}