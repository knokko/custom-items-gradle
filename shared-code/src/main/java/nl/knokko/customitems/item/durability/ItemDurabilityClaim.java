package nl.knokko.customitems.item.durability;

import nl.knokko.customitems.texture.BowTextureEntry;

import java.util.List;

public class ItemDurabilityClaim {

    public final String resourcePath;
    public final short itemDamage;

    public final List<BowTextureEntry> pullTextures;

    public ItemDurabilityClaim(String resourcePath, short itemDamage, List<BowTextureEntry> pullTextures) {
        this.resourcePath = resourcePath;
        this.itemDamage = itemDamage;
        this.pullTextures = pullTextures;
    }
}
