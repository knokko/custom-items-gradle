package nl.knokko.customitems.plugin.multisupport.denizen;

import com.denizenscript.denizen.objects.ItemTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.tags.TagManager;
import nl.knokko.customitems.plugin.CustomItemsApi;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import org.bukkit.inventory.ItemStack;

public class KciItemsTag {

    KciItemsTag() {
        TagManager.registerStaticTagBaseHandler(ObjectTag.class, "kci_items", attribute -> {
            if (attribute.getAttribute(1).startsWith("kci_items")) {
                String customItemName = attribute.getParam();
                ItemStack customItemStack = CustomItemsApi.createItemStack(customItemName, 1);
                if (customItemStack != null) {
                    return new ItemTag(customItemStack);
                } else {
                    attribute.echoError("Can't find custom item with name " + customItemName);
                    return null;
                }
            } else {
                return null;
            }
        });
    }
}
