package nl.knokko.customitems.block.model;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.item.model.GeyserCustomModel;
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
        if (encoding < 1 || encoding > 2) throw new UnknownEncodingException("CustomBlockModel", encoding);

        ModernCustomItemModel model = (ModernCustomItemModel) ItemModel.load(input, itemSet.getSide());
        TextureReference editorTexture = itemSet.textures.getReference(input.readString());
        GeyserCustomModel geyserModel = null;
        if (encoding != 1 && input.readBoolean()) geyserModel = GeyserCustomModel.load(input, itemSet.getSide());

        return new CustomBlockModel(model, editorTexture, geyserModel);
    }

    private final ModernCustomItemModel itemModel;
    private final TextureReference editorTexture;
    private final GeyserCustomModel geyserModel;

    public CustomBlockModel(ModernCustomItemModel itemModel, TextureReference editorTexture, GeyserCustomModel geyserModel) {
        this.itemModel = itemModel;
        this.editorTexture = editorTexture;
        this.geyserModel = geyserModel;
    }

    @Override
    public void write(ZipOutputStream zipOutput, String blockName) throws IOException {
        itemModel.write(zipOutput, "block/" + blockName, null, null, false);
    }

    @Override
    public void save(BitOutput output, ItemSet.Side targetSide) {
        output.addByte(MODEL_TYPE_CUSTOM);
        output.addByte((byte) 2);

        itemModel.save(output, targetSide);
        output.addString(editorTexture.get().getName());
        output.addBoolean(geyserModel != null);
        if (geyserModel != null) geyserModel.save(output, targetSide);
    }

    @Override
    public void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if (itemModel == null) throw new ValidationException("You need to choose a model");
        if (editorTexture == null) throw new ValidationException("You need to choose an Editor texture");
        if (!itemSet.textures.isValid(editorTexture)) {
            throw new ProgrammingValidationException("Editor texture is no longer valid");
        }
    }

    public ModernCustomItemModel getItemModel() {
        return itemModel;
    }

    @Override
    public TextureReference getPrimaryTexture() {
        return editorTexture;
    }

    public GeyserCustomModel getGeyserModel() {
        return geyserModel;
    }

    @Override
    public String toString() {
        return "custom";
    }
}
