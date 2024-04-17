package nl.knokko.customitems.itemset;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.texture.FancyPantsTexture;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.CollectionHelper;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class FancyPantsManager extends ModelManager<FancyPantsTexture, FancyPantsReference> {

    protected FancyPantsManager(ItemSet itemSet) {
        super(itemSet);
    }

    @Override
    protected void saveElement(FancyPantsTexture element, BitOutput output, ItemSet.Side targetSide) {
        element.save(output, targetSide);
    }

    @Override
    FancyPantsReference createReference(Model<FancyPantsTexture> element) {
        return new FancyPantsReference(element);
    }

    @Override
    protected FancyPantsTexture loadElement(BitInput input) throws UnknownEncodingException {
        return FancyPantsTexture.load(input, itemSet.getSide());
    }

    @Override
    protected void validateExportVersion(
            FancyPantsTexture fpTexture, int mcVersion
    ) throws ValidationException, ProgrammingValidationException {
        Validation.scope("FP texture " + fpTexture.getName(), fpTexture::validateExportVersion, mcVersion);
    }

    @Override
    public void validate() throws ValidationException, ProgrammingValidationException {
        super.validate();
        validateUniqueIDs("FP texture ID", elements, fpTexture -> fpTexture.getValues().getId());
        validateUniqueIDs("FP texture name", elements, fpTexture -> fpTexture.getValues().getName());
        validateUniqueIDs("FP texture RGB value", elements, fpTexture -> fpTexture.getValues().getRgb());
    }

    @Override
    protected void validateCreation(FancyPantsTexture values) throws ValidationException, ProgrammingValidationException {
        values.validate(itemSet, null);
    }

    @Override
    protected void validate(FancyPantsTexture fpTexture) throws ValidationException, ProgrammingValidationException {
        Validation.scope(
                "FP texture " + fpTexture.getName(),
                () -> fpTexture.validate(itemSet, fpTexture.getId())
        );
    }

    @Override
    protected void validateChange(FancyPantsReference reference, FancyPantsTexture newValues) throws ValidationException, ProgrammingValidationException {
        newValues.validate(itemSet, reference.get().getId());
    }

    public FancyPantsReference getReference(UUID id) throws NoSuchElementException {
        if (itemSet.finishedLoading) {
            return new FancyPantsReference(CollectionHelper.find(
                    elements, fpTexture -> fpTexture.getValues().getId(), id
            ).get());
        } else {
            return new FancyPantsReference(id, itemSet);
        }
    }

    public Optional<FancyPantsTexture> get(UUID id) {
        return CollectionHelper.find(elements, fpTexture -> fpTexture.getValues().getId(), id).map(Model::getValues);
    }

    public int findFreeRgb() {
        int candidateRgb = 0;
        whileLoop:
        while (true) {
            for (FancyPantsTexture existing : this) {
                if (existing.getRgb() == candidateRgb) {
                    candidateRgb += 1;
                    continue whileLoop;
                }
            }
            return candidateRgb;
        }
    }
}
