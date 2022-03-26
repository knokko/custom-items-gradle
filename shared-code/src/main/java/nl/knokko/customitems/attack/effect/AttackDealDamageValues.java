package nl.knokko.customitems.attack.effect;

import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.damage.DamageSource;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

import static nl.knokko.customitems.util.Checks.isClose;

public class AttackDealDamageValues extends AttackEffectValues {

    static AttackDealDamageValues loadOwn(BitInput input) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("AttackDealDamage", encoding);

        AttackDealDamageValues result = new AttackDealDamageValues(true);
        result.damageSource = DamageSource.valueOf(input.readString());
        result.damage = input.readFloat();
        result.delay = input.readInt();
        return result;
    }

    public static AttackDealDamageValues createQuick(DamageSource damageSource, float damage, int delay) {
        AttackDealDamageValues result = new AttackDealDamageValues(true);
        result.setDamageSource(damageSource);
        result.setDamage(damage);
        result.setDelay(delay);
        return result;
    }

    private DamageSource damageSource;
    private float damage;
    private int delay;

    public AttackDealDamageValues(boolean mutable) {
        super(mutable);
        this.damageSource = DamageSource.THORNS;
        this.damage = 5f;
        this.delay = 1;
    }

    public AttackDealDamageValues(AttackDealDamageValues toCopy, boolean mutable) {
        super(mutable);
        this.damageSource = toCopy.getDamageSource();
        this.damage = toCopy.getDamage();
        this.delay = toCopy.getDelay();
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(ENCODING_DEAL_DAMAGE);
        output.addByte((byte) 1);

        output.addString(damageSource.name());
        output.addFloat(damage);
        output.addInt(delay);
    }

    @Override
    public AttackDealDamageValues copy(boolean mutable) {
        return new AttackDealDamageValues(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof AttackDealDamageValues) {
            AttackDealDamageValues otherEffect = (AttackDealDamageValues) other;
            return this.damageSource == otherEffect.damageSource && isClose(this.damage, otherEffect.damage)
                    && this.delay == otherEffect.delay;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "AttackDealDamage(" + damageSource + ",damage=" + damage + ",delay=" + delay + ")";
    }

    public DamageSource getDamageSource() {
        return damageSource;
    }

    public float getDamage() {
        return damage;
    }

    public int getDelay() {
        return delay;
    }

    public void setDamageSource(DamageSource damageSource) {
        assertMutable();
        Checks.notNull(damageSource);
        this.damageSource = damageSource;
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
    public void validate() throws ValidationException, ProgrammingValidationException {
        if (this.damageSource == null) throw new ProgrammingValidationException("No damage source");
        if (this.damage <= 0f) throw new ValidationException("Damage must be positive");
        if (this.delay <= 0) throw new ValidationException("Delay must be positive");
    }

    @Override
    public void validateExportVersion(int mcVersion) throws ValidationException, ProgrammingValidationException {
        if (mcVersion < damageSource.firstVersion) {
            throw new ValidationException("Damage source " + damageSource + " doesn't exist yet in MC " + MCVersions.createString(mcVersion));
        }
        if (mcVersion > damageSource.lastVersion) {
            throw new ValidationException("Damage source " + damageSource + " doesn't exist anymore in MC " + MCVersions.createString(mcVersion));
        }
    }
}
