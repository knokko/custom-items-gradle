package nl.knokko.customitems.itemset;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.drops.MobDropValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

public class MobDropManager extends ModelManager<MobDropValues, MobDropReference> {

    protected MobDropManager(ItemSet itemSet) {
        super(itemSet);
    }

    @Override
    protected void saveElement(MobDropValues mobDrop, BitOutput output, ItemSet.Side targetSide) {
        mobDrop.save(output);
    }

    @Override
    MobDropReference createReference(Model<MobDropValues> element) {
        return new MobDropReference(element);
    }

    @Override
    protected MobDropValues loadElement(BitInput input) throws UnknownEncodingException {
        return MobDropValues.load(input, itemSet);
    }

    @Override
    protected void validateExportVersion(MobDropValues mobDrop, int mcVersion) throws ValidationException, ProgrammingValidationException {
        Validation.scope(
                "Mob drop for " + mobDrop.getEntityType(),
                () -> mobDrop.validateExportVersion(mcVersion)
        );
    }

    @Override
    protected void validate(MobDropValues mobDrop) throws ValidationException, ProgrammingValidationException {
        Validation.scope(
                "Mob drop for " + mobDrop.getEntityType(),
                () -> mobDrop.validate(itemSet)
        );
    }

    @Override
    protected void validateCreation(MobDropValues values) throws ValidationException, ProgrammingValidationException {
        values.validate(itemSet);
    }

    @Override
    protected void validateChange(MobDropReference reference, MobDropValues newValues) throws ValidationException, ProgrammingValidationException {
        newValues.validate(itemSet);
    }
}
