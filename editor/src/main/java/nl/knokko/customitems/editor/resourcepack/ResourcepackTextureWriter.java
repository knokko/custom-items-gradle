package nl.knokko.customitems.editor.resourcepack;

import nl.knokko.customitems.item.CustomArmorValues;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.texture.*;

import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static nl.knokko.customitems.MCVersions.VERSION1_12;

class ResourcepackTextureWriter {

    private final ItemSet itemSet;
    private final int mcVersion;
    private final ZipOutputStream zipOutput;

    ResourcepackTextureWriter(ItemSet itemSet, int mcVersion, ZipOutputStream zipOutput) {
        this.itemSet = itemSet;
        this.mcVersion = mcVersion;
        this.zipOutput = zipOutput;
    }

    void writeBaseTextures() throws IOException {
        for (BaseTextureValues texture : itemSet.getTextures()) {

            String baseTextureName = texture.getName();
            if (texture instanceof BowTextureValues || texture instanceof CrossbowTextureValues) {
                baseTextureName += "_standby";

                List<BowTextureEntry> pullTextures;
                if (texture instanceof BowTextureValues) {
                    pullTextures = ((BowTextureValues) texture).getPullTextures();
                } else {
                    pullTextures = ((CrossbowTextureValues) texture).getPullTextures();
                }

                for (int pullIndex = 0; pullIndex < pullTextures.size(); pullIndex++) {
                    ZipEntry entry = new ZipEntry("assets/minecraft/textures/customitems/" + texture.getName()
                            + "_pulling_" + pullIndex + ".png");
                    zipOutput.putNextEntry(entry);
                    ImageIO.write(pullTextures.get(pullIndex).getImage(), "PNG", new MemoryCacheImageOutputStream(zipOutput));
                    zipOutput.closeEntry();
                }

                if (texture instanceof CrossbowTextureValues) {
                    CrossbowTextureValues cbt = (CrossbowTextureValues)  texture;

                    ZipEntry arrowEntry = new ZipEntry("assets/minecraft/textures/customitems/" + cbt.getName()
                            + "_arrow.png");
                    zipOutput.putNextEntry(arrowEntry);
                    ImageIO.write(cbt.getArrowImage(), "PNG", new MemoryCacheImageOutputStream(zipOutput));
                    zipOutput.closeEntry();

                    ZipEntry fireworkEntry = new ZipEntry("assets/minecraft/textures/customitems/" + cbt.getName()
                            + "_firework.png");
                    zipOutput.putNextEntry(fireworkEntry);
                    ImageIO.write(cbt.getFireworkImage(), "PNG", new MemoryCacheImageOutputStream(zipOutput));
                    zipOutput.closeEntry();
                }
            }

            ZipEntry entry = new ZipEntry("assets/minecraft/textures/customitems/" + baseTextureName + ".png");
            zipOutput.putNextEntry(entry);
            ImageIO.write(texture.getImage(), "PNG", new MemoryCacheImageOutputStream(zipOutput));
            zipOutput.closeEntry();
        }
    }

    void writeOptifineArmorTextures() throws IOException {
        String citPrefix;
        if (mcVersion <= VERSION1_12) {
            citPrefix = "assets/minecraft/mcpatcher/cit/";
        } else {
            citPrefix = "assets/minecraft/optifine/cit/";
        }

        for (ArmorTextureValues armorTexture : itemSet.getArmorTextures()) {
            String prefix = citPrefix + "customarmor/" + armorTexture.getName() + "/";
            ZipEntry firstLayerEntry = new ZipEntry(prefix + "layer_1.png");
            zipOutput.putNextEntry(firstLayerEntry);
            ImageIO.write(
                    armorTexture.getLayer1(),
                    "PNG",
                    new MemoryCacheImageOutputStream(zipOutput)
            );
            zipOutput.closeEntry();

            ZipEntry secondLayerEntry = new ZipEntry(prefix + "layer_2.png");
            zipOutput.putNextEntry(secondLayerEntry);
            ImageIO.write(
                    armorTexture.getLayer2(),
                    "PNG",
                    new MemoryCacheImageOutputStream(zipOutput)
            );
            zipOutput.closeEntry();
        }

        // Link the custom armor to their textures
        for (CustomItemValues item : itemSet.getItems()) {
            if (item instanceof CustomArmorValues) {

                CustomArmorValues armor = (CustomArmorValues) item;
                if (armor.getArmorTexture() != null) {

                    ArmorTextureValues wornTexture = armor.getArmorTexture();
                    String prefix = citPrefix + "customarmor/" + wornTexture.getName() + "/";
                    ZipEntry armorEntry = new ZipEntry(prefix + armor.getName() + ".properties");
                    zipOutput.putNextEntry(armorEntry);

                    PrintWriter propertyWriter = new PrintWriter(zipOutput);
                    propertyWriter.println("type=armor");
                    String vanillaName = armor.getItemType().getModelName14();
                    propertyWriter.println("items=" + vanillaName);
                    vanillaName = armor.getItemType().getTextureName12();
                    String vanillaMaterial = vanillaName.substring(0, vanillaName.indexOf('_'));
                    propertyWriter.println("texture." + vanillaMaterial + "_layer_1=layer_1");
                    propertyWriter.println("texture." + vanillaMaterial + "_layer_2=layer_2");
                    propertyWriter.println("nbt.KnokkosCustomItems.Name=" + armor.getName());
                    propertyWriter.flush();

                    zipOutput.closeEntry();
                }
            }
        }
    }
}
