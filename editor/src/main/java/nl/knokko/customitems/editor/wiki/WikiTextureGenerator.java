package nl.knokko.customitems.editor.wiki;

import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.texture.KciTexture;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class WikiTextureGenerator {

    private final ItemSet itemSet;

    public WikiTextureGenerator(ItemSet itemSet) {
        this.itemSet = itemSet;
    }

    public void generate(File texturesFolder) throws IOException {
        if (!texturesFolder.isDirectory() && !texturesFolder.mkdirs()) throw new IOException("Failed to create the textures folder");

        for (KciTexture texture : itemSet.textures) {
            ImageIO.write(texture.getImage(), "PNG", new File(texturesFolder + "/" + texture.getName() + ".png"));
        }
    }
}
