package nl.knokko.customitems.itemset;

import nl.knokko.customitems.texture.FancyPantsArmorTextureValues;

import java.util.Collection;
import java.util.UUID;

public class FancyPantsArmorTextureReference extends UUIDBasedReference<FancyPantsArmorTextureValues> {
    FancyPantsArmorTextureReference(UUID id, ItemSet itemSet) {
        super(id, itemSet);
    }

    FancyPantsArmorTextureReference(Model<FancyPantsArmorTextureValues> model) {
        super(model);
    }

    @Override
    String getDescription() {
        return "FP armor";
    }

    @Override
    Collection<Model<FancyPantsArmorTextureValues>> getCollection() {
        return itemSet.fancyPants.elements;
    }

    @Override
    UUID extractIdentity(FancyPantsArmorTextureValues values) {
        return values.getId();
    }
}
