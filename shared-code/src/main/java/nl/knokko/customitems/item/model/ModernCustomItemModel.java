package nl.knokko.customitems.item.model;

import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonKey;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static nl.knokko.customitems.texture.KciTexture.*;

public class ModernCustomItemModel implements ItemModel {

    public static final JsonKey TEXTURES_KEY = Jsoner.mintJsonKey("textures", new HashMap<Object, String>());

    private static String determineIncludedTexturePath(String itemName, String includedImageName) {
        return "customitems/model/" + itemName + "/" + includedImageName;
    }

    public static ModernCustomItemModel loadModernCustom(BitInput input, ItemSet.Side side) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding < 1 || encoding > 2) throw new UnknownEncodingException("ModernCustomItemModel", encoding);

        byte[] rawModel = null;
        if (encoding == 1 || side == ItemSet.Side.EDITOR) rawModel = input.readByteArray();

        int numIncludedImages = input.readInt();
        Collection<IncludedImage> includedImages = new ArrayList<>(numIncludedImages);
        for (int counter = 0; counter < numIncludedImages; counter++) {
            int numTextureReferences = input.readInt();
            List<String> textureReferences = new ArrayList<>(numTextureReferences);
            for (int refCounter = 0; refCounter < numTextureReferences; refCounter++) {
                textureReferences.add(input.readString());
            }
            String name = input.readString();
            BufferedImage image = null;
            if (encoding == 1 || side == ItemSet.Side.EDITOR) {
                image = loadImage(input, true);
            }
            includedImages.add(new IncludedImage(textureReferences, name, image));
        }

        return new ModernCustomItemModel(rawModel, includedImages);
    }

    private final byte[] rawModel;
    private final Collection<IncludedImage> includedImages;

    public ModernCustomItemModel(byte[] rawModel, Collection<IncludedImage> includedImages) {
        this.rawModel = rawModel;
        this.includedImages = new ArrayList<>(includedImages);
    }

    public byte[] getRawModel() {
        return rawModel;
    }

    public Collection<IncludedImage> getIncludedImages() {
        return new ArrayList<>(includedImages);
    }

    @Override
    public void write(
            ZipOutputStream zipOutput, String itemName, String textureName,
            DefaultModelType defaultModelType, boolean isLeatherArmor
    ) throws IOException {
        String rawModelString = new String(rawModel, StandardCharsets.UTF_8);
        try {
            JsonObject modelJson = (JsonObject) Jsoner.deserialize(rawModelString);
            Map<String, String> textureMap = modelJson.getMap(TEXTURES_KEY);

            for (IncludedImage includedImage : includedImages) {
                for (String textureReference : includedImage.textureReferences) {
                    textureMap.put(textureReference, determineIncludedTexturePath(itemName, includedImage.name));
                }
            }

            String updatedJson = Jsoner.prettyPrint(Jsoner.serialize(modelJson));
            byte[] rawUpdatedJson = updatedJson.getBytes(StandardCharsets.UTF_8);
            zipOutput.write(rawUpdatedJson);
            zipOutput.flush();
        } catch (JsonException e) {
            throw new RuntimeException(e);
        }

        for (IncludedImage includedImage : includedImages) {
            zipOutput.closeEntry();
            zipOutput.putNextEntry(new ZipEntry("assets/minecraft/textures/" + determineIncludedTexturePath(itemName, includedImage.name) + ".png"));
            ImageIO.write(includedImage.image, "PNG", zipOutput);
            zipOutput.flush();
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ModernCustomItemModel) {
            ModernCustomItemModel otherModel = (ModernCustomItemModel) other;
            return Arrays.equals(this.rawModel, otherModel.rawModel) && this.includedImages.equals(otherModel.includedImages);
        } else {
            return false;
        }
    }

    @Override
    public void save(BitOutput output, ItemSet.Side targetSide) {
        output.addByte(MODEL_TYPE_CUSTOM_MODERN);
        output.addByte((byte) 2);

        if (targetSide == ItemSet.Side.EDITOR) output.addByteArray(rawModel);

        output.addInt(includedImages.size());
        for (IncludedImage image : includedImages) {
            output.addInt(image.textureReferences.size());
            for (String textureReference : image.textureReferences) {
                output.addString(textureReference);
            }
            output.addString(image.name);
            if (targetSide == ItemSet.Side.EDITOR) saveImage(output, image.image);
        }
    }

    public static class IncludedImage {

        public final List<String> textureReferences;
        public final String name;
        public final BufferedImage image;

        public IncludedImage(List<String> textureReference, String name, BufferedImage image) {
            this.textureReferences = Collections.unmodifiableList(textureReference);
            this.name = name;
            this.image = image;
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof IncludedImage) {
                IncludedImage otherImage = (IncludedImage) other;
                return this.textureReferences.equals(otherImage.textureReferences) && this.name.equals(otherImage.name)
                        && areImagesEqual(this.image, otherImage.image);
            } else {
                return false;
            }
        }
    }
}
