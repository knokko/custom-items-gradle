package nl.knokko.customitems.texture.animated;

import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.util.Checks;

public class AnimationFrameValues extends ModelValues {

    public static AnimationFrameValues createQuick(String imageLabel, int duration) {
        AnimationFrameValues result = new AnimationFrameValues(true);
        result.setImageLabel(imageLabel);
        result.setDuration(duration);
        return result;
    }

    private String imageLabel;
    private int duration;

    public AnimationFrameValues(boolean mutable) {
        super(mutable);
        this.imageLabel = "";
        this.duration = 1;
    }

    public AnimationFrameValues(AnimationFrameValues toCopy, boolean mutable) {
        super(mutable);
        this.imageLabel = toCopy.getImageLabel();
        this.duration = toCopy.getDuration();
    }

    @Override
    public AnimationFrameValues copy(boolean mutable) {
        return new AnimationFrameValues(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof AnimationFrameValues) {
            AnimationFrameValues otherFrame = (AnimationFrameValues) other;
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
