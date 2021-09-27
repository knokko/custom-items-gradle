package nl.knokko.customitems.itemset;

import nl.knokko.customitems.texture.ArmorTexture;
import nl.knokko.customitems.texture.ArmorTextureValues;

import java.util.Collection;

public class ArmorTextureReference extends StringBasedReference<ArmorTexture, ArmorTextureValues> {

    ArmorTextureReference(String name, SItemSet itemSet) {
        super(name, itemSet);
    }

    ArmorTextureReference(ArmorTexture model) {
        super(model);
    }

    @Override
    String getDescription() {
        return "armor texture";
    }

    @Override
    Collection<ArmorTexture> getCollection() {
        return itemSet.armorTextures;
    }

    @Override
    String extractIdentity(ArmorTextureValues values) {
        return values.getName();
    }
}
