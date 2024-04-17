package nl.knokko.customitems.itemset;

import nl.knokko.customitems.texture.ArmorTexture;

import java.util.Collection;

public class ArmorTextureReference extends StringBasedReference<ArmorTexture> {

    ArmorTextureReference(String name, ItemSet itemSet) {
        super(name, itemSet);
    }

    ArmorTextureReference(Model<ArmorTexture> model) {
        super(model);
    }

    @Override
    String getDescription() {
        return "armor texture";
    }

    @Override
    Collection<Model<ArmorTexture>> getCollection() {
        return itemSet.armorTextures.elements;
    }

    @Override
    String extractIdentity(ArmorTexture values) {
        return values.getName();
    }
}
