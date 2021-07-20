package customitems.plugin.set.backward;

import nl.knokko.customitems.plugin.recipe.CustomRecipe;
import nl.knokko.customitems.plugin.recipe.ShapedCustomRecipe;
import nl.knokko.customitems.plugin.recipe.ShapelessCustomRecipe;
import nl.knokko.customitems.plugin.recipe.ingredient.*;
import nl.knokko.customitems.plugin.set.ItemSet;
import nl.knokko.util.bits.BitInputStream;
import org.bukkit.inventory.ItemStack;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BackwardHelper {

    public static ItemSet loadItemSet(String name) {
        InputStream rawInput = BackwardHelper.class.getClassLoader().getResourceAsStream(
                "backward/itemset/" + name + ".cis"
        );
        BitInputStream bitInput = new BitInputStream(new BufferedInputStream(rawInput));
        ItemSet result;
        try {
            result = new ItemSet(bitInput);
        } catch (Exception e) {
            throw new RuntimeException("Let the test fail", e);
        }
        bitInput.terminate();
        return result;
    }

    public static BufferedImage loadImage(String name) {
        try {
            InputStream input = BackwardHelper.class.getClassLoader().getResourceAsStream(
                    "backward/itemset/texture/" + name + ".png"
            );
            BufferedImage result = ImageIO.read(input);
            input.close();
            return result;
        } catch (IOException io) {
            throw new RuntimeException("Let the test fail", io);
        }
    }

    public static String[] stringArray(String...strings) {
        return strings;
    }

    public static boolean compareRecipes(CustomRecipe a, CustomRecipe b) {
        if (a.getClass() != b.getClass()) return false;
        if (a instanceof ShapedCustomRecipe) {
            ShapedCustomRecipe shapedA = (ShapedCustomRecipe) a;
            ShapedCustomRecipe shapedB = (ShapedCustomRecipe) b;

            if (shapedA.getIngredients().length != shapedB.getIngredients().length) return false;
            for (int index = 0; index < shapedA.getIngredients().length; index++) {
                if (!compareIngredient(shapedA.getIngredients()[index], shapedB.getIngredients()[index])) {
                    return false;
                }
            }

            return compareResult(shapedA.getResult(), shapedB.getResult());
        } else if (a instanceof ShapelessCustomRecipe) {
            ShapelessCustomRecipe shapelessA = (ShapelessCustomRecipe) a;
            ShapelessCustomRecipe shapelessB = (ShapelessCustomRecipe) b;

            if (shapelessA.getIngredients().length != shapelessB.getIngredients().length) return false;
            for (int index = 0; index < shapelessA.getIngredients().length; index++) {
                if (!compareIngredient(shapelessA.getIngredients()[index], shapelessB.getIngredients()[index])) {
                    return false;
                }
            }

            return compareResult(shapelessA.getResult(), shapelessB.getResult());
        } else {
            throw new Error("Unknown recipe type: " + a);
        }
    }

    public static boolean compareResult(ItemStack a, ItemStack b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }

    public static boolean compareIngredient(Ingredient a, Ingredient b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        if (
                a.getClass() != b.getClass() ||
                        a.getAmount() != b.getAmount() ||
                        !compareResult(a.getRemainingItem(), b.getRemainingItem())
        ) return false;

        if (a instanceof NoIngredient) return true;
        else if (a instanceof CustomIngredient) {
            CustomIngredient customA = (CustomIngredient) a;
            CustomIngredient customB = (CustomIngredient) b;
            return customA.getItem() == customB.getItem();
        } else if (a instanceof SimpleVanillaIngredient) {
            SimpleVanillaIngredient simpleA = (SimpleVanillaIngredient) a;
            SimpleVanillaIngredient simpleB = (SimpleVanillaIngredient) b;
            return simpleA.getType() == simpleB.getType();
        } else if (a instanceof DataVanillaIngredient) {
            DataVanillaIngredient dataA = (DataVanillaIngredient) a;
            DataVanillaIngredient dataB = (DataVanillaIngredient) b;
            return dataA.getData() == dataB.getData() && dataA.getType() == dataB.getType();
        } else {
            throw new IllegalArgumentException("Unknown type a: " + a);
        }
    }
}
