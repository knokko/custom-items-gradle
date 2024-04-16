package nl.knokko.customitems.itemset;

import nl.knokko.customitems.sound.CustomSoundTypeValues;

import java.util.Collection;
import java.util.UUID;

public class SoundTypeReference extends UUIDBasedReference<CustomSoundTypeValues>  {

    SoundTypeReference(UUID id, ItemSet itemSet) {
        super(id, itemSet);
    }

    SoundTypeReference(Model<CustomSoundTypeValues> model) {
        super(model);
    }

    @Override
    String getDescription() {
        return "custom sound type";
    }

    @Override
    Collection<Model<CustomSoundTypeValues>> getCollection() {
        return itemSet.soundTypes.elements;
    }

    @Override
    UUID extractIdentity(CustomSoundTypeValues values) {
        return values.getId();
    }
}
