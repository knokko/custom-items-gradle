package nl.knokko.customitems.nms20;

import kr.toxicity.libraries.datacomponent.DataComponentAPIBukkit;
import kr.toxicity.libraries.datacomponent.api.DataComponentAPI;
import kr.toxicity.libraries.datacomponent.api.ItemAdapter;
import kr.toxicity.libraries.datacomponent.api.NMS;
import kr.toxicity.libraries.datacomponent.api.wrapper.ItemLore;
import net.kyori.adventure.text.Component;
import nl.knokko.customitems.nms18plus.KciNmsItems18Plus;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R4.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class KciNmsItems20 extends KciNmsItems18Plus {

    private static final boolean HAS_PAPER;
    private static final boolean DATA_COMPONENTS_API_SUPPORTS_VERSION;

    static {
        boolean foundPaper;
        try {
            Class.forName("io.papermc.paper.adventure.PaperAdventure");
            foundPaper = true;
        } catch (ClassNotFoundException noPaper) {
            foundPaper = false;
        }
        HAS_PAPER = foundPaper;

        boolean dataComponentApiSupportsVersion = false;
        if (HAS_PAPER) {
            try {
                DataComponentAPIBukkit.load();
                dataComponentApiSupportsVersion = true;
            } catch (UnsupportedOperationException versionNotSupported) {
                Bukkit.getLogger().log(
                        Level.WARNING, "It looks like the DataComponentsAPI version bundled with CustomItems " +
                                "doesn't support this minecraft version", versionNotSupported
                );
            }
        }

        DATA_COMPONENTS_API_SUPPORTS_VERSION = dataComponentApiSupportsVersion;
    }

    @Override
    public String getStackName(ItemStack stack) {
        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        return nms.x().getString();
    }

    @Override
    public ItemStack translate(ItemStack item, String itemName, boolean translateDisplayName, int loreSize) {
        if (!HAS_PAPER) {
            Bukkit.getLogger().warning("Translations in MC 1.20+ require PaperMC");
            return item;
        }

        if (!DATA_COMPONENTS_API_SUPPORTS_VERSION) {
            Bukkit.getLogger().warning("The bundled DCAPI version doesn't support this minecraft version");
            return item;
        }

        ItemAdapter dataComponents = DataComponentAPI.api().adapter(item);
        if (translateDisplayName) {
            dataComponents.set(NMS.nms().customName(), Component.translatable("kci." + itemName + ".name"));
        }
        if (loreSize > 0) {
            List<Component> loreComponents = new ArrayList<>(loreSize);
            for (int index = 0; index < loreSize; index++) {
                loreComponents.add(Component.translatable("kci." + itemName + ".lore." + index));
            }
            dataComponents.set(NMS.nms().lore(), new ItemLore(loreComponents, loreComponents));
        }
        return dataComponents.build();
    }
}
