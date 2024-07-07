package nl.knokko.customitems.editor.resourcepack;

import nl.knokko.customitems.container.KciContainer;
import nl.knokko.customitems.itemset.ItemSet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

class ResourcepackFontOverrider {

    private final ItemSet itemSet;
    private final ZipOutputStream zipOutput;

    ResourcepackFontOverrider(ItemSet itemSet, ZipOutputStream zipOutput) {
        this.itemSet = itemSet;
        this.zipOutput = zipOutput;
    }

    void overrideContainerOverlayChars() throws IOException {
        if (itemSet.containers.stream().anyMatch(container -> container.getOverlayTexture() != null)) {
            ZipEntry entry = new ZipEntry("assets/minecraft/font/default.json");
            zipOutput.putNextEntry(entry);

            PrintWriter jsonWriter = new PrintWriter(zipOutput);
            jsonWriter.println("{");
            jsonWriter.println("  \"providers\": [");

            for (KciContainer container : itemSet.containers) {
                if (container.getOverlayTexture() != null) {
                    writeFontEntry(
                            jsonWriter, "customcontainers/overlay/" + container.getResourceName(),
                            16, 105, container.getOverlayChar(), true
                    );
                }
            }

            writeFontEntry(jsonWriter, "customcontainers/black", -5000, -50, (char) KciContainer.OVERLAY_BASE_CHAR, false);
            jsonWriter.println("  ]");
            jsonWriter.println("}");

            jsonWriter.flush();

            zipOutput.closeEntry();
        }
    }

    private void writeFontEntry(
            PrintWriter jsonWriter, String filePath, int ascent, int height, char charToOverride, boolean endWithComma
    ) {
        jsonWriter.println("    {");
        jsonWriter.println("      \"type\": \"bitmap\",");
        jsonWriter.println("      \"file\": \"" + filePath + ".png\",");
        jsonWriter.println("      \"ascent\": " + ascent + ",");
        jsonWriter.println("      \"height\": " + height + ",");
        jsonWriter.println("      \"chars\": [\"\\u" + Integer.toHexString(charToOverride) + "\"]");
        jsonWriter.print("    }");
        if (endWithComma) jsonWriter.print(",");
        jsonWriter.println();
    }
}
