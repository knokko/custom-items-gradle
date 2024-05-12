package nl.knokko.customrecipes;

import nl.knokko.customrecipes.cooking.CustomCookingManager;
import nl.knokko.customrecipes.crafting.CustomCraftingRecipes;
import nl.knokko.customrecipes.smithing.CustomSmithingRecipes;
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
    public final CustomCookingManager cooking = new CustomCookingManager();
    public final CustomSmithingRecipes smithing = new CustomSmithingRecipes();

    private Set<NamespacedKey> keys;

    public CustomRecipes(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private void removeRecipes() {
        if (keys != null) {

            boolean canRemoveRecipes;
            try {
                Bukkit.class.getMethod("removeRecipe", NamespacedKey.class);
                canRemoveRecipes = true;
            } catch (NoSuchMethodException cantRemove) {
                canRemoveRecipes = false;
            }

            Iterator<Recipe> iterator = Bukkit.recipeIterator();
            while (iterator.hasNext()) {
                Recipe next = iterator.next();
                if (next instanceof Keyed && keys.contains(((Keyed) next).getKey())) {
                    NamespacedKey key = ((Keyed) next).getKey();
                    if (canRemoveRecipes) {
                        Bukkit.removeRecipe(key);
                    } else {
                        try {
                            iterator.remove();
                        } catch (UnsupportedOperationException stupidOldMinecraftVersion) {
                            Bukkit.resetRecipes();
                            break;
                        }
                    }
                }
            }
            keys = null;
        }
    }

    public void reset() {
        removeRecipes();
        crafting.clear();
        cooking.clear();
        smithing.clear();
    }

    public void register() {
        removeRecipes();
        keys = new HashSet<>();

        crafting.register(plugin, keys);
        cooking.register(plugin, keys);
        smithing.register(plugin, keys);
    }
}
