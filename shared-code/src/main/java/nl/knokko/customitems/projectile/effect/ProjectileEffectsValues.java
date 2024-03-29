package nl.knokko.customitems.projectile.effect;

import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.model.Mutability;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.ArrayList;
import java.util.Collection;

public class ProjectileEffectsValues extends ModelValues {

    private static final byte ENCODING_1 = 0;

    public static ProjectileEffectsValues load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        ProjectileEffectsValues result = new ProjectileEffectsValues(false);

        if (encoding == ENCODING_1) {
            result.load1(input, itemSet);
        } else {
            throw new UnknownEncodingException("ProjectileEffects", encoding);
        }

        return result;
    }

    private int delay, period;
    private Collection<ProjectileEffectValues> effects;

    public ProjectileEffectsValues(boolean mutable) {
        super(mutable);
        this.delay = 10;
        this.period = 20;
        this.effects = new ArrayList<>();
    }

    public ProjectileEffectsValues(ProjectileEffectsValues toCopy, boolean mutable) {
        super(mutable);
        this.delay = toCopy.getDelay();
        this.period = toCopy.getPeriod();
        this.effects = toCopy.getEffects();
    }

    private void load1(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        this.delay = input.readInt();
        this.period = input.readInt();
        int numEffects = input.readByte() & 0xFF;
        this.effects = new ArrayList<>(numEffects);
        for (int counter = 0; counter < numEffects; counter++) {
            this.effects.add(ProjectileEffectValues.load(input, itemSet));
        }
    }

    public void save(BitOutput output) {
        output.addByte(ENCODING_1);
        output.addInts(delay, period);
        output.addByte((byte) effects.size());
        for (ProjectileEffectValues effect : effects) {
            effect.save(output);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == ProjectileEffectsValues.class) {
            ProjectileEffectsValues otherEffects = (ProjectileEffectsValues) other;
            return this.delay == otherEffects.delay && this.period == otherEffects.period
                    && this.effects.equals(otherEffects.effects);
        } else {
            return false;
        }
    }

    @Override
    public ProjectileEffectsValues copy(boolean mutable) {
        return new ProjectileEffectsValues(this, mutable);
    }

    @Override
    public String toString() {
        return this.effects.size() + " effects with period " + this.period;
    }

    public int getDelay() {
        return delay;
    }

    public int getPeriod() {
        return period;
    }

    public Collection<ProjectileEffectValues> getEffects() {
        return new ArrayList<>(effects);
    }

    public void setDelay(int delay) {
        assertMutable();
        this.delay = delay;
    }

    public void setPeriod(int period) {
        assertMutable();
        this.period = period;
    }

    public void setEffects(Collection<ProjectileEffectValues> effects) {
        assertMutable();
        Checks.nonNull(effects);
        this.effects = Mutability.createDeepCopy(effects, false);
    }

    public void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if (delay < 0) throw new ValidationException("Delay can't be negative");
        if (period <= 0) throw new ValidationException("Period must be positive");
        if (effects == null) throw new ProgrammingValidationException("No effects");
        if (effects.isEmpty()) throw new ValidationException("You need at least 1 effect");
        if (effects.size() > Byte.MAX_VALUE) throw new ValidationException("You can have at most 127 effects (technical reasons)");
        for (ProjectileEffectValues effect : effects) {
            if (effect == null) throw new ProgrammingValidationException("Missing an effect");
            Validation.scope(effect.toString(), () -> effect.validate(itemSet));
        }
    }

    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        for (ProjectileEffectValues effect : effects) {
            effect.validateExportVersion(version);
        }
    }
}
