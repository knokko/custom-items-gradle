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
    }

    @Override
    public TextureReference getTextureReference(String name) {
        return new TextureReference(name, this);
    }

    @Override
    public ArmorTextureReference getArmorTextureReference(String name) {
        return new ArmorTextureReference(name, this);
    }

    @Override
    public FancyPantsArmorTextureReference getFancyPantsArmorTextureReference(UUID id) {
        return new FancyPantsArmorTextureReference(id, this);
    }

    @Override
    public ItemReference getItemReference(String name) {
        return new ItemReference(name, this);
    }

    @Override
    public BlockReference getBlockReference(int internalID) {
        return new BlockReference(internalID, this);
    }

    @Override
    public ContainerReference getContainerReference(String name) {
        return new ContainerReference(name, this);
    }

    @Override
    public FuelRegistryReference getFuelRegistryReference(String name) {
        return new FuelRegistryReference(name, this);
    }

    @Override
    public ProjectileReference getProjectileReference(String name) {
        return new ProjectileReference(name, this);
    }

    @Override
    public ProjectileCoverReference getProjectileCoverReference(String name) {
        return new ProjectileCoverReference(name, this);
    }

    @Override
    public SoundTypeReference getSoundTypeReference(UUID id) {
        return new SoundTypeReference(id, this);
    }

    @Override
    public CustomDamageSourceReference getDamageSourceReference(UUID id) {
        return new CustomDamageSourceReference(id, this);
    }
}
