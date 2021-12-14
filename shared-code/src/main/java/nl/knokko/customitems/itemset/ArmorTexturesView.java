package nl.knokko.customitems.itemset;

import nl.knokko.customitems.model.CollectionView;
import nl.knokko.customitems.texture.ArmorTexture;
import nl.knokko.customitems.texture.ArmorTextureValues;

import java.util.Collection;

public class ArmorTexturesView extends CollectionView<ArmorTexture, ArmorTextureValues, ArmorTextureReference> {
    public ArmorTexturesView(Collection<ArmorTexture> liveCollection) {
        super(liveCollection, ArmorTextureReference::new);
    }
}
