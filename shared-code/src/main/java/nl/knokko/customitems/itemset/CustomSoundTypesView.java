package nl.knokko.customitems.itemset;

import nl.knokko.customitems.model.CollectionView;
import nl.knokko.customitems.sound.CustomSoundType;
import nl.knokko.customitems.sound.CustomSoundTypeValues;

import java.util.Collection;

public class CustomSoundTypesView extends CollectionView<CustomSoundType, CustomSoundTypeValues, SoundTypeReference> {
    public CustomSoundTypesView(Collection<CustomSoundType> liveCollection) {
        super(liveCollection, SoundTypeReference::new);
    }
}
