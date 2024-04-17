package nl.knokko.customitems.attack.effect;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

import static nl.knokko.customitems.util.Checks.isClose;

public class AttackEffectLaunchProjectile extends AttackEffect {

    static AttackEffectLaunchProjectile loadOwn(BitInput input) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("AttackLaunch", encoding);

        AttackEffectLaunchProjectile result = new AttackEffectLaunchProjectile(false);
        result.direction = LaunchDirection.valueOf(input.readString());
        result.speed = input.readFloat();
        return result;
    }

    public static AttackEffectLaunchProjectile createQuick(LaunchDirection direction, float speed) {
        AttackEffectLaunchProjectile result = new AttackEffectLaunchProjectile(true);
        result.setDirection(direction);
        result.setSpeed(speed);
        return result;
    }

    private LaunchDirection direction;
    private float speed;

    public AttackEffectLaunchProjectile(boolean mutable) {
        super(mutable);
        this.direction = LaunchDirection.ATTACK;
        this.speed = 0.5f;
    }

    public AttackEffectLaunchProjectile(AttackEffectLaunchProjectile toCopy, boolean mutable) {
        super(mutable);
        this.direction = toCopy.getDirection();
        this.speed = toCopy.getSpeed();
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(ENCODING_LAUNCH);
        output.addByte((byte) 1);

        output.addString(direction.name());
        output.addFloat(speed);
    }

    @Override
    public AttackEffectLaunchProjectile copy(boolean mutable) {
        return new AttackEffectLaunchProjectile(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof AttackEffectLaunchProjectile) {
            AttackEffectLaunchProjectile otherEffect = (AttackEffectLaunchProjectile) other;
            return this.direction == otherEffect.direction && isClose(this.speed, otherEffect.speed);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "AttackLaunch(" + direction + "," + speed + ")";
    }

    public LaunchDirection getDirection() {
        return direction;
    }

    public float getSpeed() {
        return speed;
    }

    public void setDirection(LaunchDirection direction) {
        assertMutable();
        Checks.notNull(direction);
        this.direction = direction;
    }

    public void setSpeed(float speed) {
        assertMutable();
        this.speed = speed;
    }

    @Override
    public void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if (direction == null) throw new ProgrammingValidationException("No direction");
        if (!Float.isFinite(speed)) throw new ValidationException("Speed must be finite");
    }

    @Override
    public void validateExportVersion(int mcVersion) throws ValidationException, ProgrammingValidationException {
        // This effect is completely available in all supported minecraft versions
    }

    public enum LaunchDirection {
        UP,
        ATTACK,
        ATTACK_HORIZONTAL,
        ATTACK_SIDE
    }
}
