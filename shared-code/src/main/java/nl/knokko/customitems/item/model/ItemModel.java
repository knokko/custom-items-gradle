package nl.knokko.customitems.item.model;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;

import java.io.IOException;
import java.util.zip.ZipOutputStream;

public interface ItemModel {

    byte MODEL_TYPE_NONE = 0;
    byte MODEL_TYPE_DEFAULT = 1;
    byte MODEL_TYPE_CUSTOM_LEGACY = 2;
    byte MODEL_TYPE_CUSTOM_MODERN = 3;

    static ItemModel load(BitInput input, ItemSet.Side side) throws UnknownEncodingException {
        byte modelType = input.readByte();
        switch (modelType) {
            case MODEL_TYPE_NONE: return null;
            case MODEL_TYPE_DEFAULT: return DefaultItemModel.loadDefault(input);
            case MODEL_TYPE_CUSTOM_LEGACY: return LegacyCustomItemModel.loadLegacyCustom(input, side);
            case MODEL_TYPE_CUSTOM_MODERN: return ModernCustomItemModel.loadModernCustom(input, side);
            default: throw new UnknownEncodingException("ItemModel", modelType);
        }
    }

    void write(
            ZipOutputStream zipOutput, String itemName, String textureName,
            DefaultModelType defaultModelType, boolean isLeatherArmor
    ) throws IOException;

    void save(BitOutput output, ItemSet.Side targetSide);
}
