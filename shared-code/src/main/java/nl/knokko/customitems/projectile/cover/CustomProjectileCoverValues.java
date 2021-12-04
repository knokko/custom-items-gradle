package nl.knokko.customitems.projectile.cover;

import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.CollectionHelper;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import java.io.IOException;
import java.util.zip.ZipOutputStream;

public class CustomProjectileCoverValues extends ProjectileCoverValues {

    static CustomProjectileCoverValues load(BitInput input, byte encoding) throws UnknownEncodingException {
        CustomProjectileCoverValues result = new CustomProjectileCoverValues(false);

        if (encoding == ENCODING_CUSTOM1) {
            result.load1(input);
        } else {
            throw new UnknownEncodingException("CustomProjectileCover", encoding);
        }

        return result;
    }

    private byte[] customModel;

    public CustomProjectileCoverValues(boolean mutable) {
        super(mutable);
        this.customModel = null;
    }

    public CustomProjectileCoverValues(CustomProjectileCoverValues toCopy, boolean mutable) {
        super(toCopy, mutable);
        this.customModel = toCopy.getCustomModel();
    }

    private void load1(BitInput input) {
        loadSharedProperties1(input);
        this.customModel = input.readByteArray();
    }

    @Override
    protected void save(BitOutput output) {
        output.addByte(ENCODING_CUSTOM1);
        saveSharedProperties1(output);
        output.addByteArray(customModel);
    }

    @Override
    public CustomProjectileCoverValues copy(boolean mutable) {
        return new CustomProjectileCoverValues(this, mutable);
    }

    public byte[] getCustomModel() {
        return CollectionHelper.arrayCopy(customModel);
    }

    public void setCustomModel(byte[] newModel) {
        assertMutable();
        Checks.notNull(newModel);
        this.customModel = CollectionHelper.arrayCopy(newModel);
    }

    @Override
    public void validate(SItemSet itemSet, String oldName) throws ValidationException, ProgrammingValidationException {
        super.validate(itemSet, oldName);
        if (customModel == null) throw new ValidationException("You need to select a custom model");
    }

    @Override
    public void writeModel(ZipOutputStream output) throws IOException {
        output.write(customModel);
    }
}
