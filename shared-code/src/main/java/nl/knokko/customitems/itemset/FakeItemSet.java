package nl.knokko.customitems.itemset;

import java.util.ArrayList;
import java.util.UUID;

/**
 * This is a dirty subclass of ItemSet that overrides all reference getter methods
 * (getItemReference, getBlockReference, ...) such that they always return uninitialized references.
 */
public class FakeItemSet extends ItemSet {
    public FakeItemSet() {
        super(Side.PLUGIN);

        this.intReferences = new ArrayList<>();
        this.stringReferences = new ArrayList<>();
        this.uuidReferences = new ArrayList<>();
        this.finishedLoading = false;
    }

    @Override
    public FancyPantsArmorTextureReference getFancyPantsArmorTextureReference(UUID id) {
        return new FancyPantsArmorTextureReference(id, this);
    }

    @Override
    public FuelRegistryReference getFuelRegistryReference(String name) {
        return new FuelRegistryReference(name, this);
    }

    @Override
    public ProjectileCoverReference getProjectileCoverReference(String name) {
        return new ProjectileCoverReference(name, this);
    }
}
