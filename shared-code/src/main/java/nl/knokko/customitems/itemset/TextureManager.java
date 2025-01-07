package nl.knokko.customitems.itemset;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.texture.KciTexture;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.CollectionHelper;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;

public class TextureManager extends ModelManager<KciTexture, TextureReference> {

    protected TextureManager(ItemSet itemSet) {
        super(itemSet);
    }

    @Override
    protected void saveElement(KciTexture texture, BitOutput output, ItemSet.Side targetSide) {
        texture.save(output, targetSide);
    }

    @Override
    TextureReference createReference(Model<KciTexture> element) {
        return new TextureReference(element);
    }

    @Override
    public void load(BitInput input) throws UnknownEncodingException {
        throw new UnsupportedOperationException();
    }

    public void load(BitInput input, boolean readEncoding, boolean expectCompressed) throws UnknownEncodingException {
        int numTextures = input.readInt();
        this.elements = new ArrayList<>(numTextures);
        for (int counter = 0; counter < numTextures; counter++) {
            if (readEncoding) {
                this.elements.add(new Model<>(KciTexture.load(input, expectCompressed, itemSet.getSide())));
            } else {
                this.elements.add(new Model<>(KciTexture.load(input, KciTexture.ENCODING_SIMPLE_1, expectCompressed, itemSet.getSide())));
            }
        }
    }

    @Override
    protected KciTexture loadElement(BitInput input) throws UnknownEncodingException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void validateExportVersion(KciTexture texture, int mcVersion) throws ValidationException, ProgrammingValidationException {
        Validation.scope(
                "Texture " + texture.getName(),
                () -> texture.validateExportVersion(mcVersion)
        );
    }

    @Override
    public void validate() throws ValidationException, ProgrammingValidationException {
        super.validate();
        validateUniqueIDs("texture name", elements, texture -> texture.getValues().getName());
    }

    @Override
    protected void validateCreation(KciTexture values) throws ValidationException, ProgrammingValidationException {
        values.validateComplete(itemSet, null);
    }

    @Override
    protected void validate(KciTexture texture) throws ValidationException, ProgrammingValidationException {
        Validation.scope(
                "Texture " + texture.getName(),
                () -> texture.validateComplete(itemSet, texture.getName())
        );
    }

    @Override
    protected void validateChange(TextureReference reference, KciTexture newValues) throws ValidationException, ProgrammingValidationException {
        newValues.validateComplete(itemSet, reference.get().getName());
    }

    public TextureReference getReference(String name) throws NoSuchElementException {
        if (itemSet.finishedLoading) {
            return new TextureReference(CollectionHelper.find(elements, texture -> texture.getValues().getName(), name).get());
        } else {
            return new TextureReference(name, itemSet);
        }
    }

    public Optional<KciTexture> get(String name) {
        return CollectionHelper.find(elements, texture -> texture.getValues().getName(), name).map(Model::getValues);
    }
}
