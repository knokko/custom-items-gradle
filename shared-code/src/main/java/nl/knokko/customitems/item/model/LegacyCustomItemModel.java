package nl.knokko.customitems.item.model;

import nl.knokko.customitems.item.CustomItemValues;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.zip.ZipOutputStream;

public class LegacyCustomItemModel implements ItemModel {

    private final byte[] rawModel;

    public LegacyCustomItemModel(byte[] rawModel) {
        this.rawModel = rawModel;
    }

    @Override
    public void write(ZipOutputStream zipOutput, CustomItemValues item, DefaultModelType defaultModelType) throws IOException {
        zipOutput.write(rawModel);
        zipOutput.flush();
    }
}
