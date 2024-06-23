package nl.knokko.customitems.item.equipment;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.item.KciAttributeModifier;
import nl.knokko.customitems.item.DamageResistance;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.model.Mutability;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.ArrayList;
import java.util.Collection;

public class EquipmentSetBonus extends ModelValues {

    public static EquipmentSetBonus load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("EquipmentBonus", encoding);

        EquipmentSetBonus result = new EquipmentSetBonus(false);
        result.minValue = input.readInt();
        result.maxValue = input.readInt();

        int numAttributeModifiers = input.readInt();
        result.attributeModifiers = new ArrayList<>(numAttributeModifiers);
        for (int counter = 0; counter < numAttributeModifiers; counter++) {
            result.attributeModifiers.add(KciAttributeModifier.load1(input, false));
        }

        result.damageResistances = DamageResistance.loadNew(input, itemSet);

        return result;
    }

    private int minValue, maxValue;
    private Collection<KciAttributeModifier> attributeModifiers;
    private DamageResistance damageResistances;

    public EquipmentSetBonus(boolean mutable) {
        super(mutable);
        this.minValue = 2;
        this.maxValue = 4;
        this.attributeModifiers = new ArrayList<>();
        this.damageResistances = new DamageResistance(false);
    }

    public EquipmentSetBonus(EquipmentSetBonus toCopy, boolean mutable) {
        super(mutable);
        this.minValue = toCopy.getMinValue();
        this.maxValue = toCopy.getMaxValue();
        this.attributeModifiers = toCopy.getAttributeModifiers();
        this.damageResistances = toCopy.getDamageResistances();
    }

    public int getMinValue() {
        return minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public Collection<KciAttributeModifier> getAttributeModifiers() {
        return new ArrayList<>(attributeModifiers);
    }

    public DamageResistance getDamageResistances() {
        return damageResistances;
    }

    public void setMinValue(int minValue) {
        assertMutable();
        this.minValue = minValue;
    }

    public void setMaxValue(int maxValue) {
        assertMutable();
        this.maxValue = maxValue;
    }

    public void setAttributeModifiers(Collection<KciAttributeModifier> attributeModifiers) {
        assertMutable();
        Checks.nonNull(attributeModifiers);
        this.attributeModifiers = Mutability.createDeepCopy(attributeModifiers, false);
    }

    public void setDamageResistances(DamageResistance damageResistances) {
        assertMutable();
        this.damageResistances = damageResistances.copy(false);
    }

    @Override
    public EquipmentSetBonus copy(boolean mutable) {
        return new EquipmentSetBonus(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof EquipmentSetBonus) {
            EquipmentSetBonus otherBonus = (EquipmentSetBonus) other;
            return this.minValue == otherBonus.minValue && this.maxValue == otherBonus.maxValue &&
                    this.attributeModifiers.equals(otherBonus.attributeModifiers) &&
                    this.damageResistances.equals(otherBonus.damageResistances);
        } else {
            return false;
        }
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);

        output.addInt(minValue);
        output.addInt(maxValue);

        output.addInt(attributeModifiers.size());
        for (KciAttributeModifier modifier : attributeModifiers) {
            modifier.save1(output);
        }

        damageResistances.saveNew(output);
    }

    public void validate() throws ValidationException, ProgrammingValidationException {
        if (minValue > maxValue) {
            throw new ValidationException("Minimum value can't be larger than maximum value");
        }
        if (minValue <= 0 && maxValue >= 0) {
            throw new ValidationException("0 must NOT be between the minimum value and the maximum value");
        }

        if (attributeModifiers == null) {
            throw new ProgrammingValidationException("No attribute modifiers");
        }
        for (KciAttributeModifier modifier : attributeModifiers) {
            if (modifier == null) throw new ProgrammingValidationException("Missing an attribute modifier");
            Validation.scope("Attribute modifier", modifier::validate);
        }

        if (damageResistances == null) {
            throw new ProgrammingValidationException("No damage resistances");
        }
    }
}
