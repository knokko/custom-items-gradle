package nl.knokko.customitems.texture;

import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ArmorTextureValues extends ModelValues {

    private static final byte ENCODING_1 = 1;

    public static ArmorTextureValues load(BitInput input) throws UnknownEncodingException {
        byte encoding = input.readByte();
        ArmorTextureValues result = new ArmorTextureValues(false);

        if (encoding == ENCODING_1) {
            result.load1(input);
        } else {
            throw new UnknownEncodingException("ArmorTexture", encoding);
        }

        return result;
    }

    private String name;
    private BufferedImage layer1, layer2;

    public ArmorTextureValues(boolean mutable) {
        super(mutable);

        this.name = "";
        this.layer1 = null;
        this.layer2 = null;
    }

    public ArmorTextureValues(ArmorTextureValues toCopy, boolean mutable) {
        super(mutable);

        this.name = toCopy.getName();
        this.layer1 = toCopy.getLayer1();
        this.layer2 = toCopy.getLayer2();
    }

    public void save(BitOutput output) {
        output.addByte(ENCODING_1);
        save1(output);
    }

    private void save1(BitOutput output) {
        output.addString(name);
        byte[] bytesOfLayer1;
        byte[] bytesOfLayer2;
        try {
            ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
            ImageIO.write(layer1, "PNG", byteOutput);
            bytesOfLayer1 = byteOutput.toByteArray();
            byteOutput.reset();
            ImageIO.write(layer2, "PNG", byteOutput);
            bytesOfLayer2 = byteOutput.toByteArray();
        } catch (IOException shouldntHappen) {
            throw new RuntimeException("Computer is incapable of encoding images", shouldntHappen);
        }
        output.addByteArray(bytesOfLayer1);
        output.addByteArray(bytesOfLayer2);
    }

    private void load1(BitInput input) {
        name = input.readString();
        byte[] bytesOfLayer1 = input.readByteArray();
        byte[] bytesOfLayer2 = input.readByteArray();
        try {
            layer1 = ImageIO.read(new ByteArrayInputStream(bytesOfLayer1));
            layer2 = ImageIO.read(new ByteArrayInputStream(bytesOfLayer2));
        } catch (IOException shouldntHappen) {
            throw new IllegalArgumentException("Corrupted image input", shouldntHappen);
        }
    }

    @Override
    public ArmorTextureValues copy(boolean mutable) {
        return new ArmorTextureValues(this, mutable);
    }

    public String getName() {
        return name;
    }

    // Note: we are leaking a mutable instance of layer1 and layer2. I don't know a quick way around this...
    public BufferedImage getLayer1() {
        return layer1;
    }

    public BufferedImage getLayer2() {
        return layer2;
    }

    public void setName(String newName) {
        assertMutable();
        Checks.notNull(newName);
        this.name = newName;
    }

    public void setLayer1(BufferedImage newLayer1) {
        assertMutable();
        Checks.notNull(newLayer1);
        this.layer1 = newLayer1;
    }

    public void setLayer2(BufferedImage newLayer2) {
        assertMutable();
        Checks.notNull(newLayer2);
        this.layer2 = newLayer2;
    }

    public void validate(SItemSet itemSet, String oldName) throws ValidationException, ProgrammingValidationException {
        if (name == null) throw new ProgrammingValidationException("No name");
        if (name.isEmpty()) throw new ValidationException("Name can't be empty");

        if (oldName == null || !oldName.equals(name)) {
            for (ArmorTextureValues otherArmorTexture : itemSet.getArmorTextures()) {
                if (otherArmorTexture.getName().equals(name)) {
                    throw new ValidationException("Another armor texture has the same name");
                }
            }
        }

        if (layer1 == null) throw new ValidationException("You must choose a layer1 texture");
        if (layer1.getWidth() != 2 * layer1.getHeight()) {
            throw new ValidationException("The width of layer1 (" + layer1.getWidth() + ") is not twice as big as the height (" + layer1.getHeight() + ")");
        }

        if (layer2 == null) throw new ValidationException("You must choose a layer2 texture");
        if (layer2.getWidth() != 2 * layer2.getHeight()) {
            throw new ValidationException("The width of layer2 (" + layer2.getWidth() + ") is not twice as big as the height (" + layer2.getHeight() + ")");
        }
    }
}
