package nl.knokko.customitems.editor.set.item.texture;

import nl.knokko.customitems.texture.NamedImage;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CrossbowTextures extends NamedImage {

    private final List<PullTexture> pullTextures;

    private BufferedImage arrowImage;
    private BufferedImage fireworkImage;

    public CrossbowTextures(
            String name, List<PullTexture> pullTextures,
            BufferedImage standbyImage, BufferedImage arrowImage, BufferedImage fireworkImage
    ) {
        super(name, standbyImage);
        this.pullTextures = new ArrayList<>(pullTextures);
        this.arrowImage = arrowImage;
        this.fireworkImage = fireworkImage;
    }

    public CrossbowTextures(BitInput input) throws IOException {
        super(input, true);

        int numPullTextures = input.readInt();
        this.pullTextures = new ArrayList<>(numPullTextures);
        for (int counter = 0; counter < numPullTextures; counter++) {
            double pull = input.readDouble();
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(input.readByteArray()));
            this.pullTextures.add(new PullTexture(image, pull));
        }

        this.arrowImage = ImageIO.read(new ByteArrayInputStream(input.readByteArray()));
        this.fireworkImage = ImageIO.read(new ByteArrayInputStream(input.readByteArray()));
    }

    @Override
    public void save(BitOutput output, boolean shouldCompress) throws IOException {
        if (!shouldCompress) throw new UnsupportedOperationException("Crossbow textures are always compressed");

        super.save(output, true);
        output.addInt(pullTextures.size());
        for (PullTexture pullTexture : pullTextures) {
            output.addDouble(pullTexture.pull);
            saveImage(output, pullTexture.image);
        }

        saveImage(output, this.arrowImage);
        saveImage(output, this.fireworkImage);
    }

    private void saveImage(BitOutput output, BufferedImage image) throws IOException {
        ByteArrayOutputStream imageOutput = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", imageOutput);
        output.addByteArray(imageOutput.toByteArray());
    }

    public List<PullTexture> getPullTextures() {
        return new ArrayList<>(pullTextures);
    }

    public BufferedImage getArrowImage() {
        return arrowImage;
    }

    public BufferedImage getFireworkImage() {
        return fireworkImage;
    }

    public void setPullTextures(List<PullTexture> newPullTextures) {
        this.pullTextures.clear();
        this.pullTextures.addAll(newPullTextures);
    }

    public void setArrowImage(BufferedImage newArrowImage) {
        this.arrowImage = newArrowImage;
    }

    public void setFireworkImage(BufferedImage newFireworkImage) {
        this.fireworkImage = newFireworkImage;
    }

    public static class PullTexture {

        private BufferedImage image;
        private double pull;

        public PullTexture(BufferedImage image, double pull) {
            this.image = image;
            this.pull = pull;
        }

        public BufferedImage getImage() {
            return image;
        }

        public double getPull() {
            return pull;
        }

        public void setImage(BufferedImage newImage) {
            this.image = newImage;
        }

        public void setPull(double newPull) {
            this.pull = newPull;
        }
    }
}
