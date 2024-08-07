package nl.knokko.customitems.itemset;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.container.energy.EnergyType;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.CollectionHelper;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class EnergyTypeManager extends ModelManager<EnergyType, EnergyTypeReference> {

    protected EnergyTypeManager(ItemSet itemSet) {
        super(itemSet);
    }

    @Override
    protected void saveElement(EnergyType element, BitOutput output, ItemSet.Side targetSide) {
        element.save(output);
    }

    @Override
    EnergyTypeReference createReference(Model<EnergyType> element) {
        return new EnergyTypeReference(element);
    }

    @Override
    protected EnergyType loadElement(BitInput input) throws UnknownEncodingException {
        return EnergyType.load(input);
    }

    @Override
    protected void validateExportVersion(
            EnergyType element, int mcVersion
    ) throws ValidationException, ProgrammingValidationException {}

    @Override
    public void validate() throws ValidationException, ProgrammingValidationException {
        super.validate();
        validateUniqueIDs("energy type id", elements, energyType -> energyType.getValues().getId());
        validateUniqueIDs("energy type name", elements, energyType -> energyType.getValues().getName());
    }

    @Override
    protected void validateCreation(EnergyType values) throws ValidationException, ProgrammingValidationException {
        values.validateComplete(itemSet, null);
    }

    @Override
    protected void validate(EnergyType energyType) throws ValidationException, ProgrammingValidationException {
        Validation.scope(
                "Energy type " + energyType.getName(),
                () -> energyType.validateComplete(itemSet, energyType.getId())
        );
    }

    @Override
    protected void validateChange(EnergyTypeReference reference, EnergyType newValues) throws ValidationException, ProgrammingValidationException {
        newValues.validateComplete(itemSet, reference.get().getId());
    }

    public EnergyTypeReference getReference(UUID id) throws NoSuchElementException {
        if (itemSet.finishedLoading) {
            return new EnergyTypeReference(CollectionHelper.find(elements, energyType -> energyType.getValues().getId(), id).get());
        } else {
            return new EnergyTypeReference(id, itemSet);
        }
    }

    public Optional<EnergyType> get(UUID id) {
        return CollectionHelper.find(elements, energyType -> energyType.getValues().getId(), id).map(Model::getValues);
    }
}
