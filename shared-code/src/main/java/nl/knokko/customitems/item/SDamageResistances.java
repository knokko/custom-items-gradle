package nl.knokko.customitems.item;

import nl.knokko.customitems.damage.DamageSource;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import static nl.knokko.customitems.damage.DamageSource.*;

public class SDamageResistances extends ModelValues {

    private static SDamageResistances load(BitInput input, int amount) {
        SDamageResistances result = new SDamageResistances(false);
        for (int ordinal = 0; ordinal < amount; ordinal++) {
            if (input.readBoolean()) {
                result.resistanceMap[ordinal] = input.readShort();
            }
        }
        return result;
    }

    public static SDamageResistances load12(BitInput input) {
        return load(input, AMOUNT_12);
    }

    public static SDamageResistances load14(BitInput input) {
        return load(input, AMOUNT_14);
    }

    public static SDamageResistances load17(BitInput input) {
        return load(input, AMOUNT_17);
    }

    private final short[] resistanceMap;

    public SDamageResistances(boolean mutable) {
        super(mutable);

        this.resistanceMap = new short[DamageSource.values().length];
    }

    public SDamageResistances(SDamageResistances toCopy, boolean mutable) {
        this(mutable);

        System.arraycopy(toCopy.resistanceMap, 0, this.resistanceMap, 0, this.resistanceMap.length);
    }

    @Override
    public SDamageResistances copy(boolean mutable) {
        return new SDamageResistances(this, mutable);
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
