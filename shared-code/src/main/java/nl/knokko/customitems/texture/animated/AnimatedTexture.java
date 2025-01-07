package nl.knokko.customitems.texture.animated;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.Mutability;
import nl.knokko.customitems.texture.KciTexture;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

import java.util.*;

public class AnimatedTexture extends KciTexture {

    public static AnimatedTexture load(BitInput input, ItemSet.Side side) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding < 1 || encoding > 2) throw new UnknownEncodingException("AnimatedTexture", encoding);

        AnimatedTexture result = new AnimatedTexture(false);
        result.name = input.readString();

        int numImages = input.readInt();
        for (int counter = 0; counter < numImages; counter++) {
            if (encoding == 1 || side == ItemSet.Side.EDITOR) {
                result.images.add(AnimationImage.createQuick(
                        loadImage(input, true), input.readString()
                ).copy(false));
            } else {
                AnimationImage image = new AnimationImage(true);
                image.setLabel(input.readString());
                result.images.add(image.copy(false));
            }
        }

        result.image = result.images.get(0).copyImage();

        int numFrames = input.readInt();
        for (int counter = 0; counter < numFrames; counter++) {
            result.frames.add(AnimationFrame.createQuick(
                    input.readString(), input.readInt()
            ).copy(false));
        }

        return result;
    }

    private List<AnimationImage> images;
    private List<AnimationFrame> frames;

    public AnimatedTexture(boolean mutable) {
        super(mutable);
        this.images = new ArrayList<>();
        this.frames = new ArrayList<>();
    }

    public AnimatedTexture(AnimatedTexture toCopy, boolean mutable) {
        super(toCopy, mutable);
        this.images = toCopy.copyImages(false);
        this.frames = toCopy.getFrames();
    }

    @Override
    public AnimatedTexture copy(boolean mutable) {
        return new AnimatedTexture(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof AnimatedTexture) {
            AnimatedTexture otherTexture = (AnimatedTexture) other;
            return this.images.equals(otherTexture.images) && this.frames.equals(otherTexture.frames);
        } else {
            return false;
        }
    }

    @Override
    public void save(BitOutput output, ItemSet.Side targetSide) {
        output.addByte(ENCODING_ANIMATED);
        output.addByte((byte) 2);

        output.addString(this.name);
        output.addInt(this.images.size());
        for (AnimationImage image : this.images) {
            if (targetSide == ItemSet.Side.EDITOR) saveImage(output, image.getImageReference());
            output.addString(image.getLabel());
        }

        output.addInt(this.frames.size());
        for (AnimationFrame frame : this.frames) {
            output.addString(frame.getImageLabel());
            output.addInt(frame.getDuration());
        }
    }

    public List<AnimationImage> copyImages(boolean mutable) {
        return Mutability.createDeepCopy(this.images, mutable);
    }

    /**
     * Do <b>NOT</b> modify the returned collection!!
     */
    public Collection<AnimationImage> getImageReferences() {
        return this.images;
    }

    public List<AnimationFrame> getFrames() {
        return new ArrayList<>(this.frames);
    }

    public void setImages(Collection<AnimationImage> newImages) {
        assertMutable();
        Checks.nonNull(newImages);
        this.images = Mutability.createDeepCopy(newImages, false);
        this.image = this.images.get(0).copyImage();
    }

    public void setFrames(Collection<AnimationFrame> newFrames) {
        assertMutable();
        Checks.nonNull(newFrames);
        this.frames = Mutability.createDeepCopy(newFrames, false);
    }

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        if (this.images == null) throw new ProgrammingValidationException("No images");
        if (this.images.isEmpty()) throw new ValidationException("You need at least 1 image");
        for (AnimationImage image : this.images) {
            if (image == null) throw new ProgrammingValidationException("Missing an image");
            if (image.getImageReference() == null) throw new ValidationException("An image entry misses its image");
            if (image.getLabel() == null) throw new ProgrammingValidationException("An image entry has a null label");
            if (image.getLabel().isEmpty()) throw new ValidationException("All image entries need a label");
        }
        Set<String> imageLabels = new HashSet<>();
        int imageWidth = this.images.get(0).getImageReference().getWidth();
        int imageHeight = this.images.get(0).getImageReference().getHeight();
        for (AnimationImage image : this.images) {
            if (imageLabels.contains(image.getLabel())) {
                throw new ValidationException("Image label '" + image.getLabel() + "' occurs more than once");
            }
            imageLabels.add(image.getLabel());
            if (image.getImageReference().getWidth() != imageWidth) throw new ValidationException("All images must have the same width");
            if (image.getImageReference().getHeight() != imageHeight) throw new ValidationException("All images must have the same height");
        }

        if (this.frames == null) throw new ProgrammingValidationException("No frames");
        if (this.frames.isEmpty()) throw new ValidationException("You need at least 1 frame");
        for (AnimationFrame frame : this.frames) {
            if (frame.getImageLabel() == null) throw new ProgrammingValidationException("A frame has a null label");
            if (frame.getImageLabel().isEmpty()) throw new ValidationException("All frames must have an image label");
            if (this.images.stream().noneMatch(candidate -> candidate.getLabel().equals(frame.getImageLabel()))) {
                throw new ValidationException("No image has label '" + frame.getImageLabel() + "'");
            }
            if (frame.getDuration() < 1) throw new ValidationException("All frame durations must be positive");
        }

        super.validateIndependent();
    }
}
