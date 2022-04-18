package nl.knokko.customitems.damage;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ValidationException;

public class SpecialMeleeDamageValues extends ModelValues {

    public static SpecialMeleeDamageValues createQuick(RawDamageSource damageSource, boolean shouldIgnoreArmor, boolean isFire) {
        SpecialMeleeDamageValues result = new SpecialMeleeDamageValues(true);
        result.setDamageSource(damageSource);
        result.setIgnoreArmor(shouldIgnoreArmor);
        result.setFire(isFire);
        return result;
    }

    public static SpecialMeleeDamageValues load(BitInput input) throws UnknownEncodingException {
        SpecialMeleeDamageValues result = new SpecialMeleeDamageValues(false);
        byte encoding = input.readByte();

        if (encoding != 1) throw new UnknownEncodingException("SpecialMeleeDamage", encoding);

        String damageSourceName = input.readString();
        result.damageSource = damageSourceName != null ? RawDamageSource.valueOf(damageSourceName) : null;
        result.ignoreArmor = input.readBoolean();
        result.fire = input.readBoolean();

        return result;
    }

    private RawDamageSource damageSource;
    private boolean ignoreArmor;
    private boolean fire;

    public SpecialMeleeDamageValues(boolean mutable) {
        super(mutable);
        this.damageSource = null;
        this.ignoreArmor = false;
        this.fire = false;
    }

    public SpecialMeleeDamageValues(SpecialMeleeDamageValues toCopy, boolean mutable) {
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
    public SpecialMeleeDamageValues copy(boolean mutable) {
        return new SpecialMeleeDamageValues(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof SpecialMeleeDamageValues) {
            SpecialMeleeDamageValues otherDamage = (SpecialMeleeDamageValues) other;
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

    public RawDamageSource getDamageSource() {
        return damageSource;
    }

    public boolean shouldIgnoreArmor() {
        return ignoreArmor;
    }

    public boolean isFire() {
        return fire;
    }

    public void setDamageSource(RawDamageSource damageSource) {
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
