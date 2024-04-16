package nl.knokko.customitems.itemset;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.CollectionHelper;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;

public class ItemManager extends ModelManager<CustomItemValues, ItemReference> {

    protected ItemManager(ItemSet itemSet) {
        super(itemSet);
    }

    @Override
    protected void saveElement(CustomItemValues item, BitOutput output, ItemSet.Side targetSide) {
        item.save(output, targetSide);
    }

    @Override
    ItemReference createReference(Model<CustomItemValues> element) {
        return new ItemReference(element);
    }

    public void loadWithoutModel(BitInput input) throws UnknownEncodingException {
        int numItems = input.readInt();
        this.elements = new ArrayList<>(numItems);
        for (int counter = 0; counter < numItems; counter++) {
            this.elements.add(new Model<>(CustomItemValues.load(input, itemSet, false)));
        }
    }

    @Override
    protected CustomItemValues loadElement(BitInput input) throws UnknownEncodingException {
        return CustomItemValues.load(input, itemSet, true);
    }

    @Override
    protected void validateExportVersion(CustomItemValues item, int mcVersion) throws ValidationException, ProgrammingValidationException {
        Validation.scope(
                "Item " + item.getName(),
                () -> item.validateExportVersion(mcVersion)
        );
    }

    @Override
    public void validate() throws ValidationException, ProgrammingValidationException {
        super.validate();
        validateUniqueIDs("item name", elements, item -> item.getValues().getName());
    }

    @Override
    protected void validateCreation(CustomItemValues values) throws ValidationException, ProgrammingValidationException {
        values.validateComplete(itemSet, null);
    }

    @Override
    protected void validate(CustomItemValues item) throws ValidationException, ProgrammingValidationException {
        Validation.scope(
                "Item " + item.getName(),
                () -> item.validateComplete(itemSet, item.getName())
        );
    }

    @Override
    protected void validateChange(ItemReference reference, CustomItemValues newValues) throws ValidationException, ProgrammingValidationException {
        newValues.validateComplete(itemSet, reference.get().getName());
    }

    @Override
    public void remove(ItemReference reference) throws ValidationException, ProgrammingValidationException {
        String name = reference.get().getName();
        super.remove(reference);
        itemSet.removedItemNames.add(name);
    }

    public ItemReference getReference(String name) throws NoSuchElementException {
        if (itemSet.finishedLoading) {
            return new ItemReference(CollectionHelper.find(elements, item -> item.getValues().getName(), name).get());
        } else {
            return new ItemReference(name, itemSet);
        }
    }

    public Optional<CustomItemValues> get(String itemName) {
        return CollectionHelper.find(elements, item -> item.getValues().getName(), itemName).map(Model::getValues);
    }
}
