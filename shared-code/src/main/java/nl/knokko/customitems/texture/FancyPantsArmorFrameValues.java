package nl.knokko.customitems.texture;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

import java.awt.image.BufferedImage;
import java.util.Objects;

import static nl.knokko.customitems.texture.BaseTextureValues.*;

public class FancyPantsArmorFrameValues extends ModelValues {

    public static FancyPantsArmorFrameValues load(BitInput input) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("FancyPantsArmorFrame", encoding);

        FancyPantsArmorFrameValues frame = new FancyPantsArmorFrameValues(false);
        frame.layer1 = loadImage(input, true);
        frame.layer2 = loadImage(input, true);
        frame.emissivityLayer1 = input.readBoolean() ? loadImage(input, true) : null;
        frame.emissivityLayer2 = input.readBoolean() ? loadImage(input, true) : null;

        return frame;
    }

    private BufferedImage layer1, layer2, emissivityLayer1, emissivityLayer2;

    public FancyPantsArmorFrameValues(boolean mutable) {
        super(mutable);
        this.layer1 = new BufferedImage(64, 32, BufferedImage.TYPE_INT_ARGB);
        this.layer2 = new BufferedImage(64, 32, BufferedImage.TYPE_INT_ARGB);
        this.emissivityLayer1 = new BufferedImage(64, 32, BufferedImage.TYPE_INT_ARGB);
        this.emissivityLayer2 = new BufferedImage(64, 32, BufferedImage.TYPE_INT_ARGB);
    }

    public FancyPantsArmorFrameValues(FancyPantsArmorFrameValues toCopy, boolean mutable) {
        super(mutable);
        this.layer1 = toCopy.getLayer1();
        this.layer2 = toCopy.getLayer2();
        this.emissivityLayer1 = toCopy.getEmissivityLayer1();
        this.emissivityLayer2 = toCopy.getEmissivityLayer2();
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);

        saveImage(output, layer1);
        saveImage(output, layer2);

        output.addBoolean(emissivityLayer1 != null);
        if (emissivityLayer1 != null) saveImage(output, emissivityLayer1);
        output.addBoolean(emissivityLayer2 != null);
        if (emissivityLayer2 != null) saveImage(output, emissivityLayer2);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof FancyPantsArmorFrameValues) {
            FancyPantsArmorFrameValues otherFrame = (FancyPantsArmorFrameValues) other;
            return areImagesEqual(this.layer1, otherFrame.layer1) && areImagesEqual(this.layer2, otherFrame.layer2)
                    && areImagesEqual(this.emissivityLayer1, otherFrame.emissivityLayer1)
                    && areImagesEqual(this.emissivityLayer2, otherFrame.emissivityLayer2);
        } else return false;
    }

    @Override
    public FancyPantsArmorFrameValues copy(boolean mutable) {
        return new FancyPantsArmorFrameValues(this, mutable);
    }

    public BufferedImage getLayer1() {
        return layer1;
    }

    public BufferedImage getLayer2() {
        return layer2;
    }

    public BufferedImage getEmissivityLayer1() {
        return emissivityLayer1;
    }

    public BufferedImage getEmissivityLayer2() {
        return emissivityLayer2;
    }

    public void setLayer1(BufferedImage layer1) {
        assertMutable();
        this.layer1 = Objects.requireNonNull(layer1);
    }

    public void setLayer2(BufferedImage layer2) {
        assertMutable();
        this.layer2 = Objects.requireNonNull(layer2);
    }

    public void setEmissivityLayer1(BufferedImage emissivityLayer1) {
        assertMutable();
        this.emissivityLayer1 = emissivityLayer1;
    }

    public void setEmissivityLayer2(BufferedImage emissivityLayer2) {
        assertMutable();
        this.emissivityLayer2 = emissivityLayer2;
    }

    private void checkSize(BufferedImage image, String description) throws ValidationException {
        if (image.getWidth() != 64) {
            throw new ValidationException("Width of " + description + " must be 64, but is " + image.getWidth());
        }
        if (image.getHeight() != 32) {
            throw new ValidationException("Height of " + description + " must be 32, but is " + image.getHeight());
        }
    }

    public void validate(FancyPantsArmorTextureValues.Emissivity emissivity) throws ValidationException, ProgrammingValidationException {
        if (layer1 == null) throw new ProgrammingValidationException("No layer1 texture");
        checkSize(layer1, "layer1");
        if (layer2 == null) throw new ProgrammingValidationException("No layer2 texture");
        checkSize(layer2, "layer2");
        if (emissivity == FancyPantsArmorTextureValues.Emissivity.PARTIAL) {
            if (emissivityLayer1 == null) throw new ValidationException("You need to choose an emissivity texture for layer1");
            checkSize(emissivityLayer1, "emissivity of layer1");
            if (emissivityLayer2 == null) throw new ValidationException("You need to choose an emissivity texture for layer2");
            checkSize(emissivityLayer2, "emissivity of layer2");
        }
    }
}
