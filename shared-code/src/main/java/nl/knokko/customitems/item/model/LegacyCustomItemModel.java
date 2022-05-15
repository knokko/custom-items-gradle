package nl.knokko.customitems.item.model;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.trouble.UnknownEncodingException;

import java.io.IOException;
import java.util.Arrays;
import java.util.zip.ZipOutputStream;

public class LegacyCustomItemModel implements ItemModel {

    public static LegacyCustomItemModel loadLegacyCustom(BitInput input) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("LegacyCustomItemModel", encoding);

        byte[] rawModel = input.readByteArray();
        return new LegacyCustomItemModel(rawModel);
    }

    private final byte[] rawModel;

    public LegacyCustomItemModel(byte[] rawModel) {
        this.rawModel = rawModel;
    }

    public byte[] getRawModel() {
        return rawModel;
    }

    @Override
    public void write(
            ZipOutputStream zipOutput, String itemName, String textureName,
            DefaultModelType defaultModelType, boolean isLeatherArmor
    ) throws IOException {
        zipOutput.write(rawModel);
        zipOutput.flush();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof LegacyCustomItemModel && Arrays.equals(this.rawModel, ((LegacyCustomItemModel) other).rawModel);
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(MODEL_TYPE_CUSTOM_LEGACY);
        output.addByte((byte) 1);

        output.addByteArray(rawModel);
    }
}
