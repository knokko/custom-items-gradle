package nl.knokko.customitems.itemset;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.sound.CustomSoundType;
import nl.knokko.customitems.sound.CustomSoundTypeValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.CollectionHelper;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class SoundTypeManager extends ModelManager<CustomSoundType, CustomSoundTypeValues, SoundTypeReference> {

    protected SoundTypeManager(ItemSet itemSet) {
        super(itemSet);
    }

    @Override
    protected void saveElement(CustomSoundType soundType, BitOutput output, ItemSet.Side targetSide) {
        soundType.getValues().save(output, targetSide);
    }

    @Override
    protected SoundTypeReference createReference(CustomSoundType element) {
        return new SoundTypeReference(element);
    }

    @Override
    protected CustomSoundType loadElement(BitInput input) throws UnknownEncodingException {
        return new CustomSoundType(CustomSoundTypeValues.load(input, itemSet));
    }

    @Override
    protected void validateExportVersion(
            CustomSoundTypeValues element, int mcVersion
    ) throws ValidationException, ProgrammingValidationException {}

    @Override
    public void validate() throws ValidationException, ProgrammingValidationException {
        super.validate();
        validateUniqueIDs("sound type id", elements, soundType -> soundType.getValues().getId());
        validateUniqueIDs("sound type name", elements, soundType -> soundType.getValues().getName());
    }

    @Override
    protected void validate(CustomSoundTypeValues soundType) throws ValidationException, ProgrammingValidationException {
        Validation.scope(
                "Sound type " + soundType.getName(),
                () -> soundType.validate(itemSet, soundType.getId())
        );
    }

    @Override
    protected CustomSoundType checkAndCreateElement(CustomSoundTypeValues values) throws ValidationException, ProgrammingValidationException {
        values.validate(itemSet, null);
        return new CustomSoundType(values);
    }

    @Override
    protected void validateChange(SoundTypeReference reference, CustomSoundTypeValues newValues) throws ValidationException, ProgrammingValidationException {
        newValues.validate(itemSet, reference.get().getId());
    }

    public SoundTypeReference getReference(UUID id) throws NoSuchElementException {
        if (itemSet.finishedLoading) {
            return new SoundTypeReference(CollectionHelper.find(elements, soundType -> soundType.getValues().getId(), id).get());
        } else {
            return new SoundTypeReference(id, itemSet);
        }
    }

    public Optional<CustomSoundTypeValues> get(UUID id) {
        return CollectionHelper.find(elements, soundType -> soundType.getValues().getId(), id).map(CustomSoundType::getValues);
    }
}
