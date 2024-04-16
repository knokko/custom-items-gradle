package nl.knokko.customitems.itemset;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.item.equipment.EquipmentSetValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

public class EquipmentSetManager extends ModelManager<EquipmentSetValues, EquipmentSetReference> {

    protected EquipmentSetManager(ItemSet itemSet) {
        super(itemSet);
    }

    @Override
    protected void saveElement(EquipmentSetValues element, BitOutput output, ItemSet.Side targetSide) {
        element.save(output);
    }

    @Override
    EquipmentSetReference createReference(Model<EquipmentSetValues> element) {
        return new EquipmentSetReference(element);
    }

    @Override
    protected EquipmentSetValues loadElement(BitInput input) throws UnknownEncodingException {
        return EquipmentSetValues.load(input, itemSet);
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
    protected void validateCreation(EquipmentSetValues values) throws ValidationException, ProgrammingValidationException {
        values.validate(itemSet);
    }

    @Override
    protected void validateChange(EquipmentSetReference reference, EquipmentSetValues newValues) throws ValidationException, ProgrammingValidationException {
        newValues.validate(itemSet);
    }
}
