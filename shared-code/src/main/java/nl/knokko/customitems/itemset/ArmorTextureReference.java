package nl.knokko.customitems.itemset;

import nl.knokko.customitems.texture.ArmorTextureValues;

import java.util.Collection;

public class ArmorTextureReference extends StringBasedReference<ArmorTextureValues> {

    ArmorTextureReference(String name, ItemSet itemSet) {
        super(name, itemSet);
    }

    ArmorTextureReference(Model<ArmorTextureValues> model) {
        super(model);
    }

    @Override
    String getDescription() {
        return "armor texture";
    }

    @Override
    Collection<Model<ArmorTextureValues>> getCollection() {
        return itemSet.armorTextures.elements;
    }

    @Override
    String extractIdentity(ArmorTextureValues values) {
        return values.getName();
    }
}
