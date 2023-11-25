package nl.knokko.customitems.plugin.multisupport.magic;

import com.elmakers.mine.bukkit.api.magic.MagicAPI;
import org.bukkit.Bukkit;

public class MagicSupport {

    public static final MagicAPI MAGIC;

    static {
        Object magicPlugin = Bukkit.getPluginManager().getPlugin("Magic");
        if (magicPlugin instanceof MagicAPI) {
            MAGIC = (MagicAPI) magicPlugin;
            Bukkit.getLogger().info("Enabled Magic integration");
        } else {
            MAGIC = null;
            Bukkit.getLogger().info("Disabled OPTIONAL Magic integration: can't find plug-in Magic");
        }
    }
}
