package nl.knokko.customitems.plugin.recipe;

import nl.knokko.core.plugin.item.ItemHelper;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.plugin.set.item.CustomItemWrapper;
import nl.knokko.customitems.plugin.util.ItemUtils;
import nl.knokko.customitems.recipe.CraftingRecipeValues;
import nl.knokko.customitems.recipe.ShapedRecipeValues;
import nl.knokko.customitems.recipe.ShapelessRecipeValues;
import nl.knokko.customitems.recipe.ingredient.*;
import nl.knokko.customitems.recipe.result.*;
import nl.knokko.customitems.util.StringEncoder;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class RecipeHelper {

    public static CraftingRecipeWrapper wrap(CraftingRecipeValues recipe) {
        if (recipe instanceof ShapedRecipeValues) return new ShapedCraftingRecipeWrapper((ShapedRecipeValues) recipe);
        if (recipe instanceof ShapelessRecipeValues) return new ShapelessCraftingRecipeWrapper((ShapelessRecipeValues) recipe);
        throw new IllegalArgumentException("Unknown recipe class " + recipe.getClass());
    }

    public static boolean shouldIngredientAcceptItemStack(IngredientValues ingredient, ItemStack itemStack) {
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

    @SuppressWarnings("deprecation")
    public static boolean shouldIngredientAcceptAmountless(IngredientValues ingredient, ItemStack item) {
        if (ingredient instanceof NoIngredientValues) return ItemUtils.isEmpty(item);

        if (ingredient instanceof SimpleVanillaIngredientValues) {
            CIMaterial type = ((SimpleVanillaIngredientValues) ingredient).getMaterial();
            if (type == CIMaterial.AIR) {
                return ItemUtils.isEmpty(item);
            } else {
                return !ItemUtils.isEmpty(item) && !ItemUtils.isCustom(item)
                        && ItemHelper.getMaterialName(item).equals(type.name());
            }
        }

        if (ingredient instanceof DataVanillaIngredientValues) {
            DataVanillaIngredientValues dataIngredient = (DataVanillaIngredientValues) ingredient;
            if (dataIngredient.getMaterial() == CIMaterial.AIR) {
                return ItemUtils.isEmpty(item);
            } else {
                return !ItemUtils.isEmpty(item)
                        && !ItemUtils.isCustom(item)
                        && ItemHelper.getMaterialName(item).equals(dataIngredient.getMaterial().name())
                        && item.getData().getData() == dataIngredient.getDataValue();
            }
        }

        if (ingredient instanceof CustomItemIngredientValues) {
            return CustomItemWrapper.wrap(((CustomItemIngredientValues) ingredient).getItem()).is(item);
        }

        throw new IllegalArgumentException("Unknown ingredient class: " + ingredient.getClass());
    }

    @SuppressWarnings("deprecation")
    public static ItemStack convertResultToItemStack(ResultValues result) {
        if (result instanceof SimpleVanillaResultValues) {
            SimpleVanillaResultValues simpleResult = (SimpleVanillaResultValues) result;
            return ItemHelper.createStack(simpleResult.getMaterial().name(), simpleResult.getAmount());
        }

        if (result instanceof DataVanillaResultValues) {
            DataVanillaResultValues dataResult = (DataVanillaResultValues) result;
            ItemStack stack = ItemHelper.createStack(dataResult.getMaterial().name(), dataResult.getAmount());
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

        throw new IllegalArgumentException("Unknown result class: " + result.getClass());
    }
}
