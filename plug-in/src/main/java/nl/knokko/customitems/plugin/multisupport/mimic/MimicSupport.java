package nl.knokko.customitems.plugin.multisupport.mimic;

import nl.knokko.customitems.plugin.CustomItemsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MimicSupport {

    private static final String TEST_CLASS = "ru.endlesscode.mimic.items.BukkitItemsRegistry";

    public static void onLoad(CustomItemsPlugin plugin) {
        try {
            Class.forName(TEST_CLASS);
            MimicItemRegistry.load(plugin);
        } catch (ClassNotFoundException noMimic) {
            Bukkit.getLogger().info("Disabled OPTIONAL Mimic integration: can't find " + TEST_CLASS);
        }
    }

    public static ItemStack fetchItem(String id, int amount) {
        try {
            Class.forName(TEST_CLASS);
            return MimicItemRegistry.fetchMimicItem(id, amount);
        } catch (ClassNotFoundException noMimic) {
            Bukkit.getLogger().severe("Can't get Mimic item " + id + " because Mimic is not installed");
            return new ItemStack(Material.AIR);
        }
    }

    public static boolean isItem(ItemStack candidate, String id) {
        try {
            Class.forName(TEST_CLASS);
            return MimicItemRegistry.isMimicItem(candidate, id);
        } catch (ClassNotFoundException noMimic) {
            Bukkit.getLogger().severe("Can't check Mimic item " + id + " because Mimic is not installed");
            return false;
        }
    }
}
