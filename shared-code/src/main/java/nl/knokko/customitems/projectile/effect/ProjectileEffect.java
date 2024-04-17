package nl.knokko.customitems.projectile.effect;

import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

public abstract class ProjectileEffect extends ModelValues {

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
    static final byte ENCODING_PLAY_SOUND_NEW = 11;

    public static ProjectileEffect load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();

        if (encoding == ENCODING_COLORED_REDSTONE_1) {
            return PEColoredRedstone.load(input, encoding);
        } else if (encoding == ENCODING_COMMAND_1) {
            return PEExecuteCommand.load(input, encoding);
        } else if (encoding == ENCODING_EXPLOSION_1) {
            return PECreateExplosion.load(input, encoding);
        } else if (encoding == ENCODING_PLAY_SOUND_1 || encoding == ENCODING_PLAY_SOUND_NEW) {
            return PEPlaySound.load(input, encoding, itemSet);
        } else if (encoding == ENCODING_POTION_AURA_1) {
            return PEPotionAura.load(input, encoding);
        } else if (encoding == ENCODING_PUSH_PULL_1) {
            return PEPushOrPull.load(input, encoding);
        } else if (encoding == ENCODING_RANDOM_ACCELERATION_1) {
            return PERandomAcceleration.load(input, encoding);
        } else if (encoding == ENCODING_FIREWORK_1) {
            return PEShowFireworks.load(input, encoding);
        } else if (encoding == ENCODING_SIMPLE_PARTICLE_1) {
            return PESimpleParticle.load(input, encoding);
        } else if (encoding == ENCODING_STRAIGHT_ACCELERATION_1) {
            return PEStraightAcceleration.load(input, encoding);
        } else if (encoding == ENCODING_SUB_PROJECTILE_1) {
            return PESubProjectiles.load(input, encoding, itemSet);
        } else {
            throw new UnknownEncodingException("ProjectileEffect", encoding);
        }
    }

    ProjectileEffect(boolean mutable) {
        super(mutable);
    }

    public abstract void save(BitOutput output);

    @Override
    public abstract ProjectileEffect copy(boolean mutable);

    public abstract void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException;

    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        // Most projectile effects don't need this
    }
}
