package nl.knokko.customitems.itemset;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.recipe.KciSmithingRecipe;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.CollectionHelper;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.Optional;
import java.util.UUID;

public class SmithingRecipeManager extends ModelManager<KciSmithingRecipe, SmithingRecipeReference> {

    SmithingRecipeManager(ItemSet itemSet) {
        super(itemSet);
    }

    @Override
    protected void saveElement(KciSmithingRecipe element, BitOutput output, ItemSet.Side targetSide) {
        element.save(output);
    }

    @Override
    SmithingRecipeReference createReference(Model<KciSmithingRecipe> element) {
        return new SmithingRecipeReference(element);
    }

    @Override
    protected KciSmithingRecipe loadElement(BitInput input) throws UnknownEncodingException {
        return KciSmithingRecipe.load(input, itemSet);
    }

    @Override
    protected void validateExportVersion(KciSmithingRecipe element, int mcVersion) throws ValidationException, ProgrammingValidationException {
        element.validateExportVersion(mcVersion);
    }

    @Override
    protected void validate(KciSmithingRecipe element) throws ValidationException, ProgrammingValidationException {
        Validation.scope(element.toString(), () -> element.validate(itemSet, element.getId()));
    }

    @Override
    protected void validateCreation(KciSmithingRecipe values) throws ValidationException, ProgrammingValidationException {
        values.validate(itemSet, null);
    }

    @Override
    protected void validateChange(SmithingRecipeReference reference, KciSmithingRecipe newValues) throws ValidationException, ProgrammingValidationException {
        newValues.validate(itemSet, reference.get().getId());
    }

    public Optional<KciSmithingRecipe> get(UUID id) {
        return CollectionHelper.find(elements, recipe -> recipe.getValues().getId(), id).map(Model::getValues);
    }
}
