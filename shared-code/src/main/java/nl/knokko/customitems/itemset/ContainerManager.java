package nl.knokko.customitems.itemset;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.container.CustomContainer;
import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.CollectionHelper;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.NoSuchElementException;
import java.util.Optional;

public class ContainerManager extends ModelManager<CustomContainer, CustomContainerValues, ContainerReference> {

    protected ContainerManager(ItemSet itemSet) {
        super(itemSet);
    }

    @Override
    protected void saveElement(CustomContainer element, BitOutput output, ItemSet.Side targetSide) {
        element.getValues().save(output);
    }

    @Override
    protected ContainerReference createReference(CustomContainer element) {
        return new ContainerReference(element);
    }

    @Override
    protected CustomContainer loadElement(BitInput input) throws UnknownEncodingException {
        return new CustomContainer(CustomContainerValues.load(input, itemSet));
    }

    @Override
    protected void validateExportVersion(CustomContainerValues container, int mcVersion) throws ValidationException, ProgrammingValidationException {
        Validation.scope(
                "Container " + container.getName(),
                () -> container.validateExportVersion(mcVersion)
        );
    }

    @Override
    public void validate() throws ValidationException, ProgrammingValidationException {
        super.validate();
        validateUniqueIDs("container name", elements, container -> container.getValues().getName());
    }

    @Override
    protected void validate(CustomContainerValues container) throws ValidationException, ProgrammingValidationException {
        Validation.scope(
                "Container " + container.getName(),
                () -> container.validate(itemSet, container.getName())
        );
    }

    @Override
    protected CustomContainer checkAndCreateElement(CustomContainerValues values) throws ValidationException, ProgrammingValidationException {
        values.validate(itemSet, null);
        return new CustomContainer(values);
    }

    @Override
    protected void validateChange(ContainerReference reference, CustomContainerValues newValues) throws ValidationException, ProgrammingValidationException {
        newValues.validate(itemSet, reference.get().getName());
    }

    public ContainerReference getReference(String containerName) throws NoSuchElementException {
        if (itemSet.finishedLoading) {
            return new ContainerReference(CollectionHelper.find(elements, container -> container.getValues().getName(), containerName).get());
        } else {
            return new ContainerReference(containerName, itemSet);
        }
    }

    public Optional<CustomContainerValues> get(String containerName) {
        return CollectionHelper.find(elements, container -> container.getValues().getName(), containerName).map(CustomContainer::getValues);
    }
}
