package nl.knokko.customitems.nms21;

import nl.knokko.customitems.nms16plus.KciNms16Plus;
import org.bukkit.Bukkit;
import org.bukkit.attribute.AttributeModifier;

import java.util.UUID;

@SuppressWarnings("unused")
public class KciNms21 extends KciNms16Plus {

    public static final String NMS_VERSION_STRING = "1_21_R1";

    public KciNms21() {
        super(new KciNmsEntities21(), new KciNmsItems21());
    }

    @Override
    protected boolean isCompatible() {
        var id = new UUID(1234, 5678);

        @SuppressWarnings("deprecation")
        var testAttribute = new AttributeModifier(
                id, "generic.attackDamage", 1.0, AttributeModifier.Operation.ADD_NUMBER
        );

        boolean compatible = false;
        try {
            //noinspection deprecation
            compatible = id.equals(testAttribute.getUniqueId());
        } catch (IllegalArgumentException getUniqueIdMethodIsBugged) {
            // compatible stays false
        }

        if (!compatible) Bukkit.getLogger().severe("CustomItems requires a newer Spigot/Paper build");
        return compatible;
    }
}
