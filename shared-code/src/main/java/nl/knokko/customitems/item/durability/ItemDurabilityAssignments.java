package nl.knokko.customitems.item.durability;

import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.util.ValidationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemDurabilityAssignments {

    private short previousItemDamage = 0;

    public final List<ItemDurabilityClaim> claimList = new ArrayList<>();
    public final Map<String, Short> textureReuseMap = new HashMap<>();

    public short getNextItemDamage(CustomItemType itemType) throws ValidationException {
        short nextItemDamage = (short) (previousItemDamage + 1);
        if (itemType != CustomItemType.OTHER && nextItemDamage > itemType.getMaxDurability()) {
            throw new ValidationException("Too many items have internal item type " + itemType);
        }
        this.previousItemDamage = nextItemDamage;
        return nextItemDamage;
    }
}
