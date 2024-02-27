package nl.knokko.customitems.editor.resourcepack.geyser;

import nl.knokko.customitems.editor.resourcepack.PriorityZipOutputStream;
import nl.knokko.customitems.itemset.ItemSet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class GeyserPackGenerator {

    private final ItemSet itemSet;
    private final ZipOutputStream zipOutput;

    public GeyserPackGenerator(ItemSet itemSet, OutputStream rawOutputStream) {
        this.itemSet = itemSet;
        this.zipOutput = new PriorityZipOutputStream(rawOutputStream);
    }

    public void write() throws IOException {
        generateManifest();

        GeyserPackTextureGenerator textureGenerator = new GeyserPackTextureGenerator(itemSet, zipOutput);
        textureGenerator.writeTexturesJson();
        textureGenerator.writeTextures();

        zipOutput.flush();
        zipOutput.close();
    }

    private void generateManifest() throws IOException {
        zipOutput.putNextEntry(new ZipEntry("manifest.json"));
        PrintWriter jsonWriter = new PrintWriter(zipOutput);

        jsonWriter.println("{");
        jsonWriter.println("    \"format_version\": 2,");
        jsonWriter.println("    \"header\": {");
        jsonWriter.println("        \"description\": \"Resource pack for the Knokkos Custom Items plugin\",");
        jsonWriter.println("        \"name\": \"KCI Pack\",");
        jsonWriter.println("        \"uuid\": \"" + UUID.randomUUID() + "\",");
        jsonWriter.println("    }");
        jsonWriter.println("}");

        jsonWriter.flush();
        zipOutput.closeEntry();
    }
}
