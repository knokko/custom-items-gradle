package nl.knokko.customitems.texture;

import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import java.awt.image.BufferedImage;

public class BowTextureEntry extends ModelValues {

    public static BowTextureEntry load1(BitInput input, boolean expectCompressed, boolean mutable) {
        BowTextureEntry result = new BowTextureEntry(mutable);
        result.pull = input.readDouble();
        result.image = BaseTextureValues.loadImage(input, expectCompressed);
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
    public BowTextureEntry copy(boolean mutable) {
        return new BowTextureEntry(this, mutable);
    }

    public void save(BitOutput output) {
        output.addDouble(pull);
        BaseTextureValues.saveImage(output, image);
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
        Validation.scope("Image", () -> BaseTextureValues.validateImage(image));
    }
}
