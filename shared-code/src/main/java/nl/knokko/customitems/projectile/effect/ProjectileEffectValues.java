package nl.knokko.customitems.projectile.effect;

import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public abstract class ProjectileEffectValues extends ModelValues {

    static final byte ENCODING_EXPLOSION_1 = 0;
    static final byte ENCODING_COLORED_REDSTONE_1 = 1;
    static final byte ENCODING_SIMPLE_PARTICLE_1 = 2;
    static final byte ENCODING_STRAIGHT_ACCELERATION_1 = 3;
    static final byte ENCODING_RANDOM_ACCELERATION_1 = 4;
    static final byte ENCODING_SUB_PROJECTILE_1 = 5;
    static final byte ENCODING_COMMAND_1 = 6;
    static final byte ENCODING_PUSH_PULL_1 = 7;
    static final byte ENCODING_PLAY_SOUND_1 = 8;
    static final byte ENCODING_FIREWORK_1 = 9;
    static final byte ENCODING_POTION_AURA_1 = 10;

    public static ProjectileEffectValues load(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();

        if (encoding == ENCODING_COLORED_REDSTONE_1) {
            return ColoredRedstoneValues.load(input, encoding);
        } else if (encoding == ENCODING_COMMAND_1) {
            return ExecuteCommandValues.load(input, encoding);
        } else if (encoding == ENCODING_EXPLOSION_1) {
            return ExplosionValues.load(input, encoding);
        } else if (encoding == ENCODING_PLAY_SOUND_1) {
            return PlaySoundValues.load(input, encoding);
        } else if (encoding == ENCODING_POTION_AURA_1) {
            return PotionAuraValues.load(input, encoding);
        } else if (encoding == ENCODING_PUSH_PULL_1) {
            return PushOrPullValues.load(input, encoding);
        } else if (encoding == ENCODING_RANDOM_ACCELERATION_1) {
            return RandomAccelerationValues.load(input, encoding);
        } else if (encoding == ENCODING_FIREWORK_1) {
            return ShowFireworkValues.load(input, encoding);
        } else if (encoding == ENCODING_SIMPLE_PARTICLE_1) {
            return SimpleParticleValues.load(input, encoding);
        } else if (encoding == ENCODING_STRAIGHT_ACCELERATION_1) {
            return StraightAccelerationValues.load(input, encoding);
        } else if (encoding == ENCODING_SUB_PROJECTILE_1) {
            return SubProjectilesValues.load(input, encoding, itemSet);
        } else {
            throw new UnknownEncodingException("ProjectileEffect", encoding);
        }
    }

    ProjectileEffectValues(boolean mutable) {
        super(mutable);
    }

    public abstract void save(BitOutput output);

    @Override
    public abstract ProjectileEffectValues copy(boolean mutable);

    public abstract void validate(SItemSet itemSet) throws ValidationException, ProgrammingValidationException;

    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        // Most projectile effects don't need this
    }
}
