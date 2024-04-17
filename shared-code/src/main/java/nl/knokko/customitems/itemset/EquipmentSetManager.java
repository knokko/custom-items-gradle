package nl.knokko.customitems.itemset;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.item.equipment.EquipmentSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

public class EquipmentSetManager extends ModelManager<EquipmentSet, EquipmentSetReference> {

    protected EquipmentSetManager(ItemSet itemSet) {
        super(itemSet);
    }

    @Override
    protected void saveElement(EquipmentSet element, BitOutput output, ItemSet.Side targetSide) {
        element.save(output);
    }

    @Override
    EquipmentSetReference createReference(Model<EquipmentSet> element) {
        return new EquipmentSetReference(element);
    }

    @Override
    protected EquipmentSet loadElement(BitInput input) throws UnknownEncodingException {
        return EquipmentSet.load(input, itemSet);
    }

    @Override
    protected void validateExportVersion(EquipmentSet equipmentSet, int mcVersion) throws ValidationException, ProgrammingValidationException {
        Validation.scope(
                "Equipment set " + equipmentSet,
                () -> equipmentSet.validateExportVersion(mcVersion)
        );
    }

    @Override
    protected void validate(EquipmentSet equipmentSet) throws ValidationException, ProgrammingValidationException {
        Validation.scope("Equipment set " + equipmentSet, equipmentSet::validate, itemSet);
    }

    @Override
    protected void validateCreation(EquipmentSet values) throws ValidationException, ProgrammingValidationException {
        values.validate(itemSet);
    }

    @Override
    protected void validateChange(EquipmentSetReference reference, EquipmentSet newValues) throws ValidationException, ProgrammingValidationException {
        newValues.validate(itemSet);
    }
}
