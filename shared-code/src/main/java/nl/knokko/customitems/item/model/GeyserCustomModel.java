package nl.knokko.customitems.item.model;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;

import java.util.Objects;

public class GeyserCustomModel {

    public static GeyserCustomModel load(BitInput input, ItemSet.Side side) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding < 1 || encoding > 2) throw new UnknownEncodingException("GeyserCustomModel", encoding);

        if (encoding == 1 || side == ItemSet.Side.EDITOR) {
            return new GeyserCustomModel(
                    input.readString(), input.readString(), input.readByteArray(),
                    input.readByteArray(), input.readByteArray(), input.readByteArray()
            );
        } else return new GeyserCustomModel(
                input.readString(), input.readString(), null,
                null, null, null
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
        this.animationFile = animationFile;
        this.attachableFile = attachableFile;
        this.modelFile = modelFile;
        this.textureFile = textureFile;
    }

    public void save(BitOutput output, ItemSet.Side targetSide) {
        output.addByte((byte) 2);
        output.addString(attachableId);
        output.addString(geometryId);
        if (targetSide == ItemSet.Side.EDITOR) {
            output.addByteArray(animationFile);
            output.addByteArray(attachableFile);
            output.addByteArray(modelFile);
            output.addByteArray(textureFile);
        }
    }
}
