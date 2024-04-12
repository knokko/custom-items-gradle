package nl.knokko.customitems.editor.resourcepack.geyser;

import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.texture.BaseTextureValues;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class GeyserPackTextureGenerator {

    private final ItemSet itemSet;
    private final ZipOutputStream zipOutput;

    public GeyserPackTextureGenerator(ItemSet itemSet, ZipOutputStream zipOutput) {
        this.itemSet = itemSet;
        this.zipOutput = zipOutput;
    }

    public void writeTextures() throws IOException {
        // TODO Support (cross)bow textures
        for (BaseTextureValues texture : itemSet.getTextures()) {
            zipOutput.putNextEntry(new ZipEntry("textures/kci/" + texture.getName() + ".png"));
            ImageIO.write(texture.getImage(), "PNG", zipOutput);
            zipOutput.closeEntry();
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
        for (BaseTextureValues texture : itemSet.getTextures()) {
            jsonWriter.println("        \"kci_" + texture.getName() + "\": {");
            jsonWriter.println("            \"textures\": \"textures/kci/" + texture.getName() + "\"");
            jsonWriter.print("        }");
            if (counter != itemSet.getTextures().size()) jsonWriter.print(",");
            jsonWriter.println();

            counter += 1;
        }
        jsonWriter.println("    }");
        jsonWriter.println("}");
        jsonWriter.flush();
        zipOutput.closeEntry();
    }
}
