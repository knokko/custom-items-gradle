package nl.knokko.customitems.itemset;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.misc.CombinedResourcepack;
import nl.knokko.customitems.misc.CombinedResourcepackValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.CollectionHelper;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.Optional;

public class CombinedResourcepackManager extends ModelManager<
        CombinedResourcepack, CombinedResourcepackValues, CombinedResourcepackReference
        > {

    protected CombinedResourcepackManager(ItemSet itemSet) {
        super(itemSet);
    }

    @Override
    protected void saveElement(CombinedResourcepack element, BitOutput output, ItemSet.Side targetSide) {
        element.getValues().save(output);
    }

    @Override
    protected CombinedResourcepackReference createReference(CombinedResourcepack element) {
        return new CombinedResourcepackReference(element);
    }

    @Override
    public void load(BitInput input) throws UnknownEncodingException {
        if (itemSet.getSide() == ItemSet.Side.EDITOR) super.load(input);
    }

    @Override
    protected CombinedResourcepack loadElement(BitInput input) throws UnknownEncodingException {
        return new CombinedResourcepack(CombinedResourcepackValues.load(input));
    }

    @Override
    protected void validateExportVersion(
            CombinedResourcepackValues element, int mcVersion
    ) throws ValidationException, ProgrammingValidationException {}

    @Override
    public void validate() throws ValidationException, ProgrammingValidationException {
        super.validate();
        validateUniqueIDs("Combined resourcepack name", elements, pack -> pack.getValues().getName());
        validateUniqueIDs("Combined resourcepack priority", elements, pack -> pack.getValues().getPriority());
    }

    @Override
    protected void validate(CombinedResourcepackValues combinedPack) throws ValidationException, ProgrammingValidationException {
        Validation.scope(
                "Combined resourcepack " + combinedPack.getName(),
                () -> combinedPack.validate(itemSet, combinedPack.getName(), combinedPack.getPriority())
        );
    }

    @Override
    protected CombinedResourcepack checkAndCreateElement(CombinedResourcepackValues values) throws ValidationException, ProgrammingValidationException {
        values.validate(itemSet, null, null);
        return new CombinedResourcepack(values);
    }

    @Override
    protected void validateChange(CombinedResourcepackReference reference, CombinedResourcepackValues newValues) throws ValidationException, ProgrammingValidationException {
        newValues.validate(itemSet, reference.get().getName(), reference.get().getPriority());
    }

    public Optional<CombinedResourcepackValues> get(String name) {
        return CollectionHelper.find(elements, pack -> pack.getValues().getName(), name).map(CombinedResourcepack::getValues);
    }

    public void combine(CombinedResourcepackManager primary, CombinedResourcepackManager secondary) {
        elements.addAll(primary.elements);
        elements.addAll(secondary.elements);
    }
}
