package nl.knokko.customitems.editor.unittest.itemset;

import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.recipe.Recipe;
import nl.knokko.customitems.editor.set.recipe.ShapedRecipe;
import nl.knokko.customitems.editor.set.recipe.ShapelessRecipe;
import nl.knokko.customitems.editor.set.recipe.ingredient.*;
import nl.knokko.customitems.editor.set.recipe.result.CustomItemResult;
import nl.knokko.customitems.editor.set.recipe.result.DataVanillaResult;
import nl.knokko.customitems.editor.set.recipe.result.Result;
import nl.knokko.customitems.editor.set.recipe.result.SimpleVanillaResult;
import nl.knokko.customitems.texture.NamedImage;
import nl.knokko.util.bits.BitInputStream;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

public class BackwardHelper {

    public static ItemSet loadItemSet(String name) {
        InputStream rawInput = BackwardHelper.class.getClassLoader().getResourceAsStream(
                "backward/itemset/" + name + ".cisb"
        );
        BitInputStream bitInput = new BitInputStream(new BufferedInputStream(rawInput));
        ItemSet result;
        try {
            result = new ItemSet(name, bitInput);
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

    public static void checkTexture(ItemSet itemSet, String expectedName) {
        NamedImage texture = itemSet.getTextureByName(expectedName);
        BufferedImage expectedImage = loadImage(texture.getName());
        BufferedImage actualImage = texture.getImage();

        assertEquals(expectedImage.getWidth(), actualImage.getWidth());
        assertEquals(expectedImage.getHeight(), actualImage.getHeight());
        for (int x = 0; x < expectedImage.getWidth(); x++) {
            for (int y = 0; y < expectedImage.getHeight(); y++) {
                assertEquals(expectedImage.getRGB(x, y), actualImage.getRGB(x, y));
            }
        }
    }

    public static String[] stringArray(String...strings) {
        return strings;
    }

    public static boolean compareRecipes(Recipe a, Recipe b) {
        if (a.getClass() != b.getClass()) return false;
        if (a instanceof ShapedRecipe) {
            ShapedRecipe shapedA = (ShapedRecipe) a;
            ShapedRecipe shapedB = (ShapedRecipe) b;

            if (shapedA.getIngredients().length != shapedB.getIngredients().length) return false;
            for (int index = 0; index < shapedA.getIngredients().length; index++) {
                if (!compareIngredient(shapedA.getIngredients()[index], shapedB.getIngredients()[index])) {
                    return false;
                }
            }

            return compareResult(shapedA.getResult(), shapedB.getResult());
        } else if (a instanceof ShapelessRecipe) {
            ShapelessRecipe shapelessA = (ShapelessRecipe) a;
            ShapelessRecipe shapelessB = (ShapelessRecipe) b;

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

    public static boolean compareResult(Result a, Result b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        if (a.getClass() != b.getClass() || a.getAmount() != b.getAmount()) return false;
        if (a instanceof CustomItemResult) {
            CustomItemResult customA = (CustomItemResult) a;
            CustomItemResult customB = (CustomItemResult) b;
            return customA.getItem() == customB.getItem();
        } else if (a instanceof SimpleVanillaResult) {
            SimpleVanillaResult simpleA = (SimpleVanillaResult) a;
            SimpleVanillaResult simpleB = (SimpleVanillaResult) b;
            return simpleA.getType() == simpleB.getType();
        } else if (a instanceof DataVanillaResult) {
            DataVanillaResult dataA = (DataVanillaResult) a;
            DataVanillaResult dataB = (DataVanillaResult) b;
            return dataA.getData() == dataB.getData() && dataA.getType() == dataB.getType();
        } else {
            throw new IllegalArgumentException("Unexpected type a: " + a);
        }
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
        else if (a instanceof CustomItemIngredient) {
            CustomItemIngredient customA = (CustomItemIngredient) a;
            CustomItemIngredient customB = (CustomItemIngredient) b;
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
