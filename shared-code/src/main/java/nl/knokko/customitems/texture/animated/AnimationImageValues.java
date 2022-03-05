package nl.knokko.customitems.texture.animated;

import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.texture.BaseTextureValues;
import nl.knokko.customitems.util.Checks;

import java.awt.image.BufferedImage;

public class AnimationImageValues extends ModelValues {

    public static AnimationImageValues createQuick(BufferedImage image, String label) {
        AnimationImageValues result = new AnimationImageValues(true);
        result.setImage(image);
        result.setLabel(label);
        return result;
    }

    private BufferedImage image;
    private String label;

    public AnimationImageValues(boolean mutable) {
        super(mutable);
        this.image = null;
        this.label = "";
    }

    public AnimationImageValues(AnimationImageValues toCopy, boolean mutable) {
        super(mutable);
        this.image = toCopy.copyImage();
        this.label = toCopy.getLabel();
    }

    @Override
    public AnimationImageValues copy(boolean mutable) {
        return new AnimationImageValues(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof AnimationImageValues) {
            AnimationImageValues otherImage = (AnimationImageValues) other;
            return BaseTextureValues.areImagesEqual(this.image, otherImage.image) && this.label.equals(otherImage.label);
        } else {
            return false;
        }
    }

    public BufferedImage getImageReference() {
        return this.image;
    }

    public BufferedImage copyImage() {
        BufferedImage copied = new BufferedImage(this.image.getWidth(), this.image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < this.image.getWidth(); x++) {
            for (int y = 0; y < this.image.getHeight(); y++) {
                copied.setRGB(x, y, this.image.getRGB(x, y));
            }
        }
        return copied;
    }

    public String getLabel() {
        return this.label;
    }

    public void setImage(BufferedImage image) {
        assertMutable();
        Checks.notNull(image);
        this.image = image;
    }

    public void setLabel(String label) {
        assertMutable();
        Checks.notNull(label);
        this.label = label;
    }
}
