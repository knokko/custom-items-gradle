package nl.knokko.customitems.itemset;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.texture.ArmorTexture;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.CollectionHelper;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.NoSuchElementException;
import java.util.Optional;

public class ArmorTextureManager extends ModelManager<ArmorTexture, ArmorTextureReference> {

    protected ArmorTextureManager(ItemSet itemSet) {
        super(itemSet);
    }

    @Override
    protected void saveElement(ArmorTexture element, BitOutput output, ItemSet.Side targetSide) {
        element.save(output);
    }

    @Override
    ArmorTextureReference createReference(Model<ArmorTexture> element) {
        return new ArmorTextureReference(element);
    }

    @Override
    public void load(BitInput input) throws UnknownEncodingException {
        if (itemSet.getSide() == ItemSet.Side.EDITOR) super.load(input);
    }

    @Override
    protected ArmorTexture loadElement(BitInput input) throws UnknownEncodingException {
        return ArmorTexture.load(input);
    }

    @Override
    protected void validateExportVersion(ArmorTexture texture, int mcVersion) throws ValidationException, ProgrammingValidationException {
        Validation.scope(
                "Armor texture " + texture.getName(),
                () -> texture.validateExportVersion(mcVersion)
        );
    }

    @Override
    public void validate() throws ValidationException, ProgrammingValidationException {
        super.validate();
        validateUniqueIDs("armor texture name", elements, armorTexture -> armorTexture.getValues().getName());
    }

    @Override
    protected void validateCreation(ArmorTexture values) throws ValidationException, ProgrammingValidationException {
        values.validate(itemSet, null);
    }

    @Override
    protected void validate(ArmorTexture texture) throws ValidationException, ProgrammingValidationException {
        Validation.scope(
                "Armor texture " + texture.getName(),
                () -> texture.validate(itemSet, texture.getName())
        );
    }

    @Override
    protected void validateChange(ArmorTextureReference reference, ArmorTexture newValues) throws ValidationException, ProgrammingValidationException {
        newValues.validate(itemSet, reference.get().getName());
    }

    public ArmorTextureReference getReference(String name) throws NoSuchElementException {
        if (itemSet.finishedLoading) {
            return new ArmorTextureReference(CollectionHelper.find(elements, texture -> texture.getValues().getName(), name).get());
        } else {
            return new ArmorTextureReference(name, itemSet);
        }
    }

    public Optional<ArmorTexture> get(String name) {
        return CollectionHelper.find(elements, texture -> texture.getValues().getName(), name).map(Model::getValues);
    }
}
