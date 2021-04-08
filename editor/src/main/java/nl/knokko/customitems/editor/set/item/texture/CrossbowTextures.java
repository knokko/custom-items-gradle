package nl.knokko.customitems.editor.set.item.texture;

import nl.knokko.customitems.editor.set.item.NamedImage;

import java.awt.image.BufferedImage;
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
