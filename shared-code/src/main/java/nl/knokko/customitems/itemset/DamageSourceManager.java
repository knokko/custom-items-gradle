package nl.knokko.customitems.itemset;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.damage.CustomDamageSourceValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.CollectionHelper;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class DamageSourceManager extends ModelManager<CustomDamageSourceValues, CustomDamageSourceReference> {

    protected DamageSourceManager(ItemSet itemSet) {
        super(itemSet);
    }

    @Override
    protected void saveElement(CustomDamageSourceValues element, BitOutput output, ItemSet.Side targetSide) {
        element.save(output);
    }

    @Override
    CustomDamageSourceReference createReference(Model<CustomDamageSourceValues> element) {
        return new CustomDamageSourceReference(element);
    }

    @Override
    protected CustomDamageSourceValues loadElement(BitInput input) throws UnknownEncodingException {
        return CustomDamageSourceValues.load(input);
    }

    @Override
    protected void validateExportVersion(
            CustomDamageSourceValues element, int mcVersion
    ) throws ValidationException, ProgrammingValidationException {}

    @Override
    public void validate() throws ValidationException, ProgrammingValidationException {
        super.validate();
        validateUniqueIDs("Damage sources", elements, damageSource -> damageSource.getValues().getId());
    }

    @Override
    protected void validateCreation(CustomDamageSourceValues values) throws ValidationException, ProgrammingValidationException {
        values.validateComplete(itemSet, null);
    }

    @Override
    protected void validate(CustomDamageSourceValues damageSource) throws ValidationException, ProgrammingValidationException {
        Validation.scope(
                "Damage source " + damageSource.getName(),
                () -> damageSource.validateComplete(itemSet, damageSource.getId())
        );
    }

    @Override
    protected void validateChange(CustomDamageSourceReference reference, CustomDamageSourceValues newValues) throws ValidationException, ProgrammingValidationException {
        newValues.validateComplete(itemSet, reference.get().getId());
    }

    public CustomDamageSourceReference getReference(UUID damageSourceID) throws NoSuchElementException {
        if (itemSet.finishedLoading) {
            return new CustomDamageSourceReference(CollectionHelper.find(elements, damageSource -> damageSource.getValues().getId(), damageSourceID).get());
        } else {
            return new CustomDamageSourceReference(damageSourceID, itemSet);
        }
    }

    public Optional<CustomDamageSourceValues> get(UUID damageSourceID) {
        return CollectionHelper.find(elements, damageSource -> damageSource.getValues().getId(), damageSourceID).map(Model::getValues);
    }
}
