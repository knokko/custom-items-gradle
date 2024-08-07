package nl.knokko.customitems.itemset;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.sound.KciSoundType;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.CollectionHelper;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class SoundTypeManager extends ModelManager<KciSoundType, SoundTypeReference> {

    protected SoundTypeManager(ItemSet itemSet) {
        super(itemSet);
    }

    @Override
    protected void saveElement(KciSoundType soundType, BitOutput output, ItemSet.Side targetSide) {
        soundType.save(output, targetSide);
    }

    @Override
    SoundTypeReference createReference(Model<KciSoundType> element) {
        return new SoundTypeReference(element);
    }

    @Override
    protected KciSoundType loadElement(BitInput input) throws UnknownEncodingException {
        return KciSoundType.load(input, itemSet);
    }

    @Override
    protected void validateExportVersion(
            KciSoundType element, int mcVersion
    ) throws ValidationException, ProgrammingValidationException {}

    @Override
    public void validate() throws ValidationException, ProgrammingValidationException {
        super.validate();
        validateUniqueIDs("sound type id", elements, soundType -> soundType.getValues().getId());
        validateUniqueIDs("sound type name", elements, soundType -> soundType.getValues().getName());
    }

    @Override
    protected void validateCreation(KciSoundType values) throws ValidationException, ProgrammingValidationException {
        values.validate(itemSet, null);
    }

    @Override
    protected void validate(KciSoundType soundType) throws ValidationException, ProgrammingValidationException {
        Validation.scope(
                "Sound type " + soundType.getName(),
                () -> soundType.validate(itemSet, soundType.getId())
        );
    }

    @Override
    protected void validateChange(SoundTypeReference reference, KciSoundType newValues) throws ValidationException, ProgrammingValidationException {
        newValues.validate(itemSet, reference.get().getId());
    }

    public SoundTypeReference getReference(UUID id) throws NoSuchElementException {
        if (itemSet.finishedLoading) {
            return new SoundTypeReference(CollectionHelper.find(elements, soundType -> soundType.getValues().getId(), id).get());
        } else {
            return new SoundTypeReference(id, itemSet);
        }
    }

    public Optional<KciSoundType> get(UUID id) {
        return CollectionHelper.find(elements, soundType -> soundType.getValues().getId(), id).map(Model::getValues);
    }
}
