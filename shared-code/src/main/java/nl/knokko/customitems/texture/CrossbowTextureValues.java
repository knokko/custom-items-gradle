package nl.knokko.customitems.texture;

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
}
