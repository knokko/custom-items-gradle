package nl.knokko.customitems.editor.resourcepack;

import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.texture.FancyPantsArmorFrameValues;
import nl.knokko.customitems.texture.FancyPantsArmorTextureValues;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

class ResourcepackFancyPants {

    private final ItemSet itemSet;
    private final ZipOutputStream zipOutput;

    public ResourcepackFancyPants(ItemSet itemSet, ZipOutputStream zipOutput) {
        this.itemSet = itemSet;
        this.zipOutput = zipOutput;
    }

    public void copyShaderAndLicense() throws IOException {
        if (!itemSet.fancyPants.isEmpty()) {
            String[][] pathsToCopy = {
                    {
                            "ancientking/fancypants/rendertype_armor_cutout_no_cull.fsh",
                            "assets/minecraft/shaders/core/rendertype_armor_cutout_no_cull.fsh"
                    },
                    {
                            "ancientking/fancypants/rendertype_armor_cutout_no_cull.vsh",
                            "assets/minecraft/shaders/core/rendertype_armor_cutout_no_cull.vsh"
                    },
                    {
                            "ancientking/fancypants/rendertype_armor_cutout_no_cull.json",
                            "assets/minecraft/shaders/core/rendertype_armor_cutout_no_cull.json"
                    },
                    {
                            "ancientking/fancypants/LICENSE",
                            "assets/minecraft/shaders/core/FancyPantsLicense"
                    }
            };
            for (String[] copyPair : pathsToCopy) {
                String source = copyPair[0];
                String destination = copyPair[1];

                InputStream input = ResourcepackFancyPants.class.getClassLoader().getResourceAsStream(source);
                assert input != null;
                Scanner inputScanner = new Scanner(input);
                zipOutput.putNextEntry(new ZipEntry(destination));

                PrintWriter output = new PrintWriter(zipOutput);
                while (inputScanner.hasNextLine()) {
                    output.println(inputScanner.nextLine());
                }
                output.flush();
                inputScanner.close();

                zipOutput.closeEntry();
            }
        }
    }

    public void generateEmptyTextures() throws IOException {
        if (!itemSet.fancyPants.isEmpty()) {
            BufferedImage emptyImage = new BufferedImage(64, 32, BufferedImage.TYPE_INT_ARGB);
            String[] destinations = {
                    "assets/minecraft/textures/models/armor/leather_layer_1_overlay.png",
                    "assets/minecraft/textures/models/armor/leather_layer_2_overlay.png"
            };
            for (String destination : destinations) {
                zipOutput.putNextEntry(new ZipEntry(destination));
                ImageIO.write(emptyImage, "PNG", zipOutput);
                zipOutput.flush();
                zipOutput.closeEntry();
            }
        }
    }

    private void copyImage(String sourcePath, Graphics graphics) throws IOException {
        InputStream input = ResourcepackFancyPants.class.getClassLoader().getResourceAsStream(sourcePath);
        assert input != null;

        BufferedImage source = ImageIO.read(input);
        input.close();

        graphics.drawImage(source, 0, 0, null);
    }

    public void generateFullTextures() throws IOException {
        if (!itemSet.fancyPants.isEmpty()) {
            // TODO Allow armor textures to be larger
            int width = 64;
            int height = width / 2;

            int totalWidth = width;
            int maxNumFrames = 1;
            for (FancyPantsArmorTextureValues fpTexture : itemSet.fancyPants) {
                if (fpTexture.getFrames().size() > maxNumFrames) maxNumFrames = fpTexture.getFrames().size();
                if (fpTexture.getEmissivity() == FancyPantsArmorTextureValues.Emissivity.PARTIAL) totalWidth += 2 * width;
                else totalWidth += width;
            }
            int totalHeight = maxNumFrames * height;

            BufferedImage layer1 = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_ARGB);
            BufferedImage layer2 = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_ARGB);

            Graphics graphics1 = layer1.createGraphics();
            Graphics graphics2 = layer2.createGraphics();

            copyImage("ancientking/fancypants/template/leather_layer_1.png", graphics1);
            copyImage("ancientking/fancypants/template/leather_layer_2.png", graphics2);

            int currentX = width;
            for (FancyPantsArmorTextureValues fpTexture : itemSet.fancyPants) {

                int currentY = 0;
                for (FancyPantsArmorFrameValues frame : fpTexture.getFrames()) {
                    graphics1.drawImage(frame.getLayer1(), currentX, currentY, null);
                    graphics2.drawImage(frame.getLayer2(), currentX, currentY, null);
                    if (fpTexture.getEmissivity() == FancyPantsArmorTextureValues.Emissivity.PARTIAL) {
                        graphics1.drawImage(frame.getEmissivityLayer1(), currentX + width, currentY, null);
                        graphics2.drawImage(frame.getEmissivityLayer2(), currentX + width, currentY, null);
                    }
                    currentY += height;
                }

                int rgb1 = new Color(fpTexture.getRgb(), false).getRGB();
                int rgb2 = new Color(
                        fpTexture.getFrames().size(), fpTexture.getAnimationSpeed(),
                        fpTexture.shouldInterpolateAnimations() ? 1 : 0
                ).getRGB();
                int rgb3 = new Color(
                        fpTexture.getEmissivity().ordinal(),
                        fpTexture.usesLeatherTint() ? 1 : 0,
                        255
                ).getRGB();
                for (BufferedImage layer : new BufferedImage[]{ layer1, layer2 }) {
                    layer.setRGB(currentX, 0, rgb1);
                    layer.setRGB(currentX + 1, 0, rgb2);
                    layer.setRGB(currentX + 2, 0, rgb3);
                }
                currentX += width;
                if (fpTexture.getEmissivity() == FancyPantsArmorTextureValues.Emissivity.PARTIAL) currentX += width;
            }
            graphics1.dispose();
            graphics2.dispose();

            zipOutput.putNextEntry(new ZipEntry("assets/minecraft/textures/models/armor/leather_layer_1.png"));
            ImageIO.write(layer1, "PNG", zipOutput);
            zipOutput.closeEntry();
            zipOutput.putNextEntry(new ZipEntry("assets/minecraft/textures/models/armor/leather_layer_2.png"));
            ImageIO.write(layer2, "PNG", zipOutput);
            zipOutput.closeEntry();
        }
    }
}
