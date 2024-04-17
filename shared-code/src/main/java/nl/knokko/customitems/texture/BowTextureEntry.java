package nl.knokko.customitems.texture;

import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.awt.image.BufferedImage;

import static nl.knokko.customitems.texture.KciTexture.areImagesEqual;
import static nl.knokko.customitems.util.Checks.isClose;

public class BowTextureEntry extends ModelValues {

    public static BowTextureEntry load1(BitInput input, boolean expectCompressed, boolean mutable) {
        BowTextureEntry result = new BowTextureEntry(mutable);
        result.pull = input.readDouble();
        result.image = KciTexture.loadImage(input, expectCompressed);
        return result;
    }

    public static BowTextureEntry createQuick(BufferedImage image, double pull) {
        BowTextureEntry result = new BowTextureEntry(true);
        if (image != null) result.setImage(image);
        result.setPull(pull);
        return result;
    }

    private BufferedImage image;
    private double pull;

    public BowTextureEntry(boolean mutable) {
        super(mutable);

        this.image = null;
        this.pull = 0.0;
    }

    public BowTextureEntry(BowTextureEntry toCopy, boolean mutable) {
        super(mutable);

        this.image = toCopy.getImage();
        this.pull = toCopy.getPull();
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == BowTextureEntry.class) {
            BowTextureEntry otherEntry = (BowTextureEntry) other;
            return areImagesEqual(this.image, otherEntry.image) && isClose(this.pull, otherEntry.pull);
        } else {
            return false;
        }
    }

    @Override
    public BowTextureEntry copy(boolean mutable) {
        return new BowTextureEntry(this, mutable);
    }

    public void save(BitOutput output) {
        output.addDouble(pull);
        KciTexture.saveImage(output, image);
    }

    public BufferedImage getImage() {
        // I would like to deep copy the image, but that sounds like a performance hazard
        return image;
    }

    public double getPull() {
                          return pull;
                                      }

    public void setImage(BufferedImage newImage) {
        Checks.notNull(newImage);
        assertMutable();
        this.image = newImage;
    }

    public void setPull(double newPull) {
        assertMutable();
        this.pull = newPull;
    }

    public void validate() throws ValidationException, ProgrammingValidationException {
        if (pull < 0.0) throw new ValidationException("Pull can't be negative");
        if (pull > 1.0) throw new ValidationException("Pull can be at most 1.0");
        Validation.scope("Image", () -> KciTexture.validateImage(image));
    }
}
