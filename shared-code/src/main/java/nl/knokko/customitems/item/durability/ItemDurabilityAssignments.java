package nl.knokko.customitems.item.durability;

import nl.knokko.customitems.item.KciItemType;
import nl.knokko.customitems.util.ValidationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemDurabilityAssignments {

    private short previousItemDamage = 0;

    public final List<ItemDurabilityClaim> claimList = new ArrayList<>();
    public final Map<String, Short> textureReuseMap = new HashMap<>();

    public short getNextItemDamage(KciItemType itemType, int mcVersion) throws ValidationException {
        short nextItemDamage = (short) (previousItemDamage + 1);
        if (itemType != KciItemType.OTHER && nextItemDamage > itemType.getMaxDurability(mcVersion)) {
            throw new ValidationException("Too many items have internal item type " + itemType);
        }
        this.previousItemDamage = nextItemDamage;
        return nextItemDamage;
    }
}
