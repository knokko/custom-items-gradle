package nl.knokko.customitems.itemset;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.item.KciItem;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.CollectionHelper;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;

public class ItemManager extends ModelManager<KciItem, ItemReference> {

    protected ItemManager(ItemSet itemSet) {
        super(itemSet);
    }

    @Override
    protected void saveElement(KciItem item, BitOutput output, ItemSet.Side targetSide) {
        item.save(output, targetSide);
    }

    @Override
    ItemReference createReference(Model<KciItem> element) {
        return new ItemReference(element);
    }

    public void loadWithoutModel(BitInput input) throws UnknownEncodingException {
        int numItems = input.readInt();
        this.elements = new ArrayList<>(numItems);
        for (int counter = 0; counter < numItems; counter++) {
            this.elements.add(new Model<>(KciItem.load(input, itemSet, false)));
        }
    }

    @Override
    protected KciItem loadElement(BitInput input) throws UnknownEncodingException {
        return KciItem.load(input, itemSet, true);
    }

    @Override
    protected void validateExportVersion(KciItem item, int mcVersion) throws ValidationException, ProgrammingValidationException {
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
    protected void validateCreation(KciItem values) throws ValidationException, ProgrammingValidationException {
        values.validateComplete(itemSet, null);
    }

    @Override
    protected void validate(KciItem item) throws ValidationException, ProgrammingValidationException {
        Validation.scope(
                "Item " + item.getName(),
                () -> item.validateComplete(itemSet, item.getName())
        );
    }

    @Override
    protected void validateChange(ItemReference reference, KciItem newValues) throws ValidationException, ProgrammingValidationException {
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

    public Optional<KciItem> get(String itemName) {
        return CollectionHelper.find(elements, item -> item.getValues().getName(), itemName).map(Model::getValues);
    }
}
