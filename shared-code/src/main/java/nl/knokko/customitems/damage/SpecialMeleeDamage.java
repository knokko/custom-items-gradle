package nl.knokko.customitems.damage;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ValidationException;

public class SpecialMeleeDamage extends ModelValues {

    public static SpecialMeleeDamage createQuick(VRawDamageSource damageSource, boolean shouldIgnoreArmor, boolean isFire) {
        SpecialMeleeDamage result = new SpecialMeleeDamage(true);
        result.setDamageSource(damageSource);
        result.setIgnoreArmor(shouldIgnoreArmor);
        result.setFire(isFire);
        return result;
    }

    public static SpecialMeleeDamage load(BitInput input) throws UnknownEncodingException {
        SpecialMeleeDamage result = new SpecialMeleeDamage(false);
        byte encoding = input.readByte();

        if (encoding != 1) throw new UnknownEncodingException("SpecialMeleeDamage", encoding);

        String damageSourceName = input.readString();
        result.damageSource = damageSourceName != null ? VRawDamageSource.valueOf(damageSourceName) : null;
        result.ignoreArmor = input.readBoolean();
        result.fire = input.readBoolean();

        return result;
    }

    private VRawDamageSource damageSource;
    private boolean ignoreArmor;
    private boolean fire;

    public SpecialMeleeDamage(boolean mutable) {
        super(mutable);
        this.damageSource = null;
        this.ignoreArmor = false;
        this.fire = false;
    }

    public SpecialMeleeDamage(SpecialMeleeDamage toCopy, boolean mutable) {
        super(mutable);
        this.damageSource = toCopy.getDamageSource();
        this.ignoreArmor = toCopy.shouldIgnoreArmor();
        this.fire = toCopy.isFire();
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);

        output.addString(this.damageSource != null ? this.damageSource.name() : null);
        output.addBoolean(this.ignoreArmor);
        output.addBoolean(this.fire);
    }

    @Override
    public SpecialMeleeDamage copy(boolean mutable) {
        return new SpecialMeleeDamage(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof SpecialMeleeDamage) {
            SpecialMeleeDamage otherDamage = (SpecialMeleeDamage) other;
            return this.damageSource == otherDamage.damageSource && this.ignoreArmor == otherDamage.ignoreArmor
                    && this.fire == otherDamage.fire;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "SpecialMeleeDamage(" + this.damageSource + ",ignoreArmor=" + this.ignoreArmor + ",fire=" + this.fire + ")";
    }

    public VRawDamageSource getDamageSource() {
        return damageSource;
    }

    public boolean shouldIgnoreArmor() {
        return ignoreArmor;
    }

    public boolean isFire() {
        return fire;
    }

    public void setDamageSource(VRawDamageSource damageSource) {
        assertMutable();
        this.damageSource = damageSource;
    }

    public void setIgnoreArmor(boolean ignoreArmor) {
        assertMutable();
        this.ignoreArmor = ignoreArmor;
    }

    public void setFire(boolean fire) {
        assertMutable();
        this.fire = fire;
    }

    public void validate() throws ValidationException {
        // Currently, all possible values of all fields are allowed
    }
}
