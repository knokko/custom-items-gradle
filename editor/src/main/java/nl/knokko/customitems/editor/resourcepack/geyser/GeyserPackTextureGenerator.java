package nl.knokko.customitems.editor.resourcepack.geyser;

import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.texture.KciTexture;
import nl.knokko.customitems.texture.BowTextureEntry;
import nl.knokko.customitems.texture.BowTexture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.lang.Math.abs;

public class GeyserPackTextureGenerator {

    private final ItemSet itemSet;
    private final ZipOutputStream zipOutput;

    public GeyserPackTextureGenerator(ItemSet itemSet, ZipOutputStream zipOutput) {
        this.itemSet = itemSet;
        this.zipOutput = zipOutput;
    }

    private void writeTexture(String name, BufferedImage image) throws IOException {
        zipOutput.putNextEntry(new ZipEntry("textures/kci/" + name + ".png"));
        ImageIO.write(image, "PNG", zipOutput);
        zipOutput.closeEntry();
    }

    private void writePullTexture(BowTexture texture, int entry, double pull) throws IOException {
        List<BowTextureEntry> pulls = texture.getPullTextures();
        String textureName = texture.getName() + "_pulling_" + entry;
        if (pulls.isEmpty()) {
            writeTexture(textureName, texture.getImage());
            return;
        }

        BowTextureEntry best = pulls.get(0);
        for (BowTextureEntry pullTexture : pulls) {
            if (abs(pullTexture.getPull() - pull) < abs(best.getPull() - pull)) best = pullTexture;
        }

        writeTexture(textureName, best.getImage());
    }

    private void writeBowTexture(BowTexture texture) throws IOException {
        writeTexture(texture.getName() + "_standby", texture.getImage());
        writePullTexture(texture, 0, 0.0);
        writePullTexture(texture, 1, 0.65);
        writePullTexture(texture, 2, 0.9);
    }

    public void writeTextures() throws IOException {
        // TODO Support crossbow textures
        for (KciTexture texture : itemSet.textures) {
            if (texture instanceof BowTexture) {
                writeBowTexture((BowTexture) texture);
            } else {
                writeTexture(texture.getName(), texture.getImage());
            }
        }
    }

    public void writeTexturesJson() throws IOException {
        zipOutput.putNextEntry(new ZipEntry("textures/item_texture.json"));
        PrintWriter jsonWriter = new PrintWriter(zipOutput);
        jsonWriter.println("{");
        jsonWriter.println("    \"resource_pack_name\": \"kci_textures\",");
        jsonWriter.println("    \"texture_name\": \"atlas.items\",");
        jsonWriter.println("    \"texture_data\": {");
        int counter = 1;
        for (KciTexture texture : itemSet.textures) {
            String texturePath = "textures/kci/" + texture.getName();
            if (texture instanceof BowTexture) texturePath += "_standby";
            jsonWriter.println("        \"kci_" + texture.getName() + "\": {");
            jsonWriter.println("            \"textures\": \"" + texturePath + "\"");
            jsonWriter.print("        }");
            if (counter != itemSet.textures.size()) jsonWriter.print(",");
            jsonWriter.println();

            counter += 1;
        }
        jsonWriter.println("    }");
        jsonWriter.println("}");
        jsonWriter.flush();
        zipOutput.closeEntry();
    }
}
