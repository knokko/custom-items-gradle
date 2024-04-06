package nl.knokko.customitems.plugin.recipe;

import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.item.CustomToolValues;
import nl.knokko.customitems.nms.KciNms;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.multisupport.itembridge.ItemBridgeSupport;
import nl.knokko.customitems.plugin.multisupport.mimic.MimicSupport;
import nl.knokko.customitems.plugin.set.item.BukkitEnchantments;
import nl.knokko.customitems.plugin.set.item.CustomItemWrapper;
import nl.knokko.customitems.plugin.set.item.CustomToolWrapper;
import nl.knokko.customitems.plugin.tasks.updater.ItemUpgrader;
import nl.knokko.customitems.plugin.util.ItemUtils;
import nl.knokko.customitems.recipe.CraftingRecipeValues;
import nl.knokko.customitems.recipe.ShapedRecipeValues;
import nl.knokko.customitems.recipe.ShapelessRecipeValues;
import nl.knokko.customitems.recipe.ingredient.*;
import nl.knokko.customitems.recipe.ingredient.constraint.*;
import nl.knokko.customitems.recipe.result.*;
import nl.knokko.customitems.recipe.upgrade.UpgradeValues;
import nl.knokko.customitems.recipe.upgrade.VariableUpgradeValues;
import nl.knokko.customitems.util.StringEncoder;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.List;

import static nl.knokko.customitems.item.CustomItemValues.UNBREAKABLE_TOOL_DURABILITY;

public class RecipeHelper {

    public static CraftingRecipeWrapper wrap(CraftingRecipeValues recipe) {
        if (recipe instanceof ShapedRecipeValues) return new ShapedCraftingRecipeWrapper((ShapedRecipeValues) recipe);
        if (recipe instanceof ShapelessRecipeValues) return new ShapelessCraftingRecipeWrapper((ShapelessRecipeValues) recipe);
        throw new IllegalArgumentException("Unknown recipe class " + recipe.getClass());
    }

    public static boolean shouldIngredientAcceptItemStack(IngredientValues ingredient, ItemStack itemStack) {
        if (ingredient instanceof NoIngredientValues) return ItemUtils.isEmpty(itemStack);

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

        CustomItemValues customItem = CustomItemsPlugin.getInstance().getSet().getItem(item);
        if (customItem != null) {
            if (customItem instanceof CustomToolValues) {
                CustomToolValues customTool = (CustomToolValues) customItem;
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

    private static boolean doesItemStackSatisfyConstraints(IngredientConstraintsValues constraints, ItemStack item) {
        float durabilityPercentage = getDurabilityPercentage(item);

        for (DurabilityConstraintValues constraint : constraints.getDurabilityConstraints()) {
            if (!satisfiesFloatConstraint(durabilityPercentage, constraint.getOperator(), constraint.getPercentage())) return false;
        }

        for (EnchantmentConstraintValues constraint : constraints.getEnchantmentConstraints()) {
            int level = BukkitEnchantments.getLevel(item, constraint.getEnchantment());
            if (!satisfiesIntConstraint(level, constraint.getOperator(), constraint.getLevel())) return false;
        }

        if (!constraints.getVariableConstraints().isEmpty()) {
            List<UpgradeValues> upgrades = ItemUpgrader.getUpgrades(item, CustomItemsPlugin.getInstance().getSet());
            for (VariableConstraintValues constraint : constraints.getVariableConstraints()) {

                int itemValue = 0;
                for (UpgradeValues upgrade : upgrades) {
                    for (VariableUpgradeValues variable : upgrade.getVariables()) {
                        if (variable.getName().equals(constraint.getVariable())) itemValue += variable.getValue();
                    }
                }

                if (!satisfiesIntConstraint(itemValue, constraint.getOperator(), constraint.getValue())) return false;
            }
        }

        return true;
    }

    @SuppressWarnings("deprecation")
    public static boolean shouldIngredientAcceptAmountless(IngredientValues ingredient, ItemStack item) {
        if (ingredient instanceof NoIngredientValues) return ItemUtils.isEmpty(item);
        else if (ItemUtils.isEmpty(item)) return false;

        if (!doesItemStackSatisfyConstraints(ingredient.getConstraints(), item)) return false;

        if (ingredient instanceof SimpleVanillaIngredientValues) {
            CIMaterial type = ((SimpleVanillaIngredientValues) ingredient).getMaterial();
            if (type == CIMaterial.AIR) {
                return ItemUtils.isEmpty(item);
            } else {
                return !ItemUtils.isEmpty(item) && !ItemUtils.isCustom(item)
                        && KciNms.instance.items.getMaterialName(item).equals(type.name());
            }
        }

        if (ingredient instanceof DataVanillaIngredientValues) {
            DataVanillaIngredientValues dataIngredient = (DataVanillaIngredientValues) ingredient;
            if (dataIngredient.getMaterial() == CIMaterial.AIR) {
                return ItemUtils.isEmpty(item);
            } else {
                return !ItemUtils.isEmpty(item)
                        && !ItemUtils.isCustom(item)
                        && KciNms.instance.items.getMaterialName(item).equals(dataIngredient.getMaterial().name())
                        && item.getData().getData() == dataIngredient.getDataValue();
            }
        }

        if (ingredient instanceof CustomItemIngredientValues) {
            return CustomItemWrapper.wrap(((CustomItemIngredientValues) ingredient).getItem()).is(item);
        }

        if (ingredient instanceof MimicIngredientValues) {
            return MimicSupport.isItem(item, ((MimicIngredientValues) ingredient).getItemId());
        }

        if (ingredient instanceof ItemBridgeIngredientValues) {
            return ItemBridgeSupport.isItem(item, ((ItemBridgeIngredientValues) ingredient).getItemId());
        }

        if (ingredient instanceof CopiedIngredientValues) {
            String encoded = ((CopiedIngredientValues) ingredient).getEncodedItem();
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
    public static ItemStack convertResultToItemStack(ResultValues result) {
        if (result == null) return null;

        if (result instanceof SimpleVanillaResultValues) {
            SimpleVanillaResultValues simpleResult = (SimpleVanillaResultValues) result;
            return KciNms.instance.items.createStack(simpleResult.getMaterial().name(), simpleResult.getAmount());
        }

        if (result instanceof DataVanillaResultValues) {
            DataVanillaResultValues dataResult = (DataVanillaResultValues) result;
            ItemStack stack = KciNms.instance.items.createStack(dataResult.getMaterial().name(), dataResult.getAmount());
            MaterialData stackData = stack.getData();
            stackData.setData(dataResult.getDataValue());
            stack.setData(stackData);
            stack.setDurability(dataResult.getDataValue());
            return stack;
        }

        if (result instanceof CustomItemResultValues) {
            return CustomItemWrapper.wrap(((CustomItemResultValues) result).getItem()).create(((CustomItemResultValues) result).getAmount());
        }

        if (result instanceof CopiedResultValues) {
            String encoded = ((CopiedResultValues) result).getEncodedItem();
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

        if (result instanceof MimicResultValues) {
            MimicResultValues mimicResult = (MimicResultValues) result;
            return MimicSupport.fetchItem(mimicResult.getItemId(), mimicResult.getAmount());
        }

        if (result instanceof ItemBridgeResultValues) {
            ItemBridgeResultValues itemBridgeResult = (ItemBridgeResultValues) result;
            return ItemBridgeSupport.fetchItem(itemBridgeResult.getItemId(), itemBridgeResult.getAmount());
        }

        throw new IllegalArgumentException("Unknown result class: " + result.getClass());
    }
}
