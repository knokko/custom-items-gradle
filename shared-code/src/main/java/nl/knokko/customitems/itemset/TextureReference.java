package nl.knokko.customitems.itemset;

import nl.knokko.customitems.texture.KciTexture;

import java.util.Collection;

public class TextureReference extends StringBasedReference<KciTexture> {

    TextureReference(String name, ItemSet itemSet) {
        super(name, itemSet);
    }

    TextureReference(Model<KciTexture> model) {
        super(model);
    }

    @Override
    String getDescription() {
        return "texture";
    }

    @Override
    Collection<Model<KciTexture>> getCollection() {
        return itemSet.textures.elements;
    }

    @Override
    String extractIdentity(KciTexture values) {
        return values.getName();
    }
}
