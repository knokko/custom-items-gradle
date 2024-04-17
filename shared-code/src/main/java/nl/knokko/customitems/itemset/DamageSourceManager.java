package nl.knokko.customitems.itemset;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.damage.KciDamageSource;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.CollectionHelper;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class DamageSourceManager extends ModelManager<KciDamageSource, DamageSourceReference> {

    protected DamageSourceManager(ItemSet itemSet) {
        super(itemSet);
    }

    @Override
    protected void saveElement(KciDamageSource element, BitOutput output, ItemSet.Side targetSide) {
        element.save(output);
    }

    @Override
    DamageSourceReference createReference(Model<KciDamageSource> element) {
        return new DamageSourceReference(element);
    }

    @Override
    protected KciDamageSource loadElement(BitInput input) throws UnknownEncodingException {
        return KciDamageSource.load(input);
    }

    @Override
    protected void validateExportVersion(
            KciDamageSource element, int mcVersion
    ) throws ValidationException, ProgrammingValidationException {}

    @Override
    public void validate() throws ValidationException, ProgrammingValidationException {
        super.validate();
        validateUniqueIDs("Damage sources", elements, damageSource -> damageSource.getValues().getId());
    }

    @Override
    protected void validateCreation(KciDamageSource values) throws ValidationException, ProgrammingValidationException {
        values.validateComplete(itemSet, null);
    }

    @Override
    protected void validate(KciDamageSource damageSource) throws ValidationException, ProgrammingValidationException {
        Validation.scope(
                "Damage source " + damageSource.getName(),
                () -> damageSource.validateComplete(itemSet, damageSource.getId())
        );
    }

    @Override
    protected void validateChange(DamageSourceReference reference, KciDamageSource newValues) throws ValidationException, ProgrammingValidationException {
        newValues.validateComplete(itemSet, reference.get().getId());
    }

    public DamageSourceReference getReference(UUID damageSourceID) throws NoSuchElementException {
        if (itemSet.finishedLoading) {
            return new DamageSourceReference(CollectionHelper.find(elements, damageSource -> damageSource.getValues().getId(), damageSourceID).get());
        } else {
            return new DamageSourceReference(damageSourceID, itemSet);
        }
    }

    public Optional<KciDamageSource> get(UUID damageSourceID) {
        return CollectionHelper.find(elements, damageSource -> damageSource.getValues().getId(), damageSourceID).map(Model::getValues);
    }
}
