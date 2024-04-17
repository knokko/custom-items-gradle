package nl.knokko.customitems.itemset;

import nl.knokko.customitems.texture.FancyPantsTexture;

import java.util.Collection;
import java.util.UUID;

public class FancyPantsReference extends UUIDBasedReference<FancyPantsTexture> {
    FancyPantsReference(UUID id, ItemSet itemSet) {
        super(id, itemSet);
    }

    FancyPantsReference(Model<FancyPantsTexture> model) {
        super(model);
    }

    @Override
    String getDescription() {
        return "FP armor";
    }

    @Override
    Collection<Model<FancyPantsTexture>> getCollection() {
        return itemSet.fancyPants.elements;
    }

    @Override
    UUID extractIdentity(FancyPantsTexture values) {
        return values.getId();
    }
}
