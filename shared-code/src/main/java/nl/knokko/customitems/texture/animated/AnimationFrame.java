package nl.knokko.customitems.texture.animated;

import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.util.Checks;

public class AnimationFrame extends ModelValues {

    public static AnimationFrame createQuick(String imageLabel, int duration) {
        AnimationFrame result = new AnimationFrame(true);
        result.setImageLabel(imageLabel);
        result.setDuration(duration);
        return result;
    }

    private String imageLabel;
    private int duration;

    public AnimationFrame(boolean mutable) {
        super(mutable);
        this.imageLabel = "";
        this.duration = 1;
    }

    public AnimationFrame(AnimationFrame toCopy, boolean mutable) {
        super(mutable);
        this.imageLabel = toCopy.getImageLabel();
        this.duration = toCopy.getDuration();
    }

    @Override
    public AnimationFrame copy(boolean mutable) {
        return new AnimationFrame(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof AnimationFrame) {
            AnimationFrame otherFrame = (AnimationFrame) other;
            return this.imageLabel.equals(otherFrame.imageLabel) && this.duration == otherFrame.duration;
        } else {
            return false;
        }
    }

    public String getImageLabel() {
        return imageLabel;
    }

    public int getDuration() {
        return duration;
    }

    public void setImageLabel(String imageLabel) {
        assertMutable();
        Checks.notNull(imageLabel);
        this.imageLabel = imageLabel;
    }

    public void setDuration(int duration) {
        assertMutable();
        this.duration = duration;
    }
}
