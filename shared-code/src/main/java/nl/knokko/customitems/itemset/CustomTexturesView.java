package nl.knokko.customitems.itemset;

import nl.knokko.customitems.model.CollectionView;
import nl.knokko.customitems.texture.BaseTextureValues;
import nl.knokko.customitems.texture.CustomTexture;

import java.util.Collection;

public class CustomTexturesView extends CollectionView<CustomTexture, BaseTextureValues, TextureReference> {
    public CustomTexturesView(Collection<CustomTexture> liveCollection) {
        super(liveCollection, TextureReference::new);
    }
}
