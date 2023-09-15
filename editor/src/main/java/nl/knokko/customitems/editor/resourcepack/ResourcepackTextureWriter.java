package nl.knokko.customitems.editor.resourcepack;

import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.item.CustomArmorValues;
import nl.knokko.customitems.item.CustomElytraValues;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.texture.*;
import nl.knokko.customitems.texture.animated.AnimatedTextureValues;
import nl.knokko.customitems.texture.animated.AnimationFrameValues;
import nl.knokko.customitems.texture.animated.AnimationImageValues;

import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static nl.knokko.customitems.MCVersions.VERSION1_12;

class ResourcepackTextureWriter {

    private final ItemSet itemSet;
    private final ZipOutputStream zipOutput;

    ResourcepackTextureWriter(ItemSet itemSet, ZipOutputStream zipOutput) {
        this.itemSet = itemSet;
        this.zipOutput = zipOutput;
    }

    private static class ScheduledResource {

        private final ZipEntry entry;
        private final Future<byte[]> data;

        ScheduledResource(ZipEntry entry, Future<byte[]> data) {
            this.entry = entry;
            this.data = data;
        }
    }

    private Future<byte[]> futureImage(ExecutorService threadPool, BufferedImage image) {
        return threadPool.submit(() -> {
            ByteArrayOutputStream memoryOutput = new ByteArrayOutputStream();
            try {
                ImageIO.write(image, "PNG", memoryOutput);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return memoryOutput.toByteArray();
        });
    }

    void writeBaseTextures() throws IOException {
        ExecutorService threadPool = Executors.newFixedThreadPool(20);
        List<ScheduledResource> resources = new ArrayList<>(itemSet.getTextures().size());

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
                    BufferedImage image = pullTextures.get(pullIndex).getImage();
                    resources.add(new ScheduledResource(entry, futureImage(threadPool, image)));
                }

                if (texture instanceof CrossbowTextureValues) {
                    CrossbowTextureValues cbt = (CrossbowTextureValues)  texture;

                    ZipEntry arrowEntry = new ZipEntry("assets/minecraft/textures/customitems/" + cbt.getName()
                            + "_arrow.png");
                    resources.add(new ScheduledResource(arrowEntry, futureImage(threadPool, cbt.getArrowImage())));

                    ZipEntry fireworkEntry = new ZipEntry("assets/minecraft/textures/customitems/" + cbt.getName()
                            + "_firework.png");
                    resources.add(new ScheduledResource(fireworkEntry, futureImage(threadPool, cbt.getFireworkImage())));
                }
            }

            BufferedImage imageToExport;
            if (texture instanceof AnimatedTextureValues) {

                List<AnimationImageValues> images = ((AnimatedTextureValues) texture).copyImages(false);

                // Note that validation checks ensure that all images in the same animation have the same size
                int baseWidth = images.get(0).getImageReference().getWidth();
                int baseHeight = images.get(0).getImageReference().getHeight();

                int totalHeight = images.size() * baseHeight;
                imageToExport = new BufferedImage(baseWidth, totalHeight, BufferedImage.TYPE_INT_ARGB);
                for (int x = 0; x < baseWidth; x++) {
                    for (int y = 0; y < totalHeight; y++) {
                        int imageIndex = y / baseHeight;
                        int baseY = y % baseHeight;
                        imageToExport.setRGB(x, y, images.get(imageIndex).getImageReference().getRGB(x, baseY));
                    }
                }

                Map<String, Integer> imageIndices = new HashMap<>(images.size());
                for (int index = 0; index < images.size(); index++) {
                    imageIndices.put(images.get(index).getLabel(), index);
                }

                ZipEntry entry = new ZipEntry("assets/minecraft/textures/customitems/" + baseTextureName + ".png.mcmeta");
                resources.add(new ScheduledResource(entry, threadPool.submit(() -> {
                    ByteArrayOutputStream memoryOutput = new ByteArrayOutputStream();
                    PrintWriter metaWriter = new PrintWriter(memoryOutput);
                    metaWriter.println("{");
                    metaWriter.println("    \"animation\": {");
                    metaWriter.println("        \"frames\": [");
                    List<AnimationFrameValues> frames = ((AnimatedTextureValues) texture).getFrames();
                    for (int index = 0; index < frames.size(); index++) {
                        AnimationFrameValues frame = frames.get(index);
                        metaWriter.print("            { \"index\": " + imageIndices.get(frame.getImageLabel()) + ", \"time\": " + frame.getDuration() + "}");
                        if (index != frames.size() - 1) {
                            metaWriter.print(",");
                        }
                        metaWriter.println();
                    }
                    metaWriter.println("        ]");
                    metaWriter.println("    }");
                    metaWriter.println("}");
                    metaWriter.flush();
                    return memoryOutput.toByteArray();
                })));
            } else {
                imageToExport = texture.getImage();
            }

            ZipEntry entry = new ZipEntry("assets/minecraft/textures/customitems/" + baseTextureName + ".png");
            resources.add(new ScheduledResource(entry, futureImage(threadPool, imageToExport)));
        }

        for (ScheduledResource resource : resources) {
            zipOutput.putNextEntry(resource.entry);
            try {
                zipOutput.write(resource.data.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new IOException(e);
            }
            zipOutput.closeEntry();
        }

        threadPool.shutdown();
    }

    void writeOptifineArmorTextures() throws IOException {
        String citPrefix;
        if (itemSet.getExportSettings().getMcVersion() <= VERSION1_12) {
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

    void writeOptifineElytraTextures() throws IOException {
        String citPrefix;
        if (itemSet.getExportSettings().getMcVersion() <= VERSION1_12) {
            citPrefix = "assets/minecraft/mcpatcher/cit/";
        } else {
            citPrefix = "assets/minecraft/optifine/cit/";
        }

        for (CustomItemValues item : itemSet.getItems()) {
            if (item instanceof CustomElytraValues) {
                CustomElytraValues elytra = (CustomElytraValues) item;
                if (elytra.getWornElytraTexture() != null) {
                    String prefix = citPrefix + "customelytra/" + elytra.getName() + "/";

                    ZipEntry textureEntry = new ZipEntry(prefix + elytra.getName() + ".png");
                    zipOutput.putNextEntry(textureEntry);
                    ImageIO.write(elytra.getWornElytraTexture(), "PNG", new MemoryCacheImageOutputStream(zipOutput));
                    zipOutput.closeEntry();

                    ZipEntry propertiesEntry = new ZipEntry(prefix + elytra.getName() + ".properties");
                    zipOutput.putNextEntry(propertiesEntry);
                    PrintWriter propertyWriter = new PrintWriter(zipOutput);
                    propertyWriter.println("type=elytra");
                    propertyWriter.println("texture=" + elytra.getName());
                    propertyWriter.println("nbt.KnokkosCustomItems.Name=" + elytra.getName());
                    propertyWriter.flush();
                    zipOutput.closeEntry();
                }
            }
        }
    }

    void writeContainerOverlayTextures() throws IOException {
        for (CustomContainerValues container : itemSet.getContainers()) {
            if (container.getOverlayTexture() != null) {

                ZipEntry overlayTextureEntry = new ZipEntry("assets/minecraft/textures/customcontainers/overlay/" + container.getName() + ".png");
                zipOutput.putNextEntry(overlayTextureEntry);
                ImageIO.write(container.getOverlayTexture(), "PNG", new MemoryCacheImageOutputStream(zipOutput));
                zipOutput.closeEntry();
            }
        }

        if (itemSet.getContainers().stream().anyMatch(container -> container.getOverlayTexture() != null)) {
            ZipEntry blackTexture = new ZipEntry("assets/minecraft/textures/customcontainers/black.png");
            zipOutput.putNextEntry(blackTexture);
            ImageIO.write(new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB), "PNG", zipOutput);
            zipOutput.closeEntry();
        }
    }
}
