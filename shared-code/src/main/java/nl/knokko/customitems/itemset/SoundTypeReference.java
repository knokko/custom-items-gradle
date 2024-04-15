package nl.knokko.customitems.itemset;

import nl.knokko.customitems.sound.CustomSoundType;
import nl.knokko.customitems.sound.CustomSoundTypeValues;

import java.util.Collection;
import java.util.UUID;

public class SoundTypeReference extends UUIDBasedReference<CustomSoundType, CustomSoundTypeValues>  {

    SoundTypeReference(UUID id, ItemSet itemSet) {
        super(id, itemSet);
    }

    SoundTypeReference(CustomSoundType model) {
        super(model);
    }

    @Override
    String getDescription() {
        return "custom sound type";
    }

    @Override
    Collection<CustomSoundType> getCollection() {
        return itemSet.soundTypes.elements;
    }

    @Override
    UUID extractIdentity(CustomSoundTypeValues values) {
        return values.getId();
    }
}
