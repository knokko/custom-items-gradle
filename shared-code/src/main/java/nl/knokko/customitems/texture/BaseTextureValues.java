package nl.knokko.customitems.texture;

import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BaseTextureValues extends ModelValues {

    public static final byte ENCODING_SIMPLE_1 = 0;
    public static final byte ENCODING_BOW_1 = 1;
    public static final byte ENCODING_CROSSBOW_1 = 2;

    public static BaseTextureValues load(BitInput input, boolean expectCompressed, boolean mutable) throws UnknownEncodingException {
        byte encoding = input.readByte();
        BaseTextureValues result;

        if (encoding == ENCODING_SIMPLE_1) {
            result = new BaseTextureValues(mutable);
            result.loadBase1(input, expectCompressed);
        } else if (encoding == ENCODING_BOW_1) {
            result = new BowTextureValues(mutable);
            ((BowTextureValues) result).loadBow1(input, expectCompressed);
        } else if (encoding == ENCODING_CROSSBOW_1) {
            result = new CrossbowTextureValues(mutable);
            ((CrossbowTextureValues) result).loadCrossbow1(input);
        } else {
            throw new UnknownEncodingException("Texture", encoding);
        }

        return result;
    }

    protected String name;
    protected BufferedImage image;

    public BaseTextureValues(boolean mutable) {
        super(mutable);

        this.name = "";
        this.image = null;
    }

    public BaseTextureValues(BaseTextureValues toCopy, boolean mutable) {
        super(mutable);

        this.name = toCopy.getName();
        this.image = toCopy.getImage();
    }

    protected void loadBase1(BitInput input, boolean expectCompressed) {
        name = input.readJavaString();
        this.image = loadImage(input, expectCompressed);
    }

    public static BufferedImage loadImage(BitInput input, boolean expectCompressed) {
        if (expectCompressed) {
            try {
                byte[] imageBytes = input.readByteArray();
                InputStream imageInput = new ByteArrayInputStream(imageBytes);
                return ImageIO.read(imageInput);
            } catch (IOException shouldNotHappen) {
                throw new RuntimeException(shouldNotHappen);
            }
        } else {
            BufferedImage result = new BufferedImage(input.readInt(), input.readInt(), BufferedImage.TYPE_INT_ARGB);
            input.increaseCapacity(32 * result.getWidth() * result.getHeight());
            for (int x = 0; x < result.getWidth(); x++) {
                for (int y = 0; y < result.getHeight(); y++) {
                    result.setRGB(x, y, input.readDirectInt());
                }
            }

            return result;
        }
    }

    @Override
    public String toString() {
        return "Texture " + name;
    }

    @Override
    public ModelValues copy(boolean mutable) {
        return new BaseTextureValues(this, mutable);
    }

    public void save(BitOutput output) {
        output.addByte(ENCODING_SIMPLE_1);
        saveBase1(output);
    }

    protected void saveBase1(BitOutput output) {
        output.addJavaString(name);
        saveImage(output, image);
    }

    public static void saveImage(BitOutput output, BufferedImage toSave) {
        try {
            ByteArrayOutputStream imageOutput = new ByteArrayOutputStream();
            ImageIO.write(toSave, "PNG", imageOutput);
            byte[] imageBytes = imageOutput.toByteArray();
            output.addByteArray(imageBytes);
        } catch (IOException shouldNotHappen) {
            throw new RuntimeException(shouldNotHappen);
        }
    }

    public String getName() {
        return name;
    }

    public BufferedImage getImage() {
        // I would rather return a deep copy of the image, but that sounds like a performance hazard
        return image;
    }

    public void setName(String newName) {
        assertMutable();
        this.name = newName;
    }

    public void setImage(BufferedImage newImage) {
        assertMutable();
        this.image = newImage;
    }
}
