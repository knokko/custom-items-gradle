package nl.knokko.customitems.attack.effect;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

import static nl.knokko.customitems.util.Checks.isClose;

public class AttackEffectDelayedDamage extends AttackEffect {

    static AttackEffectDelayedDamage loadOwn(BitInput input) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("AttackDealDamage", encoding);

        AttackEffectDelayedDamage result = new AttackEffectDelayedDamage(true);
        result.damage = input.readFloat();
        result.delay = input.readInt();
        return result;
    }

    public static AttackEffectDelayedDamage createQuick(float damage, int delay) {
        AttackEffectDelayedDamage result = new AttackEffectDelayedDamage(true);
        result.setDamage(damage);
        result.setDelay(delay);
        return result;
    }

    private float damage;
    private int delay;

    public AttackEffectDelayedDamage(boolean mutable) {
        super(mutable);
        this.damage = 5f;
        this.delay = 1;
    }

    public AttackEffectDelayedDamage(AttackEffectDelayedDamage toCopy, boolean mutable) {
        super(mutable);
        this.damage = toCopy.getDamage();
        this.delay = toCopy.getDelay();
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(ENCODING_DEAL_DAMAGE);
        output.addByte((byte) 1);

        output.addFloat(damage);
        output.addInt(delay);
    }

    @Override
    public AttackEffectDelayedDamage copy(boolean mutable) {
        return new AttackEffectDelayedDamage(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof AttackEffectDelayedDamage) {
            AttackEffectDelayedDamage otherEffect = (AttackEffectDelayedDamage) other;
            return isClose(this.damage, otherEffect.damage) && this.delay == otherEffect.delay;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "AttackDealDamage(damage=" + damage + ",delay=" + delay + ")";
    }

    public float getDamage() {
        return damage;
    }

    public int getDelay() {
        return delay;
    }

    public void setDamage(float damage) {
        assertMutable();
        this.damage = damage;
    }

    public void setDelay(int delay) {
        assertMutable();
        this.delay = delay;
    }

    @Override
    public void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if (this.damage <= 0f) throw new ValidationException("Damage must be positive");
        if (this.delay <= 0) throw new ValidationException("Delay must be positive");
    }

    @Override
    public void validateExportVersion(int mcVersion) throws ValidationException, ProgrammingValidationException {
        // No properties are sensitive to the minecraft version
    }
}
