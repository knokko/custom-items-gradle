package nl.knokko.customitems.item;

import nl.knokko.customitems.damage.VDamageSource;
import nl.knokko.customitems.itemset.DamageSourceReference;
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

import static nl.knokko.customitems.damage.VDamageSource.*;

public class DamageResistance extends ModelValues {

    private static DamageResistance load(BitInput input, int amount, ItemSet itemSet) throws UnknownEncodingException {
        DamageResistance result = new DamageResistance(false);

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

    private static void loadVanilla(BitInput input, DamageResistance result, int amount) {
        for (int ordinal = 0; ordinal < amount; ordinal++) {
            if (input.readBoolean()) {
                result.vanillaResistanceMap[ordinal] = input.readShort();
            }
        }
    }

    public static DamageResistance load12(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        return load(input, AMOUNT_12, itemSet);
    }

    public static DamageResistance load14(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        return load(input, AMOUNT_14, itemSet);
    }

    public static DamageResistance load17(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        return load(input, AMOUNT_17, itemSet);
    }

    public static DamageResistance loadNew(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        return load(input, input.readInt(), itemSet);
    }

    private final short[] vanillaResistanceMap;
    private final Map<DamageSourceReference, Short> customResistanceMap;

    public DamageResistance(boolean mutable) {
        super(mutable);

        this.vanillaResistanceMap = new short[VDamageSource.values().length];
        this.customResistanceMap = new HashMap<>();
    }

    public DamageResistance(DamageResistance toCopy, boolean mutable) {
        this(mutable);

        System.arraycopy(toCopy.vanillaResistanceMap, 0, this.vanillaResistanceMap, 0, this.vanillaResistanceMap.length);
        this.customResistanceMap.putAll(toCopy.customResistanceMap);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof DamageResistance) {
            DamageResistance otherResistances = (DamageResistance) other;
            return Arrays.equals(this.vanillaResistanceMap, otherResistances.vanillaResistanceMap) &&
                    this.customResistanceMap.equals(otherResistances.customResistanceMap);
        } else return false;
    }

    @Override
    public DamageResistance copy(boolean mutable) {
        return new DamageResistance(this, mutable);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("DamageResistances(");
        for (VDamageSource damageSource : VDamageSource.values()) {
            short resistance = this.vanillaResistanceMap[damageSource.ordinal()];
            if (resistance != 0) {
                result.append(damageSource).append(": ").append(resistance).append("%, ");
            }
        }
        result.append(")");
        return result.toString();
    }

    public short getResistance(VDamageSource damageSource) {
        return vanillaResistanceMap[damageSource.ordinal()];
    }

    public short getResistance(DamageSourceReference damageSource) {
        return customResistanceMap.getOrDefault(damageSource, (short) 0);
    }

    public void setResistance(VDamageSource damageSource, short newResistance) {
        assertMutable();
        this.vanillaResistanceMap[damageSource.ordinal()] = newResistance;
    }

    public void setResistance(DamageSourceReference damageSource, short newResistance) {
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
        for (DamageSourceReference damageSource : customResistanceMap.keySet()) {
            if (damageSource == null) throw new ProgrammingValidationException("Missing a custom damage source");
            if (!itemSet.damageSources.isValid(damageSource)) throw new ProgrammingValidationException("Damage source is invalid");
        }
        if (customResistanceMap.containsValue(null)) throw new ProgrammingValidationException("Contains null resistance");
        if (customResistanceMap.containsValue((short) 0)) throw new ProgrammingValidationException("Contains 0 resistance");
    }
}
