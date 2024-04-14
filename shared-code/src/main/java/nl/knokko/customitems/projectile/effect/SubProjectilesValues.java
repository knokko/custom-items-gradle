package nl.knokko.customitems.projectile.effect;

import nl.knokko.customitems.itemset.ProjectileReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.projectile.CustomProjectileValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import static nl.knokko.customitems.util.Checks.isClose;

public class SubProjectilesValues extends ProjectileEffectValues {

    static SubProjectilesValues load(BitInput input, byte encoding, ItemSet itemSet) throws UnknownEncodingException {
        SubProjectilesValues result = new SubProjectilesValues(false);

        if (encoding == ENCODING_SUB_PROJECTILE_1) {
            result.load1(input, itemSet);
        } else {
            throw new UnknownEncodingException("SubProjectilesEffect", encoding);
        }

        return result;
    }

    public static SubProjectilesValues createQuick(
            ProjectileReference child, boolean useParentLifetime, int minAmount, int maxAmount, float angleToParent
    ) {
        SubProjectilesValues result = new SubProjectilesValues(true);
        result.setChild(child);
        result.setUseParentLifetime(useParentLifetime);
        result.setMinAmount(minAmount);
        result.setMaxAmount(maxAmount);
        result.setAngleToParent(angleToParent);
        return result;
    }

    private ProjectileReference child;
    private boolean useParentLifetime;
    private int minAmount, maxAmount;
    private float angleToParent;

    public SubProjectilesValues(boolean mutable) {
        super(mutable);
        this.child = null;
        this.useParentLifetime = false;
        this.minAmount = 3;
        this.maxAmount = 4;
        this.angleToParent = 70f;
    }

    public SubProjectilesValues(SubProjectilesValues toCopy, boolean mutable) {
        super(mutable);
        this.child = toCopy.getChildReference();
        this.useParentLifetime = toCopy.shouldUseParentLifetime();
        this.minAmount = toCopy.getMinAmount();
        this.maxAmount = toCopy.getMaxAmount();
        this.angleToParent = toCopy.getAngleToParent();
    }

    @Override
    public String toString() {
        return "SubProjectile(" + child.get().getName() + ")";
    }

    private void load1(BitInput input, ItemSet itemSet) {
        this.child = itemSet.projectiles.getReference(input.readString());
        this.useParentLifetime = input.readBoolean();
        this.minAmount = input.readInt();
        this.maxAmount = input.readInt();
        this.angleToParent = input.readFloat();
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(ENCODING_SUB_PROJECTILE_1);
        output.addString(child.get().getName());
        output.addBoolean(useParentLifetime);
        output.addInts(minAmount, maxAmount);
        output.addFloat(angleToParent);
    }

    @Override
    public SubProjectilesValues copy(boolean mutable) {
        return new SubProjectilesValues(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == SubProjectilesValues.class) {
            SubProjectilesValues otherEffect = (SubProjectilesValues) other;
            return this.child.equals(otherEffect.child) && this.useParentLifetime == otherEffect.useParentLifetime
                    && this.minAmount == otherEffect.minAmount && this.maxAmount == otherEffect.maxAmount
                    && isClose(this.angleToParent, otherEffect.angleToParent);
        } else {
            return false;
        }
    }

    public CustomProjectileValues getChild() {
        return child == null ? null : child.get();
    }

    public ProjectileReference getChildReference() {
        return child;
    }

    public boolean shouldUseParentLifetime() {
        return useParentLifetime;
    }

    public int getMinAmount() {
        return minAmount;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public float getAngleToParent() {
        return angleToParent;
    }

    public void setChild(ProjectileReference newChild) {
        assertMutable();
        Checks.notNull(newChild);
        this.child = newChild;
    }

    public void setUseParentLifetime(boolean useParentLifetime) {
        assertMutable();
        this.useParentLifetime = useParentLifetime;
    }

    public void setMinAmount(int minAmount) {
        assertMutable();
        this.minAmount = minAmount;
    }

    public void setMaxAmount(int maxAmount) {
        assertMutable();
        this.maxAmount = maxAmount;
    }

    public void setAngleToParent(float angleToParent) {
        assertMutable();
        this.angleToParent = angleToParent;
    }

    @Override
    public void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if (child == null) throw new ValidationException("You need to choose a child projectile");
        if (!itemSet.projectiles.isValid(child)) throw new ProgrammingValidationException("Child is no longer valid");
        if (minAmount < 0) throw new ValidationException("Minimum amount can't be negative");
        if (minAmount > maxAmount) throw new ValidationException("Minimum amount can't be larger than maximum amount");
        if (!Float.isFinite(angleToParent)) throw new ValidationException("Angle to parent must be finite");
        if (angleToParent < 0f) throw new ValidationException("Angle to parent can't be negative");
    }
}
