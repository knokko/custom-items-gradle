package nl.knokko.customitems.plugin.multisupport.denizen;

import com.denizenscript.denizen.objects.InventoryTag;
import com.denizenscript.denizen.objects.ItemTag;
import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizencore.DenizenCore;
import com.denizenscript.denizencore.objects.core.ElementTag;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.plugin.CustomItemsApi;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

class CIDenizenAddon {

    static void enable() {
        // <--[tag]
        // @attribute <ItemTag.is_kci_item>
        // @returns ElementTag(Boolean)
        // @description
        // Returns true if and only if the given item is a KCI custom item.
        // -->
        ItemTag.tagProcessor.registerTag(ElementTag.class, "is_kci_item", (attribute, object) -> {
            return new ElementTag(CustomItemsApi.getItemName(object.getItemStack()) != null);
        });

        // <--[tag]
        // @attribute <ItemTag.kci_item_name>
        // @returns ElementTag(String)
        // @description
        // Returns the name of the KCI custom item to which this item belongs, or null if this item is
        // not a KCI custom item.
        // -->
        ItemTag.tagProcessor.registerTag(ElementTag.class, "kci_item_name", (attribute, object) -> {
            return new ElementTag(CustomItemsApi.getItemName(object.getItemStack()));
        });

        // <--[tag]
        // @attribute <LocationTag.is_kci_block>
        // @returns ElementTag(Boolean)
        // @description
        // Returns true if and only if there is a custom KCI block at the given location.
        // -->
        LocationTag.tagProcessor.registerTag(ElementTag.class, "is_kci_block", (attribute, object) -> {
            return new ElementTag(CustomItemsApi.getBlockName(object.getBlock()) != null);
        });

        // <--[tag]
        // @attribute <LocationTag.kci_block_name>
        // @returns ElementTag(String)
        // @description
        // Returns the name of the KCI custom block at this location, or null if there is no KCI custom
        // block at this location.
        // -->
        LocationTag.tagProcessor.registerTag(ElementTag.class, "kci_block_name", (attribute, object) -> {
            return new ElementTag(CustomItemsApi.getBlockName(object.getBlock()));
        });

        InventoryTag.tagProcessor.registerTag(ElementTag.class, "kci_count", (attribute, object) -> {
            if (!attribute.hasParam()) {
                attribute.echoError("The kci_count[...] tag must have an input");
                return null;
            }
            String itemName = attribute.getParam();
            ItemStack[] contents = object.getContents();

            int count = 0;
            ItemSetWrapper itemSet = CustomItemsPlugin.getInstance().getSet();
            for (ItemStack content : contents) {
                CustomItemValues customItem = itemSet.getItem(content);
                if (customItem != null && customItem.getName().equals(itemName)) {
                    count += content.getAmount();
                }
            }
            return new ElementTag(count);
        });

        DenizenCore.commandRegistry.registerCommand(PlaceKciBlockCommand.class);
        DenizenCore.commandRegistry.registerCommand(KciContainerCommand.class);
        DenizenCore.commandRegistry.registerCommand(GiveKciItemCommand.class);
        DenizenCore.commandRegistry.registerCommand(KciRemoveItemCommand.class);
        DenizenCore.commandRegistry.registerCommand(LaunchKciProjectileCommand.class);
        DenizenCore.commandRegistry.registerCommand(DropKciItemCommand.class);
        new KciItemsTag();

        Bukkit.getPluginManager().registerEvents(new KciContainerActionEvent(), CustomItemsPlugin.getInstance());
        Bukkit.getPluginManager().registerEvents(new KciFoodEatEvent(), CustomItemsPlugin.getInstance());
    }
}
