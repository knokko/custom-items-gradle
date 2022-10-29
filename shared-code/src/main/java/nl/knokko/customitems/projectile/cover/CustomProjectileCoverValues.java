package nl.knokko.customitems.projectile.cover;

import nl.knokko.customitems.item.model.ItemModel;
import nl.knokko.customitems.item.model.LegacyCustomItemModel;
import nl.knokko.customitems.item.model.ModernCustomItemModel;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.io.IOException;
import java.util.zip.ZipOutputStream;

public class CustomProjectileCoverValues extends ProjectileCoverValues {

    static CustomProjectileCoverValues load(BitInput input, byte encoding) throws UnknownEncodingException {
        CustomProjectileCoverValues result = new CustomProjectileCoverValues(false);

        if (encoding == ENCODING_CUSTOM1) {
            result.load1(input);
        } else if (encoding == ENCODING_CUSTOM2) {
            result.loadNew(input);
        } else {
            throw new UnknownEncodingException("CustomProjectileCover", encoding);
        }

        return result;
    }

    private ItemModel model;

    public CustomProjectileCoverValues(boolean mutable) {
        super(mutable);
        this.model = null;
    }

    public CustomProjectileCoverValues(CustomProjectileCoverValues toCopy, boolean mutable) {
        super(toCopy, mutable);
        this.model = toCopy.getModel();
    }

    private void load1(BitInput input) {
        loadSharedProperties1(input);
        this.model = new LegacyCustomItemModel(input.readByteArray());
    }

    private void loadNew(BitInput input) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("CustomProjectileCover", encoding);
        loadSharedProperties1(input);
        this.model = ItemModel.load(input);
    }

    @Override
    protected void save(BitOutput output) {
        output.addByte(ENCODING_CUSTOM2);
        output.addByte((byte) 1);
        saveSharedProperties1(output);
        this.model.save(output);
    }

    protected boolean areCustomPropertiesEqual(CustomProjectileCoverValues other) {
        return areBasePropertiesEqual(other) && this.model.equals(other.model);
    }

    @Override
    public boolean equals(Object other) {
        return other.getClass() == CustomProjectileCoverValues.class && areCustomPropertiesEqual((CustomProjectileCoverValues) other);
    }

    @Override
    public CustomProjectileCoverValues copy(boolean mutable) {
        return new CustomProjectileCoverValues(this, mutable);
    }

    public ItemModel getModel() {
        return model;
    }

    public void setModel(ItemModel newModel) {
        assertMutable();
        Checks.notNull(newModel);
        if (!(newModel instanceof LegacyCustomItemModel || newModel instanceof ModernCustomItemModel)) {
            throw new IllegalArgumentException("Custom projectile covers can only have custom models");
        }
        this.model = newModel;
    }

    @Override
    public void validate(ItemSet itemSet, String oldName) throws ValidationException, ProgrammingValidationException {
        super.validate(itemSet, oldName);
        if (model == null) throw new ValidationException("You need to select a model");
    }

    @Override
    public void writeModel(ZipOutputStream output) throws IOException {
        model.write(output, "projectile_cover/" + this.name, null, null, false);
        output.flush();
    }
}
