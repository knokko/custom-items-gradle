package nl.knokko.customitems.itemset;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.container.fuel.CustomFuelRegistry;
import nl.knokko.customitems.container.fuel.FuelRegistryValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.CollectionHelper;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.NoSuchElementException;
import java.util.Optional;

public class FuelRegistryManager extends ModelManager<CustomFuelRegistry, FuelRegistryValues, FuelRegistryReference> {

    protected FuelRegistryManager(ItemSet itemSet) {
        super(itemSet);
    }

    @Override
    protected void saveElement(CustomFuelRegistry element, BitOutput output, ItemSet.Side targetSide) {
        element.getValues().save(output);
    }

    @Override
    protected FuelRegistryReference createReference(CustomFuelRegistry element) {
        return new FuelRegistryReference(element);
    }

    @Override
    protected CustomFuelRegistry loadElement(BitInput input) throws UnknownEncodingException {
        return new CustomFuelRegistry(FuelRegistryValues.load(input, itemSet));
    }

    @Override
    protected void validateExportVersion(FuelRegistryValues fuelRegistry, int mcVersion) throws ValidationException, ProgrammingValidationException {
        Validation.scope(
                "Fuel registry " + fuelRegistry.getName(),
                () -> fuelRegistry.validateExportVersion(mcVersion)
        );
    }

    @Override
    public void validate() throws ValidationException, ProgrammingValidationException {
        super.validate();
        validateUniqueIDs("fuel registry name", elements, fuelRegistry -> fuelRegistry.getValues().getName());
    }

    @Override
    protected void validate(FuelRegistryValues fuelRegistry) throws ValidationException, ProgrammingValidationException {
        Validation.scope(
                "Fuel registry " + fuelRegistry.getName(),
                () -> fuelRegistry.validate(itemSet, fuelRegistry.getName())
        );
    }

    @Override
    protected CustomFuelRegistry checkAndCreateElement(FuelRegistryValues values) throws ValidationException, ProgrammingValidationException {
        values.validate(itemSet, null);
        return new CustomFuelRegistry(values);
    }

    @Override
    protected void validateChange(FuelRegistryReference reference, FuelRegistryValues newValues) throws ValidationException, ProgrammingValidationException {
        newValues.validate(itemSet, reference.get().getName());
    }

    public FuelRegistryReference getReference(String name) throws NoSuchElementException {
        if (itemSet.finishedLoading) {
            return new FuelRegistryReference(CollectionHelper.find(elements, registry -> registry.getValues().getName(), name).get());
        } else {
            return new FuelRegistryReference(name, itemSet);
        }
    }

    public Optional<FuelRegistryValues> get(String name) {
        return CollectionHelper.find(elements, registry -> registry.getValues().getName(), name).map(CustomFuelRegistry::getValues);
    }
}
