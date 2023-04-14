package nl.knokko.customitems.itemset;

import nl.knokko.customitems.model.CollectionView;
import nl.knokko.customitems.texture.FancyPantsArmorTexture;
import nl.knokko.customitems.texture.FancyPantsArmorTextureValues;

import java.util.Collection;

public class FancyPantsArmorTexturesView extends CollectionView<
        FancyPantsArmorTexture, FancyPantsArmorTextureValues, FancyPantsArmorTextureReference
> {
    public FancyPantsArmorTexturesView(Collection<FancyPantsArmorTexture> liveCollection) {
        super(liveCollection, FancyPantsArmorTextureReference::new);
    }
}
