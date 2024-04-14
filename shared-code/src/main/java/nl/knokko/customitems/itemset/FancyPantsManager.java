package nl.knokko.customitems.itemset;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.texture.FancyPantsArmorTexture;
import nl.knokko.customitems.texture.FancyPantsArmorTextureValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.CollectionHelper;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class FancyPantsManager extends ModelManager<
        FancyPantsArmorTexture, FancyPantsArmorTextureValues, FancyPantsArmorTextureReference> {

    protected FancyPantsManager(ItemSet itemSet) {
        super(itemSet);
    }

    @Override
    protected void saveElement(FancyPantsArmorTexture element, BitOutput output, ItemSet.Side targetSide) {
        element.getValues().save(output, targetSide);
    }

    @Override
    protected FancyPantsArmorTextureReference createReference(FancyPantsArmorTexture element) {
        return new FancyPantsArmorTextureReference(element);
    }

    @Override
    protected FancyPantsArmorTexture loadElement(BitInput input) throws UnknownEncodingException {
        return new FancyPantsArmorTexture(FancyPantsArmorTextureValues.load(input, itemSet.getSide()));
    }

    @Override
    protected void validateExportVersion(
            FancyPantsArmorTextureValues fpTexture, int mcVersion
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
    protected void validate(FancyPantsArmorTextureValues fpTexture) throws ValidationException, ProgrammingValidationException {
        Validation.scope(
                "FP texture " + fpTexture.getName(),
                () -> fpTexture.validate(itemSet, fpTexture.getId())
        );
    }

    @Override
    protected FancyPantsArmorTexture checkAndCreateElement(FancyPantsArmorTextureValues values) throws ValidationException, ProgrammingValidationException {
        values.validate(itemSet, null);
        return new FancyPantsArmorTexture(values);
    }

    @Override
    protected void validateChange(FancyPantsArmorTextureReference reference, FancyPantsArmorTextureValues newValues) throws ValidationException, ProgrammingValidationException {
        newValues.validate(itemSet, reference.get().getId());
    }

    public FancyPantsArmorTextureReference getReference(UUID id) throws NoSuchElementException {
        if (itemSet.finishedLoading) {
            return new FancyPantsArmorTextureReference(CollectionHelper.find(
                    elements, fpTexture -> fpTexture.getValues().getId(), id
            ).get());
        } else {
            return new FancyPantsArmorTextureReference(id, itemSet);
        }
    }

    public Optional<FancyPantsArmorTextureValues> get(UUID id) {
        return CollectionHelper.find(elements, fpTexture -> fpTexture.getValues().getId(), id).map(FancyPantsArmorTexture::getValues);
    }

    public int findFreeRgb() {
        int candidateRgb = 0;
        whileLoop:
        while (true) {
            for (FancyPantsArmorTextureValues existing : this) {
                if (existing.getRgb() == candidateRgb) {
                    candidateRgb += 1;
                    continue whileLoop;
                }
            }
            return candidateRgb;
        }
    }
}
