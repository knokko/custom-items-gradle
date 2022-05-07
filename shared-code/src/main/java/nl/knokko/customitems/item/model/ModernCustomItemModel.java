package nl.knokko.customitems.item.model;

import nl.knokko.customitems.item.CustomItemValues;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.zip.ZipOutputStream;

public class ModernCustomItemModel implements ItemModel {

    private final byte[] rawModel;
    private final Collection<IncludedImage> includedImages;

    public ModernCustomItemModel(byte[] rawModel, Collection<IncludedImage> includedImages) {
        this.rawModel = rawModel;
        this.includedImages = new ArrayList<>(includedImages);
    }

    @Override
    public void write(ZipOutputStream zipOutput, CustomItemValues item, DefaultModelType defaultModelType) throws IOException {
        zipOutput.write(rawModel);
        zipOutput.flush();

        for (IncludedImage includedImage : includedImages) {
            zipOutput.closeEntry();
            zipOutput.putNextEntry(ehm);
            ImageIO.write(includedImage.image, "PNG", zipOutput);
            zipOutput.flush();
        }
    }

    public static class IncludedImage {

        public final String name;
        public final BufferedImage image;

        public IncludedImage(String name, BufferedImage image) {
            this.name = name;
            this.image = image;
        }
    }
}
