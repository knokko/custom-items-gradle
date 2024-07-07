package nl.knokko.customitems.editor.resourcepack.geyser;

import nl.knokko.customitems.container.KciContainer;
import nl.knokko.customitems.itemset.ItemSet;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static nl.knokko.customitems.util.ColorCodes.stripColorCodes;

class GeyserPackUIGenerator {

    private final ItemSet itemSet;
    private final ZipOutputStream zipOutput;

    GeyserPackUIGenerator(ItemSet itemSet, ZipOutputStream zipOutput) {
        this.itemSet = itemSet;
        this.zipOutput = zipOutput;
    }

    void addChestPanelControls(PrintWriter uiWriter, boolean large) {
        boolean hasLarge = itemSet.containers.stream().anyMatch(
                container -> container.getHeight() > 3 && container.getOverlayTexture() != null
        );
        boolean hasSmall = itemSet.containers.stream().anyMatch(
                container -> container.getHeight() <= 3 && container.getOverlayTexture() != null
        );

        if (large && !hasLarge) return;
        if (!large && !hasSmall) return;

        if (large) {
            uiWriter.println("\t\"large_chest_panel/root_panel/chest_panel\": {");
        } else {
            uiWriter.println("\t\"small_chest_panel/root_panel/chest_panel\": {");
        }
        uiWriter.println("\t\t\"modifications\": [");

        boolean isFirst = true;
        for (KciContainer container : itemSet.containers) {
            BufferedImage overlay = container.getOverlayTexture();
            if (overlay == null) continue;

            if (!isFirst) uiWriter.println(',');
            isFirst = false;

            uiWriter.println("\t\t\t{");
            uiWriter.println("\t\t\t\t\"array_name\": \"controls\",");
            uiWriter.println("\t\t\t\t\"operation\": \"insert_front\",");
            uiWriter.println("\t\t\t\t\"value\": { \"kci_overlay_" + container.getResourceName() + "\": {");

            String indent = "\t\t\t\t\t";
            uiWriter.println(indent + "\"type\": \"image\",");
            uiWriter.println(indent + "\"layer\": 6,");
            uiWriter.println(indent + "\"texture\": \"textures/kci/ui/" + container.getResourceName() + "\",");
            uiWriter.println(indent + "\"anchor_from\": \"top_left\",");
            uiWriter.println(indent + "\"anchor_to\": \"top_left\",");
            uiWriter.println(indent + "\"$kci_title\": \"$container_title\",");

            String title = "\\u" +
                    Integer.toHexString(KciContainer.OVERLAY_BASE_CHAR).toUpperCase(Locale.ROOT) +
                    "\\u" +
                    Integer.toHexString(container.getOverlayChar()).toUpperCase(Locale.ROOT) +
                    stripColorCodes(container.getSelectionIcon().getDisplayName());

            uiWriter.println(indent + "\"visible\": \"($kci_title = '" + title + "')\",");
            uiWriter.println(indent + "\"size\": [ " + overlay.getWidth() + ", " + overlay.getHeight() + " ],");
            uiWriter.println(indent + "\"offset\": [ -40, 1 ]");
            uiWriter.println("\t\t\t\t}}");
            uiWriter.print("\t\t\t}");
        }

        uiWriter.println();
        uiWriter.println("\t\t]");
        if (large && hasSmall) {
            uiWriter.println("\t},");
        } else {
            uiWriter.println("\t}");
        }
    }

    void useOverlayTextures() throws IOException {
        if (itemSet.containers.stream().noneMatch(container -> container.getOverlayTexture() != null)) return;

        zipOutput.putNextEntry(new ZipEntry("ui/chest_screen.json"));
        PrintWriter uiWriter = new PrintWriter(zipOutput);

        uiWriter.println("{");

        addChestPanelControls(uiWriter, true);
        addChestPanelControls(uiWriter, false);

        uiWriter.println("}");

        uiWriter.flush();
        zipOutput.closeEntry();
    }

    void writeOverlayTextures() throws IOException {
        for (KciContainer container : itemSet.containers) {
            BufferedImage overlay = container.getOverlayTexture();

            if (overlay != null) {
                zipOutput.putNextEntry(new ZipEntry("textures/kci/ui/" + container.getResourceName() + ".png"));
                ImageIO.write(overlay, "PNG", zipOutput);
                zipOutput.closeEntry();
            }
        }
    }
}
