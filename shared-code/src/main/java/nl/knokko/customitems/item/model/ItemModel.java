package nl.knokko.customitems.item.model;

import nl.knokko.customitems.item.CustomItemValues;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipOutputStream;

public interface ItemModel {

    void write(ZipOutputStream zipOutput, CustomItemValues item, DefaultModelType defaultModelType) throws IOException;
}
