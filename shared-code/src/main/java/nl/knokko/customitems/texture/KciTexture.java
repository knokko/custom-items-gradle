package nl.knokko.customitems.texture;

import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.texture.animated.AnimatedTexture;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class KciTexture extends ModelValues {

    public static final byte ENCODING_SIMPLE_1 = 0;
    public static final byte ENCODING_BOW_1 = 1;
    public static final byte ENCODING_CROSSBOW_1 = 2;
    public static final byte ENCODING_ANIMATED = 3;
    public static final byte ENCODING_SIMPLE_2 = 4;
    public static final byte ENCODING_BOW_2 = 5;
    public static final byte ENCODING_CROSSBOW_2 = 6;

    public static boolean areImagesEqual(BufferedImage a, BufferedImage b) {
        if (a == null && b == null) return true;
        if (a == null) return false;
        if (b == null) return false;
        if (a.getWidth() != b.getWidth() || a.getHeight() != b.getHeight()) return false;

        for (int x = 0; x < a.getWidth(); x++) {
            for (int y = 0; y < a.getHeight(); y++) {
                if (a.getRGB(x, y) != b.getRGB(x, y)) return false;
            }
        }

        return true;
    }

    private static void validateSize(int size) throws ValidationException {
        if (size < 1) throw new ValidationException("must be positive");
        if (size > 512) throw new ValidationException("can be at most 512");
        if (Integer.bitCount(size) != 1) throw new ValidationException("must be a power of 2");
    }

    public static void validateImage(BufferedImage image) throws ValidationException, ProgrammingValidationException {
        if (image == null) throw new ValidationException("Not selected");
        Validation.scope("width (" + image.getWidth() + ")", () -> validateSize(image.getWidth()));
        Validation.scope("height (" + image.getHeight() + ")", () -> validateSize(image.getHeight()));
        if (image.getWidth() != image.getHeight()) {
            throw new ValidationException("width (" + image.getWidth() + ") must be equal to height (" + image.getHeight() + ")");
        }
    }

    public static KciTexture load(
            BitInput input, boolean expectCompressed, ItemSet.Side targetSide
    ) throws UnknownEncodingException {
        return load(input, input.readByte(), expectCompressed, targetSide);
    }

    public static KciTexture load(
            BitInput input, byte encoding, boolean expectCompressed, ItemSet.Side side
    ) throws UnknownEncodingException {
        KciTexture result;

        if (encoding == ENCODING_SIMPLE_1) {
            result = new KciTexture(false);
            result.loadBase1(input, expectCompressed);
        } else if (encoding == ENCODING_BOW_1) {
            result = new BowTexture(false);
            ((BowTexture) result).loadBow1(input, expectCompressed);
        } else if (encoding == ENCODING_CROSSBOW_1) {
            result = new CrossbowTexture(false);
            ((CrossbowTexture) result).loadCrossbow1(input);
        } else if (encoding == ENCODING_ANIMATED) {
            result = AnimatedTexture.load(input, side);
        } else if (encoding == ENCODING_SIMPLE_2) {
            result = new KciTexture(false);
            result.loadBase2(input, side);
        } else if (encoding == ENCODING_BOW_2) {
            result = new BowTexture(false);
            ((BowTexture) result).loadBow2(input, side);
        } else if (encoding == ENCODING_CROSSBOW_2) {
            result = new CrossbowTexture(false);
            ((CrossbowTexture) result).loadCrossbow2(input, side);
        } else {
            throw new UnknownEncodingException("Texture", encoding);
        }

        return result;
    }

    public static KciTexture createQuick(String name, BufferedImage image) {
        KciTexture result = new KciTexture(true);
        result.setName(name);
        if (image != null) result.setImage(image);
        return result;
    }

    protected String name;
    protected BufferedImage image;

    public KciTexture(boolean mutable) {
        super(mutable);

        this.name = "";
        this.image = null;
    }

    public KciTexture(KciTexture toCopy, boolean mutable) {
        super(mutable);

        this.name = toCopy.getName();
        this.image = toCopy.getImage();
    }

    protected void loadBase1(BitInput input, boolean expectCompressed) {
        name = input.readJavaString();
        this.image = loadImage(input, expectCompressed);
    }

    protected void loadBase2(BitInput input, ItemSet.Side side) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("Base texture", encoding);
        name = input.readJavaString();
        if (side == ItemSet.Side.EDITOR) this.image = loadImage(input, true);
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
    public boolean equals(Object other) {
        if (other.getClass() == KciTexture.class) {
            KciTexture otherTexture = (KciTexture) other;
            return this.name.equals(otherTexture.name) && areImagesEqual(this.image, otherTexture.image);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Texture " + name;
    }

    @Override
    public KciTexture copy(boolean mutable) {
        return new KciTexture(this, mutable);
    }

    public void save(BitOutput output, ItemSet.Side targetSide) {
        output.addByte(ENCODING_SIMPLE_2);
        saveBase2(output, targetSide);
    }

    protected void saveBase2(BitOutput output, ItemSet.Side targetSide) {
        output.addByte((byte) 1);
        output.addJavaString(name);
        if (targetSide == ItemSet.Side.EDITOR) saveImage(output, image);
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
        Checks.notNull(newName);
        this.name = newName;
    }

    public void setImage(BufferedImage newImage) {
        assertMutable();
        Checks.notNull(newImage);
        this.image = newImage;
    }

    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        Validation.safeName(name);
        Validation.scope("Image", () -> validateImage(this.image));
    }

    public void validateComplete(ItemSet itemSet, String oldName) throws ValidationException, ProgrammingValidationException {
        this.validateIndependent();

        boolean nameConflict = false;
        if (oldName == null && itemSet.textures.get(this.name).isPresent()) nameConflict = true;
        if (oldName != null && !oldName.equals(this.name) && itemSet.textures.get(this.name).isPresent()) nameConflict = true;
        if (nameConflict) {
            throw new ValidationException("A texture with name " + this.name + " already exists");
        }
    }

    public void validateExportVersion(int version) throws ValidationException {
        // Regular textures don't have any version dependant properties
    }
}
