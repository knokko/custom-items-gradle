package nl.knokko.customitems.attack.effect;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

import java.util.Objects;

public class AttackIgniteValues extends AttackEffectValues {

    static AttackIgniteValues loadOwn(BitInput input) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("AttackIgnite", encoding);

        AttackIgniteValues result = new AttackIgniteValues(false);
        result.duration = input.readInt();
        return result;
    }

    public static AttackIgniteValues createQuick(int duration) {
        AttackIgniteValues result = new AttackIgniteValues(true);
        result.setDuration(duration);
        return result;
    }

    private int duration;

    public AttackIgniteValues(boolean mutable) {
        super(mutable);
        this.duration = 100;
    }

    public AttackIgniteValues(AttackIgniteValues toCopy, boolean mutable) {
        super(mutable);
        this.duration = toCopy.getDuration();
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(ENCODING_IGNITE);
        output.addByte((byte) 1);

        output.addInt(duration);
    }

    @Override
    public AttackIgniteValues copy(boolean mutable) {
        return new AttackIgniteValues(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof AttackIgniteValues && this.duration == ((AttackIgniteValues) other).duration;
    }

    @Override
    public String toString() {
        return "AttackIgnite(" + duration + ")";
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        assertMutable();
        this.duration = duration;
    }

    @Override
    public void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if (duration <= 0) throw new ValidationException("Duration must be positive");
    }

    @Override
    public void validateExportVersion(int mcVersion) throws ValidationException, ProgrammingValidationException {
        // This effect works in all supported minecraft versions
    }
}
