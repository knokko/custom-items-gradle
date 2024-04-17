package nl.knokko.customitems.item;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.itemset.ProjectileReference;
import nl.knokko.customitems.projectile.KciProjectile;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

import java.util.Objects;

import static nl.knokko.customitems.encoding.ItemEncoding.ENCODING_THROWABLE;

public class KciThrowable extends KciItem {

    static KciThrowable load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("CustomThrowable", encoding);

        KciThrowable result = new KciThrowable(false);
        result.loadSharedPropertiesNew(input, itemSet);
        result.projectile = itemSet.projectiles.getReference(input.readString());
        result.maxStackSize = input.readByte();
        result.cooldown = input.readInt();
        result.amountPerShot = input.readInt();
        result.requiresPermission = input.readBoolean();

        return result;
    }

    private ProjectileReference projectile;
    private byte maxStackSize;
    private int cooldown;
    private int amountPerShot;
    private boolean requiresPermission;

    public KciThrowable(boolean mutable) {
        super(mutable, KciItemType.DIAMOND_HOE);
        this.projectile = null;
        this.maxStackSize = 64;
        this.cooldown = 40;
        this.amountPerShot = 1;
        this.requiresPermission = false;
    }

    public KciThrowable(KciThrowable source, boolean mutable) {
        super(source, mutable);
        this.projectile = source.getProjectileReference();
        this.maxStackSize = source.getMaxStacksize();
        this.cooldown = source.getCooldown();
        this.amountPerShot = source.getAmountPerShot();
        this.requiresPermission = source.shouldRequirePermission();
    }

    @Override
    public KciThrowable copy(boolean mutable) {
        return new KciThrowable(this, mutable);
    }

    @Override
    public void save(BitOutput output, ItemSet.Side side) {
        output.addByte(ENCODING_THROWABLE);
        output.addByte((byte) 1);
        saveSharedPropertiesNew(output, side);

        output.addString(projectile.get().getName());
        output.addByte(maxStackSize);
        output.addInt(cooldown);
        output.addInt(amountPerShot);
        output.addBoolean(requiresPermission);
    }

    @Override
    public byte getMaxStacksize() {
        return maxStackSize;
    }

    protected boolean areThrowablePropertiesEqual(KciThrowable other) {
        return areBaseItemPropertiesEqual(other) && this.projectile.equals(other.projectile) &&
                this.maxStackSize == other.maxStackSize && this.cooldown == other.cooldown &&
                this.amountPerShot == other.amountPerShot && this.requiresPermission == other.requiresPermission;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof KciThrowable) return areThrowablePropertiesEqual((KciThrowable) other);
        else return false;
    }

    public ProjectileReference getProjectileReference() {
        return projectile;
    }

    public KciProjectile getProjectile() {
        return projectile == null ? null : projectile.get();
    }

    public int getCooldown() {
        return cooldown;
    }

    public int getAmountPerShot() {
        return amountPerShot;
    }

    public boolean shouldRequirePermission() {
        return requiresPermission;
    }

    public void setProjectile(ProjectileReference newProjectile) {
        assertMutable();
        this.projectile = Objects.requireNonNull(newProjectile);
    }

    public void setMaxStackSize(byte newStackSize) {
        assertMutable();
        this.maxStackSize = newStackSize;
    }

    public void setCooldown(int newCooldown) {
        assertMutable();
        this.cooldown = newCooldown;
    }

    public void setAmountPerShot(int newAmount) {
        assertMutable();
        this.amountPerShot = newAmount;
    }

    public void setRequiresPermission(boolean requiresPermission) {
        assertMutable();
        this.requiresPermission = requiresPermission;
    }

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        super.validateIndependent();

        if (projectile == null) throw new ValidationException("You must select a projectile");
        if (maxStackSize < 1) throw new ProgrammingValidationException("Maximum stacksize must be positive");
        if (maxStackSize > 64) throw new ProgrammingValidationException("Maximum stacksize can be at most 64");
        if (cooldown < 1) throw new ProgrammingValidationException("Cooldown must be positive");
        if (amountPerShot < 1) throw new ProgrammingValidationException("Amount per shot must be positive");
    }

    @Override
    public void validateComplete(ItemSet itemSet, String oldName) throws ValidationException, ProgrammingValidationException {
        super.validateComplete(itemSet, oldName);

        if (!itemSet.projectiles.isValid(projectile)) throw new ValidationException("Projectile is no longer valid");
    }
}
