package nl.knokko.customitems.item.model;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.trouble.UnknownEncodingException;

import java.util.Objects;

public class GeyserCustomModel {

    public static GeyserCustomModel load(BitInput input) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("GeyserCustomModel", encoding);

        return new GeyserCustomModel(
                input.readString(), input.readString(), input.readByteArray(),
                input.readByteArray(), input.readByteArray(), input.readByteArray()
        );
    }

    public final String attachableId;
    public final String geometryId;
    public final byte[] animationFile;
    public final byte[] attachableFile;
    public final byte[] modelFile;
    public final byte[] textureFile;

    public GeyserCustomModel(
            String attachableId, String geometryId, byte[] animationFile,
            byte[] attachableFile, byte[] modelFile, byte[] textureFile
    ) {
        this.attachableId = Objects.requireNonNull(attachableId);
        this.geometryId = Objects.requireNonNull(geometryId);
        this.animationFile = Objects.requireNonNull(animationFile);
        this.attachableFile = Objects.requireNonNull(attachableFile);
        this.modelFile = Objects.requireNonNull(modelFile);
        this.textureFile = Objects.requireNonNull(textureFile);
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);
        output.addString(attachableId);
        output.addString(geometryId);
        output.addByteArray(animationFile);
        output.addByteArray(attachableFile);
        output.addByteArray(modelFile);
        output.addByteArray(textureFile);
    }
}
