package nl.knokko.customitems.plugin.multisupport.denizen;

import com.denizenscript.denizen.objects.ItemTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.tags.TagManager;
import nl.knokko.customitems.plugin.CustomItemsApi;
import org.bukkit.inventory.ItemStack;

import static java.lang.Integer.parseInt;

public class KciItemsTag {

    KciItemsTag() {
        TagManager.registerStaticTagBaseHandler(ObjectTag.class, "kci_items", attribute -> {
            if (attribute.getAttribute(1).startsWith("kci_items")) {
                String rawItem = attribute.getParam();
                String customItemName;
                int amount;
                int indexStar = rawItem.lastIndexOf('*');
                if (indexStar != -1) {
                    customItemName = rawItem.substring(0, indexStar);
                    String rawAmount = rawItem.substring(indexStar + 1);
                    try {
                        amount = parseInt(rawAmount);
                    } catch (NumberFormatException invalidAmount) {
                        attribute.echoError("Invalid amount: '" + rawAmount + "'");
                        return null;
                    }
                } else {
                    customItemName = rawItem;
                    amount = 1;
                }
                ItemStack customItemStack = CustomItemsApi.createItemStack(customItemName, amount);
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
