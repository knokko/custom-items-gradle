package nl.knokko.customitems.nms20plus;

import kr.toxicity.libraries.datacomponent.DataComponentAPIBukkit;
import kr.toxicity.libraries.datacomponent.api.DataComponentAPI;
import kr.toxicity.libraries.datacomponent.api.ItemAdapter;
import kr.toxicity.libraries.datacomponent.api.NMS;
import kr.toxicity.libraries.datacomponent.api.wrapper.ItemLore;
import net.kyori.adventure.text.Component;
import nl.knokko.customitems.nms18plus.KciNmsItems18Plus;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class KciNmsItems20Plus extends KciNmsItems18Plus {

    private static final boolean HAS_PAPER;

    static {
        boolean foundPaper;
        try {
            Class.forName("io.papermc.paper.adventure.PaperAdventure");
            foundPaper = true;
        } catch (ClassNotFoundException noPaper) {
            foundPaper = false;
        }
        HAS_PAPER = foundPaper;

        if (HAS_PAPER) DataComponentAPIBukkit.load();
    }

    @Override
    public ItemStack translate(ItemStack item, String itemName, boolean translateDisplayName, int loreSize) {
        if (!HAS_PAPER) {
            Bukkit.getLogger().warning("Translations in MC 1.20+ require PaperMC");
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
