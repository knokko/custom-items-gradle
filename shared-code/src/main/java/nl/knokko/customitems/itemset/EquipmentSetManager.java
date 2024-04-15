package nl.knokko.customitems.itemset;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.item.equipment.EquipmentSet;
import nl.knokko.customitems.item.equipment.EquipmentSetValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

public class EquipmentSetManager extends ModelManager<EquipmentSet, EquipmentSetValues, EquipmentSetReference> {

    protected EquipmentSetManager(ItemSet itemSet) {
        super(itemSet);
    }

    @Override
    protected void saveElement(EquipmentSet element, BitOutput output, ItemSet.Side targetSide) {
        element.getValues().save(output);
    }

    @Override
    protected EquipmentSetReference createReference(EquipmentSet element) {
        return new EquipmentSetReference(element);
    }

    @Override
    protected EquipmentSet loadElement(BitInput input) throws UnknownEncodingException {
        return new EquipmentSet(EquipmentSetValues.load(input, itemSet));
    }

    @Override
    protected void validateExportVersion(EquipmentSetValues equipmentSet, int mcVersion) throws ValidationException, ProgrammingValidationException {
        Validation.scope(
                "Equipment set " + equipmentSet,
                () -> equipmentSet.validateExportVersion(mcVersion)
        );
    }

    @Override
    protected void validate(EquipmentSetValues equipmentSet) throws ValidationException, ProgrammingValidationException {
        Validation.scope("Equipment set " + equipmentSet, equipmentSet::validate, itemSet);
    }

    @Override
    protected EquipmentSet checkAndCreateElement(EquipmentSetValues values) throws ValidationException, ProgrammingValidationException {
        values.validate(itemSet);
        return new EquipmentSet(values);
    }

    @Override
    protected void validateChange(EquipmentSetReference reference, EquipmentSetValues newValues) throws ValidationException, ProgrammingValidationException {
        newValues.validate(itemSet);
    }
}
