package nl.knokko.customitems.block.model;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.itemset.TextureReference;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

import java.io.IOException;
import java.util.zip.ZipOutputStream;

public interface BlockModel {

    byte MODEL_TYPE_SIMPLE = 0;
    byte MODEL_TYPE_SIDED = 1;
    byte MODEL_TYPE_CUSTOM = 2;

    static BlockModel load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte modelType = input.readByte();
        if (modelType == MODEL_TYPE_SIMPLE) return SimpleBlockModel.loadSimple(input, itemSet);
        else if (modelType == MODEL_TYPE_SIDED) return SidedBlockModel.loadSided(input, itemSet);
        else if (modelType == MODEL_TYPE_CUSTOM) return CustomBlockModel.loadCustom(input, itemSet);
        else throw new UnknownEncodingException("BlockModel", modelType);
    }

    void write(ZipOutputStream zipOutput, String blockName) throws IOException;

    void save(BitOutput output);

    void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException;

    TextureReference getPrimaryTexture();
}
