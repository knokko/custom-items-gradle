package nl.knokko.customitems.plugin.recipe;

import nl.knokko.customitems.item.VMaterial;
import nl.knokko.customitems.item.KciItem;
import nl.knokko.customitems.item.KciTool;
import nl.knokko.customitems.nms.KciNms;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.multisupport.itembridge.ItemBridgeSupport;
import nl.knokko.customitems.plugin.multisupport.mimic.MimicSupport;
import nl.knokko.customitems.plugin.set.item.BukkitEnchantments;
import nl.knokko.customitems.plugin.set.item.CustomItemWrapper;
import nl.knokko.customitems.plugin.set.item.CustomToolWrapper;
import nl.knokko.customitems.plugin.tasks.updater.ItemUpgrader;
import nl.knokko.customitems.plugin.util.ItemUtils;
import nl.knokko.customitems.recipe.KciCraftingRecipe;
import nl.knokko.customitems.recipe.KciShapedRecipe;
import nl.knokko.customitems.recipe.KciShapelessRecipe;
import nl.knokko.customitems.recipe.ingredient.*;
import nl.knokko.customitems.recipe.ingredient.constraint.*;
import nl.knokko.customitems.recipe.result.*;
import nl.knokko.customitems.recipe.upgrade.Upgrade;
import nl.knokko.customitems.recipe.upgrade.VariableUpgrade;
import nl.knokko.customitems.util.StringEncoder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.List;
import java.util.logging.Level;

import static nl.knokko.customitems.item.KciItem.UNBREAKABLE_TOOL_DURABILITY;

public class RecipeHelper {

    public static CraftingRecipeWrapper wrap(KciCraftingRecipe recipe) {
        if (recipe instanceof KciShapedRecipe) return new ShapedCraftingRecipeWrapper((KciShapedRecipe) recipe);
        if (recipe instanceof KciShapelessRecipe) return new ShapelessCraftingRecipeWrapper((KciShapelessRecipe) recipe);
        throw new IllegalArgumentException("Unknown recipe class " + recipe.getClass());
    }

    public static Material getMaterial(KciIngredient ingredient) {
        if (ingredient instanceof NoIngredient) return Material.AIR;

        if (ingredient instanceof SimpleVanillaIngredient) {
            return Material.valueOf(((SimpleVanillaIngredient) ingredient).getMaterial().name());
        }

        if (ingredient instanceof DataVanillaIngredient) {
            return Material.valueOf(((DataVanillaIngredient) ingredient).getMaterial().name());
        }

        if (ingredient instanceof CustomItemIngredient) {
            CustomItemIngredient custom = (CustomItemIngredient) ingredient;
            return Material.valueOf(custom.getVMaterial(KciNms.mcVersion).name());
        }

        if (ingredient instanceof MimicIngredient) {
            ItemStack item = MimicSupport.fetchItem(((MimicIngredient) ingredient).getItemId(), 1);
            return item != null ? item.getType() : Material.AIR;
        }

        if (ingredient instanceof ItemBridgeIngredient) {
            return ItemBridgeSupport.fetchItem(((ItemBridgeIngredient) ingredient).getItemId(), 1).getType();
        }

        if (ingredient instanceof CopiedIngredient) {
            String encoded = ((CopiedIngredient) ingredient).getEncodedItem();
            String serialized = StringEncoder.decode(encoded);

            YamlConfiguration helperConfig = new YamlConfiguration();
            try {
                helperConfig.loadFromString(serialized);
                ItemStack item = helperConfig.getItemStack("TheItemStack");
                return item != null ? item.getType() : Material.AIR;
            } catch (InvalidConfigurationException invalidConfig) {
                Bukkit.getLogger().warning("A copied item stack ingredient is invalid");
                return Material.AIR;
            }
        }

        throw new IllegalArgumentException("Unknown ingredient class: " + ingredient.getClass());
    }

    public static boolean shouldIngredientAcceptItemStack(KciIngredient ingredient, ItemStack itemStack) {
        if (ingredient instanceof NoIngredient) return ItemUtils.isEmpty(itemStack);

        if (ingredient.getRemainingItem() == null) {

            // If there is no remaining item, we can accept if the amount is large enough
            if (itemStack == null || itemStack.getAmount() >= ingredient.getAmount()) {
                return shouldIngredientAcceptAmountless(ingredient, itemStack);
            } else {
                return false;
            }
        } else {

            // If there is a remaining item, it must be consumed ENTIRELY to make space for the remaining item
            if (itemStack == null || itemStack.getAmount() == ingredient.getAmount()) {
                return shouldIngredientAcceptAmountless(ingredient, itemStack);
            } else {
                return false;
            }
        }
    }

    public static float getDurabilityPercentage(ItemStack item) {
        long currentDurability = 0;
        long maxDurability = 0;

        KciItem customItem = CustomItemsPlugin.getInstance().getSet().getItem(item);
        if (customItem != null) {
            if (customItem instanceof KciTool) {
                KciTool customTool = (KciTool) customItem;
                if (customTool.getMaxDurabilityNew() != null) {
                    currentDurability = CustomToolWrapper.wrap(customTool).getDurability(item);
                    if (currentDurability != UNBREAKABLE_TOOL_DURABILITY) maxDurability = customTool.getMaxDurabilityNew();
                }
            }
        } else if (item != null) {
            maxDurability = item.getType().getMaxDurability();
            if (maxDurability > 0) {
                ItemMeta meta = item.getItemMeta();
                if (KciNms.mcVersion >= 13) {
                    if (meta instanceof Damageable && !meta.isUnbreakable()) {
                        currentDurability = maxDurability - ((Damageable) meta).getDamage();
                    } else {
                        currentDurability = maxDurability;
                    }
                } else {
                    if (meta != null && meta.isUnbreakable()) currentDurability = maxDurability;
                    else currentDurability = maxDurability - item.getDurability();
                }
            }
        }

        if (maxDurability > 0) {
            return 100f * currentDurability / maxDurability;
        } else {
            return 100f;
        }
    }

    private static boolean satisfiesFloatConstraint(float value, ConstraintOperator operator, float referenceValue) {
        switch (operator) {
            case AT_LEAST: return value >= referenceValue;
            case GREATER_THAN: return value > referenceValue;
            case AT_MOST: return value <= referenceValue;
            case SMALLER_THAN: return value < referenceValue;
            // Note that EQUAL is not allowed for float constraints
            default: throw new UnsupportedOperationException("Unexpected operator " + operator);
        }
    }

    private static boolean satisfiesIntConstraint(int value, ConstraintOperator operator, int referenceValue) {
        switch (operator) {
            case AT_LEAST: return value >= referenceValue;
            case GREATER_THAN: return value > referenceValue;
            case EQUAL: return value == referenceValue;
            case AT_MOST: return value <= referenceValue;
            case SMALLER_THAN: return value < referenceValue;
            default: throw new UnsupportedOperationException("Unexpected operator " + operator);
        }
    }

    private static boolean doesItemStackSatisfyConstraints(IngredientConstraints constraints, ItemStack item) {
        float durabilityPercentage = getDurabilityPercentage(item);

        for (DurabilityConstraint constraint : constraints.getDurabilityConstraints()) {
            if (!satisfiesFloatConstraint(durabilityPercentage, constraint.getOperator(), constraint.getPercentage())) return false;
        }

        for (EnchantmentConstraint constraint : constraints.getEnchantmentConstraints()) {
            int level = BukkitEnchantments.getLevel(item, constraint.getEnchantment());
            if (!satisfiesIntConstraint(level, constraint.getOperator(), constraint.getLevel())) return false;
        }

        if (!constraints.getVariableConstraints().isEmpty()) {
            List<Upgrade> upgrades = ItemUpgrader.getUpgrades(item, CustomItemsPlugin.getInstance().getSet());
            for (VariableConstraint constraint : constraints.getVariableConstraints()) {

                int itemValue = 0;
                for (Upgrade upgrade : upgrades) {
                    for (VariableUpgrade variable : upgrade.getVariables()) {
                        if (variable.getName().equals(constraint.getVariable())) itemValue += variable.getValue();
                    }
                }

                if (!satisfiesIntConstraint(itemValue, constraint.getOperator(), constraint.getValue())) return false;
            }
        }

        return true;
    }

    @SuppressWarnings("deprecation")
    public static boolean shouldIngredientAcceptAmountless(KciIngredient ingredient, ItemStack item) {
        if (ingredient instanceof NoIngredient) return ItemUtils.isEmpty(item);
        else if (ItemUtils.isEmpty(item)) return false;

        if (!doesItemStackSatisfyConstraints(ingredient.getConstraints(), item)) return false;

        if (ingredient instanceof SimpleVanillaIngredient) {
            VMaterial type = ((SimpleVanillaIngredient) ingredient).getMaterial();
            if (type == VMaterial.AIR) {
                return ItemUtils.isEmpty(item);
            } else {
                return !ItemUtils.isEmpty(item) && !ItemUtils.isCustom(item)
                        && KciNms.instance.items.getMaterialName(item).equals(type.name());
            }
        }

        if (ingredient instanceof DataVanillaIngredient) {
            DataVanillaIngredient dataIngredient = (DataVanillaIngredient) ingredient;
            if (dataIngredient.getMaterial() == VMaterial.AIR) {
                return ItemUtils.isEmpty(item);
            } else {
                return !ItemUtils.isEmpty(item)
                        && !ItemUtils.isCustom(item)
                        && KciNms.instance.items.getMaterialName(item).equals(dataIngredient.getMaterial().name())
                        && item.getData().getData() == dataIngredient.getDataValue();
            }
        }

        if (ingredient instanceof CustomItemIngredient) {
            return CustomItemWrapper.wrap(((CustomItemIngredient) ingredient).getItem()).is(item);
        }

        if (ingredient instanceof MimicIngredient) {
            return MimicSupport.isItem(item, ((MimicIngredient) ingredient).getItemId());
        }

        if (ingredient instanceof ItemBridgeIngredient) {
            return ItemBridgeSupport.isItem(item, ((ItemBridgeIngredient) ingredient).getItemId());
        }

        if (ingredient instanceof CopiedIngredient) {
            String encoded = ((CopiedIngredient) ingredient).getEncodedItem();
            String serialized = StringEncoder.decode(encoded);

            YamlConfiguration helperConfig = new YamlConfiguration();
            try {
                helperConfig.loadFromString(serialized);
                ItemStack desiredIngredient = helperConfig.getItemStack("TheItemStack");
                return item.isSimilar(desiredIngredient);
            } catch (InvalidConfigurationException invalidConfig) {
                Bukkit.getLogger().warning("A copied item stack ingredient is invalid");
                return false;
            }
        }

        throw new IllegalArgumentException("Unknown ingredient class: " + ingredient.getClass());
    }

    @SuppressWarnings("deprecation")
    public static ItemStack convertResultToItemStack(KciResult result) {
        if (result == null) return null;

        if (result instanceof SimpleVanillaResult) {
            SimpleVanillaResult simpleResult = (SimpleVanillaResult) result;
            return KciNms.instance.items.createStack(simpleResult.getMaterial().name(), simpleResult.getAmount());
        }

        if (result instanceof DataVanillaResult) {
            DataVanillaResult dataResult = (DataVanillaResult) result;
            ItemStack stack = KciNms.instance.items.createStack(dataResult.getMaterial().name(), dataResult.getAmount());
            MaterialData stackData = stack.getData();
            stackData.setData(dataResult.getDataValue());
            stack.setData(stackData);
            stack.setDurability(dataResult.getDataValue());
            return stack;
        }

        if (result instanceof CustomItemResult) {
            return CustomItemWrapper.wrap(((CustomItemResult) result).getItem()).create(((CustomItemResult) result).getAmount());
        }

        if (result instanceof CopiedResult) {
            String encoded = ((CopiedResult) result).getEncodedItem();
            String serialized = StringEncoder.decode(encoded);

            YamlConfiguration helperConfig = new YamlConfiguration();
            try {
                helperConfig.loadFromString(serialized);
                return helperConfig.getItemStack("TheItemStack");
            } catch (InvalidConfigurationException invalidConfig) {
                Bukkit.getLogger().warning("A copied item stack result is invalid");
                return null;
            }
        }

        if (result instanceof MimicResult) {
            MimicResult mimicResult = (MimicResult) result;
            ItemStack mimicItem = MimicSupport.fetchItem(mimicResult.getItemId(), mimicResult.getAmount());
            if (ItemUtils.isEmpty(mimicItem)) {
                Bukkit.getLogger().log(Level.SEVERE, "Failed to find Mimic item " + mimicResult.getItemId());
                return new ItemStack(Material.STONE);
            }
            return mimicItem;
        }

        if (result instanceof ItemBridgeResult) {
            ItemBridgeResult itemBridgeResult = (ItemBridgeResult) result;
            ItemStack itemBridgeItem = ItemBridgeSupport.fetchItem(itemBridgeResult.getItemId(), itemBridgeResult.getAmount());
            if (ItemUtils.isEmpty(itemBridgeItem)) {
                Bukkit.getLogger().log(Level.SEVERE, "Failed to find ItemBridge item " + itemBridgeResult.getItemId());
                return new ItemStack(Material.STONE);
            }
            return itemBridgeItem;
        }

        throw new IllegalArgumentException("Unknown result class: " + result.getClass());
    }
}
