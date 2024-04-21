package nl.knokko.customrecipes;

import nl.knokko.customrecipes.crafting.CustomCraftingRecipes;
import nl.knokko.customrecipes.furnace.CustomFurnaceRecipes;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class CustomRecipes implements Listener {

    private final JavaPlugin plugin;
    public final CustomCraftingRecipes crafting = new CustomCraftingRecipes();
    public final CustomFurnaceRecipes furnace = new CustomFurnaceRecipes();

    private Set<NamespacedKey> keys;

    public CustomRecipes(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private void removeRecipes() {
        if (keys != null) {
            Iterator<Recipe> iterator = Bukkit.recipeIterator();
            while (iterator.hasNext()) {
                Recipe next = iterator.next();
                if (next instanceof Keyed && keys.contains(((Keyed) next).getKey())) iterator.remove();
            }
            keys = null;
        }
    }

    public void reset() {
        removeRecipes();
        crafting.clear();
        furnace.clear();
    }

    public void register() {
        removeRecipes();
        keys = new HashSet<>();

        crafting.register(plugin, keys);
        furnace.register(plugin, keys);
    }
}
