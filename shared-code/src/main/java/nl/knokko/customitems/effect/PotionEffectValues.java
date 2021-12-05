package nl.knokko.customitems.effect;

import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import java.util.Locale;

public class PotionEffectValues extends ModelValues {

    public static PotionEffectValues load1(BitInput input, boolean mutable) {
        PotionEffectValues result = new PotionEffectValues(mutable);
        result.load1(input);
        return result;
    }

    public static PotionEffectValues load2(BitInput input, boolean mutable) {
        PotionEffectValues result = new PotionEffectValues(mutable);
        result.load2(input);
        return result;
    }

    public static PotionEffectValues createQuick(EffectType type, int duration, int level) {
        PotionEffectValues result = new PotionEffectValues(true);
        result.setType(type);
        result.setDuration(duration);
        result.setLevel(level);
        return result;
    }

    private EffectType type;
    private int duration;
    private int level;

    public PotionEffectValues(boolean mutable) {
        super(mutable);

        this.type = EffectType.SPEED;
        this.duration = 200;
        this.level = 1;
    }

    public PotionEffectValues(PotionEffectValues toCopy, boolean mutable) {
        super(mutable);

        this.type = toCopy.getType();
        this.duration = toCopy.getDuration();
        this.level = toCopy.getLevel();
    }

    @Override
    public String toString() {
        return "Effect(" + type.name().toLowerCase(Locale.ROOT) + ",duration=" + duration + ",level=" + level + ")";
    }

    private void load1(BitInput input) {
        this.type = EffectType.valueOf(input.readJavaString());
        this.duration = input.readInt();
        this.level = input.readInt();
    }

    private void load2(BitInput input) {
        this.type = EffectType.valueOf(input.readString());
        this.duration = input.readInt();
        this.level = input.readInt();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof PotionEffectValues) {
            PotionEffectValues otherEffect = (PotionEffectValues) other;
            return this.type == otherEffect.type && this.duration == otherEffect.duration && this.level == otherEffect.level;
        } else {
            return false;
        }
    }

    @Override
    public PotionEffectValues copy(boolean mutable) {
        return new PotionEffectValues(this, mutable);
    }

    public void save1(BitOutput output) {
        output.addJavaString(type.name());
        output.addInt(duration);
        output.addInt(level);
    }

    public void save2(BitOutput output) {
        output.addString(type.name());
        output.addInts(duration, level);
    }

    public EffectType getType() {
        return type;
    }

    public int getDuration() {
        return duration;
    }

    public int getLevel() {
        return level;
    }

    public void setType(EffectType newType) {
        assertMutable();
        Checks.notNull(newType);
        this.type = newType;
    }

    public void setDuration(int newDuration) {
        assertMutable();
        this.duration = newDuration;
    }

    public void setLevel(int newLevel) {
        assertMutable();
        this.level = newLevel;
    }

    public void validate() throws ValidationException, ProgrammingValidationException {
        if (type == null) throw new ProgrammingValidationException("No type");
        if (duration <= 0) throw new ValidationException("Duration is not positive");
        if (level <= 0) throw new ValidationException("Level is not positive");
        if (level > 256) throw new ValidationException("Level is larger than 256");
    }

    public void validateExportVersion(int version) throws ValidationException {
        if (version < type.firstVersion) {
            throw new ValidationException(type + " doesn't exist yet in mc " + MCVersions.createString(version));
        }
        if (version > type.lastVersion) {
            throw new ValidationException(type + " doesn't exist anymore in mc " + MCVersions.createString(version));
        }
    }
}
