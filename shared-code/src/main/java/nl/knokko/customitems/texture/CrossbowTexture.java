package nl.knokko.customitems.texture;

import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.model.Mutability;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class CrossbowTexture extends KciTexture {

    protected List<BowTextureEntry> pullTextures;

    protected BufferedImage arrowImage;
    protected BufferedImage fireworkImage;

    public CrossbowTexture(boolean mutable) {
        super(mutable);

        this.pullTextures = new ArrayList<>(3);
        this.pullTextures.add(BowTextureEntry.createQuick(null, 0.0).copy(false));
        this.pullTextures.add(BowTextureEntry.createQuick(null, 0.58).copy(false));
        this.pullTextures.add(BowTextureEntry.createQuick(null, 1.0).copy(false));
    }

    public CrossbowTexture(CrossbowTexture toCopy, boolean mutable) {
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
        if (other.getClass() == CrossbowTexture.class) {
            CrossbowTexture otherTexture = (CrossbowTexture) other;
            return this.name.equals(otherTexture.name) && areImagesEqual(this.image, otherTexture.image)
                    && this.pullTextures.equals(otherTexture.pullTextures)
                    && areImagesEqual(this.arrowImage, otherTexture.arrowImage)
                    && areImagesEqual(this.fireworkImage, otherTexture.fireworkImage);
        } else {
            return false;
        }
    }

    @Override
    public CrossbowTexture copy(boolean mutable) {
        return new CrossbowTexture(this, mutable);
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

    @Override
    public void validateExportVersion(int version) throws ValidationException {
        if (version < MCVersions.VERSION1_14) {
            throw new ValidationException("Minecraft " + MCVersions.createString(version) + " doesn't have crossbows");
        }
    }
}
