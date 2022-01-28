package nl.knokko.customitems.itemset;

import nl.knokko.customitems.texture.BaseTextureValues;
import nl.knokko.customitems.texture.CustomTexture;

import java.util.Collection;

public class TextureReference extends StringBasedReference<CustomTexture, BaseTextureValues> {

    TextureReference(String name, ItemSet itemSet) {
        super(name, itemSet);
    }

    TextureReference(CustomTexture model) {
        super(model);
    }

    @Override
    String getDescription() {
        return "texture";
    }

    @Override
    Collection<CustomTexture> getCollection() {
        return itemSet.textures;
    }

    @Override
    String extractIdentity(BaseTextureValues values) {
        return values.getName();
    }
}
