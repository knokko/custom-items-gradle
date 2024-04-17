package nl.knokko.customitems.itemset;

import nl.knokko.customitems.sound.KciSoundType;

import java.util.Collection;
import java.util.UUID;

public class SoundTypeReference extends UUIDBasedReference<KciSoundType>  {

    SoundTypeReference(UUID id, ItemSet itemSet) {
        super(id, itemSet);
    }

    SoundTypeReference(Model<KciSoundType> model) {
        super(model);
    }

    @Override
    String getDescription() {
        return "custom sound type";
    }

    @Override
    Collection<Model<KciSoundType>> getCollection() {
        return itemSet.soundTypes.elements;
    }

    @Override
    UUID extractIdentity(KciSoundType values) {
        return values.getId();
    }
}
