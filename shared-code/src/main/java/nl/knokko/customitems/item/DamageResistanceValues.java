package nl.knokko.customitems.item;

import nl.knokko.customitems.damage.DamageSource;
import nl.knokko.customitems.itemset.CustomDamageSourceReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static nl.knokko.customitems.damage.DamageSource.*;

public class DamageResistanceValues extends ModelValues {

    private static DamageResistanceValues load(BitInput input, int amount, ItemSet itemSet) throws UnknownEncodingException {
        DamageResistanceValues result = new DamageResistanceValues(false);

        if (amount > 0) {
            loadVanilla(input, result, amount);
        } else {
            if (amount != -1) throw new UnknownEncodingException("DamageResistances", amount);

            int numVanillaResistances = input.readInt();
            loadVanilla(input, result, numVanillaResistances);
            int numCustomResistances = input.readInt();
            for (int counter = 0; counter < numCustomResistances; counter++) {
                result.customResistanceMap.put(itemSet.damageSources.getReference(new UUID(input.readLong(), input.readLong())), input.readShort());
            }
        }
        return result;
    }

    private static void loadVanilla(BitInput input, DamageResistanceValues result, int amount) {
        for (int ordinal = 0; ordinal < amount; ordinal++) {
            if (input.readBoolean()) {
                result.vanillaResistanceMap[ordinal] = input.readShort();
            }
        }
    }

    public static DamageResistanceValues load12(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        return load(input, AMOUNT_12, itemSet);
    }

    public static DamageResistanceValues load14(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        return load(input, AMOUNT_14, itemSet);
    }

    public static DamageResistanceValues load17(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        return load(input, AMOUNT_17, itemSet);
    }

    public static DamageResistanceValues loadNew(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        return load(input, input.readInt(), itemSet);
    }

    private final short[] vanillaResistanceMap;
    private final Map<CustomDamageSourceReference, Short> customResistanceMap;

    public DamageResistanceValues(boolean mutable) {
        super(mutable);

        this.vanillaResistanceMap = new short[DamageSource.values().length];
        this.customResistanceMap = new HashMap<>();
    }

    public DamageResistanceValues(DamageResistanceValues toCopy, boolean mutable) {
        this(mutable);

        System.arraycopy(toCopy.vanillaResistanceMap, 0, this.vanillaResistanceMap, 0, this.vanillaResistanceMap.length);
        this.customResistanceMap.putAll(toCopy.customResistanceMap);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof DamageResistanceValues) {
            DamageResistanceValues otherResistances = (DamageResistanceValues) other;
            return Arrays.equals(this.vanillaResistanceMap, otherResistances.vanillaResistanceMap) &&
                    this.customResistanceMap.equals(otherResistances.customResistanceMap);
        } else return false;
    }

    @Override
    public DamageResistanceValues copy(boolean mutable) {
        return new DamageResistanceValues(this, mutable);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("DamageResistances(");
        for (DamageSource damageSource : DamageSource.values()) {
            short resistance = this.vanillaResistanceMap[damageSource.ordinal()];
            if (resistance != 0) {
                result.append(damageSource).append(": ").append(resistance).append("%, ");
            }
        }
        result.append(")");
        return result.toString();
    }

    public short getResistance(DamageSource damageSource) {
        return vanillaResistanceMap[damageSource.ordinal()];
    }

    public short getResistance(CustomDamageSourceReference damageSource) {
        return customResistanceMap.getOrDefault(damageSource, (short) 0);
    }

    public void setResistance(DamageSource damageSource, short newResistance) {
        assertMutable();
        this.vanillaResistanceMap[damageSource.ordinal()] = newResistance;
    }

    public void setResistance(CustomDamageSourceReference damageSource, short newResistance) {
        if (newResistance == 0) customResistanceMap.remove(damageSource);
        else customResistanceMap.put(damageSource, newResistance);
    }

    private void saveVanilla(BitOutput output, int amount) {
        for (int index = 0; index < amount; index++) {
            short resistance = vanillaResistanceMap[index];
            if (resistance != 0) {
                output.addBoolean(true);
                output.addShort(resistance);
            } else {
                output.addBoolean(false);
            }
        }
    }

    public void saveNew(BitOutput output) {
        output.addInt(-1);
        output.addInt(vanillaResistanceMap.length);
        saveVanilla(output, vanillaResistanceMap.length);
        output.addInt(customResistanceMap.size());
        customResistanceMap.forEach((damageSource, resistance) -> {
            output.addLong(damageSource.get().getId().getMostSignificantBits());
            output.addLong(damageSource.get().getId().getLeastSignificantBits());
            output.addShort(resistance);
        });
    }

    public void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if (vanillaResistanceMap == null) throw new ProgrammingValidationException("No vanilla resistances");
        if (vanillaResistanceMap.length != values().length) {
            throw new ProgrammingValidationException("Wrong number of vanilla resistances");
        }
        if (customResistanceMap == null) throw new ProgrammingValidationException("No custom resistances");
        for (CustomDamageSourceReference damageSource : customResistanceMap.keySet()) {
            if (damageSource == null) throw new ProgrammingValidationException("Missing a custom damage source");
            if (!itemSet.damageSources.isValid(damageSource)) throw new ProgrammingValidationException("Damage source is invalid");
        }
        if (customResistanceMap.containsValue(null)) throw new ProgrammingValidationException("Contains null resistance");
        if (customResistanceMap.containsValue((short) 0)) throw new ProgrammingValidationException("Contains 0 resistance");
    }
}
