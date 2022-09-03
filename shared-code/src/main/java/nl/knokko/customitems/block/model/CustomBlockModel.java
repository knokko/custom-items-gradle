package nl.knokko.customitems.block.model;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.item.model.ItemModel;
import nl.knokko.customitems.item.model.ModernCustomItemModel;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.itemset.TextureReference;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

import java.io.IOException;
import java.util.zip.ZipOutputStream;

public class CustomBlockModel implements BlockModel {

    static CustomBlockModel loadCustom(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("CustomBlockModel", encoding);

        return new CustomBlockModel((ModernCustomItemModel) ItemModel.load(input), itemSet.getTextureReference(input.readString()));
    }

    private final ModernCustomItemModel itemModel;
    private final TextureReference editorTexture;

    public CustomBlockModel(ModernCustomItemModel itemModel, TextureReference editorTexture) {
        this.itemModel = itemModel;
        this.editorTexture = editorTexture;
    }

    @Override
    public void write(ZipOutputStream zipOutput, String blockName) throws IOException {
        itemModel.write(zipOutput, "block/" + blockName, null, null, false);
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(MODEL_TYPE_CUSTOM);
        output.addByte((byte) 1);

        itemModel.save(output);
        output.addString(editorTexture.get().getName());
    }

    @Override
    public void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if (itemModel == null) throw new ValidationException("You need to choose a model");
        if (editorTexture == null) throw new ValidationException("You need to choose an Editor texture");
        if (!itemSet.isReferenceValid(editorTexture)) {
            throw new ProgrammingValidationException("Editor texture is no longer valid");
        }
    }

    @Override
    public TextureReference getPrimaryTexture() {
        return editorTexture;
    }

    @Override
    public String toString() {
        return "custom";
    }
}
