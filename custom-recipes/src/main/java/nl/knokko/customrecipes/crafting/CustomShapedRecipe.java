package nl.knokko.customrecipes.crafting;

import nl.knokko.customrecipes.ingredient.CustomIngredient;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public class CustomShapedRecipe {

    public final Function<ItemStack[], ItemStack> result;
    final String[] shape;
    public final Map<Character, CustomIngredient> ingredientMap = new HashMap<>();
    public final Predicate<HumanEntity> canCraft;

    final int offsetX, offsetY, width, height;

    public CustomShapedRecipe(Function<ItemStack[], ItemStack> result, Predicate<HumanEntity> canCraft, String... shape) {
        this.result = result;
        this.canCraft = canCraft;

        int offsetX = 0;
        loopOffsetX:
        while (offsetX < 3) {
            for (String s : shape) {
                if (s.charAt(offsetX) != ' ') break loopOffsetX;
            }
            offsetX += 1;
        }
        this.offsetX = offsetX;

        int offsetY = 0;
        while (offsetY < 3 && shape[offsetY].trim().isEmpty()) offsetY += 1;
        this.offsetY = offsetY;

        int endX = shape[0].length() - 1;
        loopEndX:
        while (endX >= offsetX) {
            for (String s : shape) {
                if (s.charAt(endX) != ' ') break loopEndX;
            }
            endX -= 1;
        }

        int endY = shape.length - 1;
        while (endY >= offsetY && shape[endY].trim().isEmpty()) endY -= 1;

        this.shape = new String[1 + endY - offsetY];
        for (int y = 0; y < this.shape.length; y++) {
            this.shape[y] = shape[y + offsetY].substring(offsetX, endX + 1);
        }

        this.width = shape[0].length() - offsetX;
        this.height = shape.length - offsetY;
    }

    public CustomShapedRecipe(ItemStack result, String... shape) {
        this(ingredients -> result, crafter -> true, shape);
    }
}
