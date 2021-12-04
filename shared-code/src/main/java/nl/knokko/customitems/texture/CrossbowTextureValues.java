package nl.knokko.customitems.texture;

import nl.knokko.customitems.model.Mutability;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class CrossbowTextureValues extends BaseTextureValues {

    protected List<BowTextureEntry> pullTextures;

    protected BufferedImage arrowImage;
    protected BufferedImage fireworkImage;

    public CrossbowTextureValues(boolean mutable) {
        super(mutable);
    }

    public CrossbowTextureValues(CrossbowTextureValues toCopy, boolean mutable) {
        super(toCopy, mutable);
        this.pullTextures = toCopy.getPullTextures();
        this.arrowImage = toCopy.getArrowImage();
        this.fireworkImage = toCopy.getFireworkImage();
    }

    protected void loadCrossbow1(BitInput input) {
        loadBase1(input, true);
        loadPullTextures1(input);
        this.arrowImage = loadImage(input, true);
        this.fireworkImage = loadImage(input, true);
    }

    protected void loadPullTextures1(BitInput input) {
        int numPullTextures = input.readInt();
        this.pullTextures = new ArrayList<>(numPullTextures);
        for (int counter = 0; counter < numPullTextures; counter++) {
            pullTextures.add(BowTextureEntry.load1(input, true, false));
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == CrossbowTextureValues.class) {
            CrossbowTextureValues otherTexture = (CrossbowTextureValues) other;
            return this.name.equals(otherTexture.name) && areImagesEqual(this.image, otherTexture.image)
                    && this.pullTextures.equals(otherTexture.pullTextures)
                    && areImagesEqual(this.arrowImage, otherTexture.arrowImage)
                    && areImagesEqual(this.fireworkImage, otherTexture.fireworkImage);
        } else {
            return false;
        }
    }

    @Override
    public CrossbowTextureValues copy(boolean mutable) {
        return new CrossbowTextureValues(this, mutable);
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(ENCODING_CROSSBOW_1);
        saveCrossbow1(output);
    }

    protected void saveCrossbow1(BitOutput output) {
        saveBase1(output);
        savePullTextures1(output);
        saveImage(output, arrowImage);
        saveImage(output, fireworkImage);
    }

    protected void savePullTextures1(BitOutput output) {
        output.addInt(pullTextures.size());
        for (BowTextureEntry pullTexture : pullTextures) {
            pullTexture.save(output);
        }
    }

    public List<BowTextureEntry> getPullTextures() {
        return new ArrayList<>(pullTextures);
    }

    // This is a bit dangerous since the caller could modify it, which would be reflected to this crossbow texture...
    public BufferedImage getArrowImage() {
        return arrowImage;
    }

    // This is a bit dangerous since the caller could modify it, which would be reflected to this crossbow texture...
    public BufferedImage getFireworkImage() {
        return fireworkImage;
    }

    public void setPullTextures(List<BowTextureEntry> newPullTextures) {
        assertMutable();
        Checks.nonNull(newPullTextures);
        this.pullTextures = Mutability.createDeepCopy(newPullTextures, false);
    }

    // This is a bit dangerous since the caller can keep changing the image after this call
    public void setArrowImage(BufferedImage newArrowImage) {
        assertMutable();
        Checks.notNull(newArrowImage);
        this.arrowImage = newArrowImage;
    }

    // This is a bit dangerous since the caller can keep changing the image after this call
    public void setFireworkImage(BufferedImage newFireworkImage) {
        assertMutable();
        Checks.notNull(newFireworkImage);
        this.fireworkImage = newFireworkImage;
    }

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        super.validateIndependent();
        if (this.pullTextures == null) throw new ProgrammingValidationException("No pull textures");
        for (BowTextureEntry pullTexture : this.pullTextures) {
            if (pullTexture == null) throw new ProgrammingValidationException("Missing a pull texture");
            Validation.scope("Pull texture " + pullTexture.getPull(), pullTexture::validate);
        }
        Validation.scope("Arrow image", () -> validateImage(this.arrowImage));
        Validation.scope("Firework image", () -> validateImage(this.fireworkImage));
    }
}
