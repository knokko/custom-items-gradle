package nl.knokko.customrecipes.crafting;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.inventory.PlayerInventoryViewMock;
import nl.knokko.customrecipes.ingredient.CustomIngredient;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static nl.knokko.customrecipes.crafting.TestShapedRecipes.checkResult;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCraftingRecipes {

    @Test
    public void testConflictBetweenShapedAndShapelessRecipes() {
        ServerMock server = Objects.requireNonNull(MockBukkit.getOrCreateMock());
        JavaPlugin plugin = MockBukkit.createMockPlugin();
        Player player = server.addPlayer();

        CustomShapedRecipe customShaped = new CustomShapedRecipe(new ItemStack(Material.GOLD_INGOT), "a");
        customShaped.ingredientMap.put('a', new CustomIngredient(
                Material.COAL, coal -> coal.containsEnchantment(Enchantment.DAMAGE_UNDEAD)
        ));
        CustomShapelessRecipe customShapeless = new CustomShapelessRecipe(new ItemStack(Material.DIAMOND), new CustomIngredient(
                Material.COAL, coal -> coal.containsEnchantment(Enchantment.DAMAGE_ALL)
        ));

        CustomCraftingRecipes craftingRecipes = new CustomCraftingRecipes();
        craftingRecipes.add(customShaped);
        craftingRecipes.add(customShapeless);

        Set<NamespacedKey> customKeys = new HashSet<>();
        craftingRecipes.register(plugin, customKeys);
        assertEquals(2, customKeys.size());

        NamespacedKey shapelessKey = customKeys.stream().filter(
                key -> key.getKey().startsWith("weak-shapeless")
        ).findFirst().get();

        ShapelessRecipe bukkitRecipe = new ShapelessRecipe(shapelessKey, new ItemStack(Material.DIAMOND));

        ItemStack[] smiteMatrix = new ItemStack[9];
        smiteMatrix[3] = new ItemStack(Material.COAL);
        smiteMatrix[3].addUnsafeEnchantment(Enchantment.DAMAGE_UNDEAD, 1);

        ItemStack[] sharpMatrix = new ItemStack[9];
        sharpMatrix[3] = new ItemStack(Material.COAL);
        sharpMatrix[3].addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);

        CraftingInventoryMock inventory = new CraftingInventoryMock(player);
        InventoryView view = new PlayerInventoryViewMock(player, inventory);

        // Test shapeless sharpness recipe
        checkResult(inventory, view, sharpMatrix, bukkitRecipe, new ItemStack(Material.DIAMOND), null);

        // Test shaped conflicted smite recipe
        checkResult(inventory, view, smiteMatrix, bukkitRecipe, new ItemStack(Material.GOLD_INGOT), null);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }
}
