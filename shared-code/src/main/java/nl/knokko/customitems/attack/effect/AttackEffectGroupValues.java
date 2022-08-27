package nl.knokko.customitems.attack.effect;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.model.Mutability;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.*;

import java.util.ArrayList;
import java.util.Collection;

import static nl.knokko.customitems.util.Checks.isClose;

public class AttackEffectGroupValues extends ModelValues {

    public static AttackEffectGroupValues load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("AttackEffectGroup", encoding);

        AttackEffectGroupValues result = new AttackEffectGroupValues(false);

        int numAttackerEffects = input.readInt();
        result.attackerEffects = new ArrayList<>(numAttackerEffects);
        for (int counter = 0; counter < numAttackerEffects; counter++) {
            result.attackerEffects.add(AttackEffectValues.load(input, itemSet));
        }

        int numVictimEffects = input.readInt();
        result.victimEffects = new ArrayList<>(numVictimEffects);
        for (int counter = 0; counter < numVictimEffects; counter++) {
            result.victimEffects.add(AttackEffectValues.load(input, itemSet));
        }

        result.chance = Chance.load(input);
        result.originalDamageThreshold = input.readFloat();
        result.finalDamageThreshold = input.readFloat();

        return result;
    }

    public static AttackEffectGroupValues createQuick(
            Collection<AttackEffectValues> attackerEffects, Collection<AttackEffectValues> victimEffects,
            Chance chance, float originalDamageThreshold, float finalDamageThreshold
    ) {
        AttackEffectGroupValues result = new AttackEffectGroupValues(true);
        result.setAttackerEffects(attackerEffects);
        result.setVictimEffects(victimEffects);
        result.setChance(chance);
        result.setOriginalDamageThreshold(originalDamageThreshold);
        result.setFinalDamageThreshold(finalDamageThreshold);
        return result;
    }

    private Collection<AttackEffectValues> attackerEffects;
    private Collection<AttackEffectValues> victimEffects;
    private Chance chance;
    private float originalDamageThreshold;
    private float finalDamageThreshold;

    public AttackEffectGroupValues(boolean mutable) {
        super(mutable);
        this.attackerEffects = new ArrayList<>();
        this.victimEffects = new ArrayList<>();
        this.chance = Chance.percentage(100);
        this.originalDamageThreshold = 0f;
        this.finalDamageThreshold = 0f;
    }

    public AttackEffectGroupValues(AttackEffectGroupValues toCopy, boolean mutable) {
        super(mutable);
        this.attackerEffects = toCopy.getAttackerEffects();
        this.victimEffects = toCopy.getVictimEffects();
        this.chance = toCopy.getChance();
        this.originalDamageThreshold = toCopy.getOriginalDamageThreshold();
        this.finalDamageThreshold = toCopy.getFinalDamageThreshold();
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);

        output.addInt(attackerEffects.size());
        for (AttackEffectValues attackEffect : attackerEffects) {
            attackEffect.save(output);
        }

        output.addInt(victimEffects.size());
        for (AttackEffectValues victimEffect : victimEffects) {
            victimEffect.save(output);
        }

        chance.save(output);
        output.addFloat(originalDamageThreshold);
        output.addFloat(finalDamageThreshold);
    }

    @Override
    public AttackEffectGroupValues copy(boolean mutable) {
        return new AttackEffectGroupValues(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof AttackEffectGroupValues) {
            AttackEffectGroupValues otherEffects = (AttackEffectGroupValues) other;
            return this.attackerEffects.equals(otherEffects.attackerEffects) && this.victimEffects.equals(otherEffects.victimEffects)
                    && this.chance.equals(otherEffects.chance) && isClose(this.originalDamageThreshold, otherEffects.originalDamageThreshold)
                    && isClose(this.finalDamageThreshold, otherEffects.finalDamageThreshold);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "AttackEffectGroup(#attackerEffects=" + attackerEffects.size() + ",#victimEffects=" + victimEffects.size()
                + ",chance=" + chance + ",originalThreshold=" + originalDamageThreshold + ",finalThreshold=" + finalDamageThreshold + ")";
    }

    public Collection<AttackEffectValues> getAttackerEffects() {
        return new ArrayList<>(attackerEffects);
    }

    public Collection<AttackEffectValues> getVictimEffects() {
        return new ArrayList<>(victimEffects);
    }

    public Chance getChance() {
        return chance;
    }

    public float getOriginalDamageThreshold() {
        return originalDamageThreshold;
    }

    public float getFinalDamageThreshold() {
        return finalDamageThreshold;
    }

    public void setAttackerEffects(Collection<AttackEffectValues> attackerEffects) {
        assertMutable();
        Checks.nonNull(attackerEffects);
        this.attackerEffects = Mutability.createDeepCopy(attackerEffects, false);
    }

    public void setVictimEffects(Collection<AttackEffectValues> victimEffects) {
        assertMutable();
        Checks.nonNull(victimEffects);
        this.victimEffects = Mutability.createDeepCopy(victimEffects, false);
    }

    public void setChance(Chance chance) {
        assertMutable();
        Checks.notNull(chance);
        this.chance = chance;
    }

    public void setOriginalDamageThreshold(float originalDamageThreshold) {
        assertMutable();
        this.originalDamageThreshold = originalDamageThreshold;
    }

    public void setFinalDamageThreshold(float finalDamageThreshold) {
        assertMutable();
        this.finalDamageThreshold = finalDamageThreshold;
    }

    public void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if (attackerEffects == null) throw new ProgrammingValidationException("No attacker effects");
        for (AttackEffectValues attackEffect : attackerEffects) {
            if (attackEffect == null) throw new ProgrammingValidationException("Missing an attacker effect");
            Validation.scope("Attacker effects", attackEffect::validate, itemSet);
        }
        if (victimEffects == null) throw new ProgrammingValidationException("No victim effects");
        for (AttackEffectValues victimEffect : victimEffects) {
            if (victimEffect == null) throw new ProgrammingValidationException("Missing a victim effect");
            Validation.scope("Victim effects", victimEffect::validate, itemSet);
        }
        if (chance == null) throw new ProgrammingValidationException("No chance");
        if (originalDamageThreshold < 0f) throw new ValidationException("Original damage threshold can't be negative");
        if (finalDamageThreshold < 0f) throw new ValidationException("Final damage threshold can't be negative");
    }

    public void validateExportVersion(int mcVersion) throws ValidationException, ProgrammingValidationException {
        for (AttackEffectValues attackEffect : attackerEffects) {
            Validation.scope("Attacker effects", () -> attackEffect.validateExportVersion(mcVersion));
        }
        for (AttackEffectValues victimEffect : victimEffects) {
            Validation.scope("Victim effects", () -> victimEffect.validateExportVersion(mcVersion));
        }
    }
}
