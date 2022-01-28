package nl.knokko.customitems.item;

import nl.knokko.customitems.damage.DamageSource;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.Arrays;

import static nl.knokko.customitems.damage.DamageSource.*;

public class DamageResistanceValues extends ModelValues {

    private static DamageResistanceValues load(BitInput input, int amount) {
        DamageResistanceValues result = new DamageResistanceValues(false);
        for (int ordinal = 0; ordinal < amount; ordinal++) {
            if (input.readBoolean()) {
                result.resistanceMap[ordinal] = input.readShort();
            }
        }
        return result;
    }

    public static DamageResistanceValues load12(BitInput input) {
        return load(input, AMOUNT_12);
    }

    public static DamageResistanceValues load14(BitInput input) {
        return load(input, AMOUNT_14);
    }

    public static DamageResistanceValues load17(BitInput input) {
        return load(input, AMOUNT_17);
    }

    private final short[] resistanceMap;

    public DamageResistanceValues(boolean mutable) {
        super(mutable);

        this.resistanceMap = new short[DamageSource.values().length];
    }

    public DamageResistanceValues(DamageResistanceValues toCopy, boolean mutable) {
        this(mutable);

        System.arraycopy(toCopy.resistanceMap, 0, this.resistanceMap, 0, this.resistanceMap.length);
    }

    @Override
    public boolean equals(Object other) {
        return other.getClass() == DamageResistanceValues.class && Arrays.equals(this.resistanceMap, ((DamageResistanceValues) other).resistanceMap);
    }

    @Override
    public DamageResistanceValues copy(boolean mutable) {
        return new DamageResistanceValues(this, mutable);
    }

    public short getResistance(DamageSource damageSource) {
        return resistanceMap[damageSource.ordinal()];
    }

    public void setResistance(DamageSource damageSource, short newResistance) {
        assertMutable();
        this.resistanceMap[damageSource.ordinal()] = newResistance;
    }

    private void save(BitOutput output, int amount) {
        for (int index = 0; index < amount; index++) {
            short resistance = resistanceMap[index];
            if (resistance != 0) {
                output.addBoolean(true);
                output.addShort(resistance);
            } else {
                output.addBoolean(false);
            }
        }
    }

    public void save12(BitOutput output) {
        save(output, AMOUNT_12);
    }

    public void save14(BitOutput output) {
        save(output, AMOUNT_14);
    }

    public void save17(BitOutput output) {
        save(output, AMOUNT_17);
    }
}
