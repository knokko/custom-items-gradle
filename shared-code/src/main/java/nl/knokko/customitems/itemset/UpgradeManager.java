package nl.knokko.customitems.itemset;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.recipe.upgrade.Upgrade;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.CollectionHelper;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class UpgradeManager extends ModelManager<Upgrade, UpgradeReference> {

    protected UpgradeManager(ItemSet itemSet) {
        super(itemSet);
    }

    @Override
    protected void saveElement(Upgrade upgrade, BitOutput output, ItemSet.Side targetSide) {
        upgrade.save(output);
    }

    @Override
    UpgradeReference createReference(Model<Upgrade> element) {
        return new UpgradeReference(element);
    }

    @Override
    protected Upgrade loadElement(BitInput input) throws UnknownEncodingException {
        return Upgrade.load(input, itemSet);
    }

    @Override
    protected void validateExportVersion(Upgrade upgrade, int mcVersion) throws ValidationException, ProgrammingValidationException {
        Validation.scope("Upgrade " + upgrade.getName(), upgrade::validateExportVersion, mcVersion);
    }

    @Override
    public void validate() throws ValidationException, ProgrammingValidationException {
        super.validate();
        validateUniqueIDs("upgrade id", elements, upgrade -> upgrade.getValues().getId());
    }

    @Override
    protected void validateCreation(Upgrade values) throws ValidationException, ProgrammingValidationException {
        values.validateComplete(itemSet, null);
    }

    @Override
    protected void validate(Upgrade upgrade) throws ValidationException, ProgrammingValidationException {
        Validation.scope(
                "Upgrade " + upgrade.getName(),
                () -> upgrade.validateComplete(itemSet, upgrade.getId())
        );
    }

    @Override
    protected void validateChange(UpgradeReference reference, Upgrade newValues) throws ValidationException, ProgrammingValidationException {
        newValues.validateComplete(itemSet, reference.get().getId());
    }

    public UpgradeReference getReference(UUID id) throws NoSuchElementException {
        if (itemSet.finishedLoading) {
            return new UpgradeReference(CollectionHelper.find(elements, upgrade -> upgrade.getValues().getId(), id).get());
        } else {
            return new UpgradeReference(id, itemSet);
        }
    }

    public Optional<Upgrade> get(UUID id) {
        return CollectionHelper.find(elements, upgrade -> upgrade.getValues().getId(), id).map(Model::getValues);
    }
}
