package nl.knokko.customitems.itemset;

import nl.knokko.customitems.texture.BaseTextureValues;

import java.util.Collection;

public class TextureReference extends StringBasedReference<BaseTextureValues> {

    TextureReference(String name, ItemSet itemSet) {
        super(name, itemSet);
    }

    TextureReference(Model<BaseTextureValues> model) {
        super(model);
    }

    @Override
    String getDescription() {
        return "texture";
    }

    @Override
    Collection<Model<BaseTextureValues>> getCollection() {
        return itemSet.textures.elements;
    }

    @Override
    String extractIdentity(BaseTextureValues values) {
        return values.getName();
    }
}
