package nl.knokko.customitems.texture.animated;

import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.texture.KciTexture;
import nl.knokko.customitems.util.Checks;

import java.awt.image.BufferedImage;

public class AnimationImage extends ModelValues {

    public static AnimationImage createQuick(BufferedImage image, String label) {
        AnimationImage result = new AnimationImage(true);
        result.setImage(image);
        result.setLabel(label);
        return result;
    }

    private BufferedImage image;
    private String label;

    public AnimationImage(boolean mutable) {
        super(mutable);
        this.image = null;
        this.label = "";
    }

    public AnimationImage(AnimationImage toCopy, boolean mutable) {
        super(mutable);
        this.image = toCopy.copyImage();
        this.label = toCopy.getLabel();
    }

    @Override
    public AnimationImage copy(boolean mutable) {
        return new AnimationImage(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof AnimationImage) {
            AnimationImage otherImage = (AnimationImage) other;
            return KciTexture.areImagesEqual(this.image, otherImage.image) && this.label.equals(otherImage.label);
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
