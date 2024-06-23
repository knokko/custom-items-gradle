package nl.knokko.customitems.effect;

import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Chance;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

public class ChancePotionEffect extends ModelValues {

    public static ChancePotionEffect load(BitInput input) throws UnknownEncodingException {
        ChancePotionEffect result = new ChancePotionEffect(false);

        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("ChancePotionEffect", encoding);

        result.type = VEffectType.valueOf(input.readString());
        result.duration = input.readInt();
        result.level = input.readInt();
        result.chance = Chance.load(input);

        return result;
    }

    public static ChancePotionEffect createQuick(KciPotionEffect effect, Chance chance) {
        ChancePotionEffect result = new ChancePotionEffect(true);
        result.setType(effect.getType());
        result.setDuration(effect.getDuration());
        result.setLevel(effect.getLevel());
        result.setChance(chance);
        return result.copy(false);
    }

    public static ChancePotionEffect createQuick(VEffectType type, int duration, int level, Chance chance) {
        return createQuick(KciPotionEffect.createQuick(type, duration, level), chance);
    }

    private VEffectType type;
    private int duration;
    private int level;
    private Chance chance;

    public ChancePotionEffect(boolean mutable) {
        super(mutable);
        this.type = VEffectType.SPEED;
        this.duration = 200;
        this.level = 1;
        this.chance = Chance.percentage(100);
    }

    public ChancePotionEffect(ChancePotionEffect toCopy, boolean mutable) {
        super(mutable);
        this.type = toCopy.getType();
        this.duration = toCopy.getDuration();
        this.level = toCopy.getLevel();
        this.chance = toCopy.getChance();
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);

        output.addString(this.type.name());
        output.addInt(this.duration);
        output.addInt(this.level);
        this.chance.save(output);
    }

    @Override
    public ChancePotionEffect copy(boolean mutable) {
        return new ChancePotionEffect(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ChancePotionEffect) {
            ChancePotionEffect otherEffect = (ChancePotionEffect) other;
            return this.type == otherEffect.type && this.duration == otherEffect.duration
                    && this.level == otherEffect.level && this.chance.equals(otherEffect.chance);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return this.chance + " " + this.type + "(duration=" + this.duration + ",level=" + this.level + ")";
    }

    public VEffectType getType() {
        return this.type;
    }

    public int getDuration() {
        return this.duration;
    }

    public int getLevel() {
        return this.level;
    }

    public Chance getChance() {
        return this.chance;
    }

    public void setType(VEffectType type) {
        assertMutable();
        Checks.notNull(type);
        this.type = type;
    }

    public void setDuration(int duration) {
        assertMutable();
        this.duration = duration;
    }

    public void setLevel(int level) {
        assertMutable();
        this.level = level;
    }

    public void setChance(Chance chance) {
        assertMutable();
        Checks.notNull(chance);
        this.chance = chance;
    }

    public void validate() throws ValidationException, ProgrammingValidationException {
        if (this.type == null) throw new ProgrammingValidationException("No type");
        if (this.duration <= 0) throw new ValidationException("Duration must be positive");
        if (this.level <= 0) throw new ValidationException("Level must be positive");
        if (this.level > 256) throw new ValidationException("Level can be at most 256");
        if (this.chance == null) throw new ProgrammingValidationException("No chance");
    }

    public void validateExportVersion(int version) throws ValidationException {
        if (this.type.firstVersion > version) throw new ValidationException(this.type + " doesn't exist yet in MC " + MCVersions.createString(version));
        if (this.type.lastVersion < version) throw new ValidationException(this.type + " doesn't exist anymore in MC " + MCVersions.createString(version));
    }
}
