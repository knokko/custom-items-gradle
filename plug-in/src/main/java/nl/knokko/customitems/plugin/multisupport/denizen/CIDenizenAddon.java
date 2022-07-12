package nl.knokko.customitems.plugin.multisupport.denizen;

import com.denizenscript.denizen.objects.ItemTag;
import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizen.scripts.commands.BukkitCommandRegistry;
import com.denizenscript.denizencore.objects.core.ElementTag;
import nl.knokko.customitems.plugin.CustomItemsApi;

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

        BukkitCommandRegistry.instance.registerCommand(PlaceKciBlockCommand.class);
        new KciItemsTag();
    }
}
