package nl.knokko.customitems.editor.resourcepack;

import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.texture.FancyPantsFrame;
import nl.knokko.customitems.texture.FancyPantsTexture;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static nl.knokko.customitems.MCVersions.VERSION1_21;

class ResourcepackFancyPants {

    private final ItemSet itemSet;
    private final ZipOutputStream zipOutput;

    public ResourcepackFancyPants(ItemSet itemSet, ZipOutputStream zipOutput) {
        this.itemSet = itemSet;
        this.zipOutput = zipOutput;
    }

    public void copyShaderAndLicense() throws IOException {
        if (!itemSet.fancyPants.isEmpty()) {
            String[][] pathsToCopy = getPathsToCopy();
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

    private String[][] getPathsToCopy() {
        String versionString = itemSet.getExportSettings().getMcVersion() >= VERSION1_21 ? "1.21" : "1.17";
        String[][] pathsToCopy = {
                {
                        "ancientking/fancypants/" + versionString + "/rendertype_armor_cutout_no_cull.fsh",
                        "assets/minecraft/shaders/core/rendertype_armor_cutout_no_cull.fsh"
                },
                {
                        "ancientking/fancypants/" + versionString + "/rendertype_armor_cutout_no_cull.vsh",
                        "assets/minecraft/shaders/core/rendertype_armor_cutout_no_cull.vsh"
                },
                {
                        "ancientking/fancypants/" + versionString + "/rendertype_armor_cutout_no_cull.json",
                        "assets/minecraft/shaders/core/rendertype_armor_cutout_no_cull.json"
                },
                {
                        "ancientking/fancypants/LICENSE",
                        "assets/minecraft/shaders/core/FancyPantsLicense"
                }
        };
        return pathsToCopy;
    }

    public void generateEmptyTextures() throws IOException {
        if (!itemSet.fancyPants.isEmpty()) {
            BufferedImage emptyImage = new BufferedImage(64, 32, BufferedImage.TYPE_INT_ARGB);
            String[] destinations;
            if (itemSet.getExportSettings().getMcVersion() >= VERSION1_21) {
                destinations = new String[] {
                        "assets/minecraft/textures/entity/equipment/humanoid/leather_overlay.png",
                        "assets/minecraft/textures/entity/equipment/humanoid_leggings/leather_overlay.png"
                };
            } else {
                destinations = new String[] {
                        "assets/minecraft/textures/models/armor/leather_layer_1_overlay.png",
                        "assets/minecraft/textures/models/armor/leather_layer_2_overlay.png"
                };
            }

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
            for (FancyPantsTexture fpTexture : itemSet.fancyPants) {
                if (fpTexture.getFrames().size() > maxNumFrames) maxNumFrames = fpTexture.getFrames().size();
                if (fpTexture.getEmissivity() == FancyPantsTexture.Emissivity.PARTIAL) totalWidth += 2 * width;
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
            for (FancyPantsTexture fpTexture : itemSet.fancyPants) {

                int currentY = 0;
                for (FancyPantsFrame frame : fpTexture.getFrames()) {
                    graphics1.drawImage(frame.getLayer1(), currentX, currentY, null);
                    graphics2.drawImage(frame.getLayer2(), currentX, currentY, null);
                    if (fpTexture.getEmissivity() == FancyPantsTexture.Emissivity.PARTIAL) {
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
                if (fpTexture.getEmissivity() == FancyPantsTexture.Emissivity.PARTIAL) currentX += width;
            }
            graphics1.dispose();
            graphics2.dispose();

            if (itemSet.getExportSettings().getMcVersion() >= VERSION1_21) {
                zipOutput.putNextEntry(new ZipEntry("assets/minecraft/textures/entity/equipment/humanoid/leather.png"));
            } else {
                zipOutput.putNextEntry(new ZipEntry("assets/minecraft/textures/models/armor/leather_layer_1.png"));
            }
            ImageIO.write(layer1, "PNG", zipOutput);
            zipOutput.closeEntry();
            if (itemSet.getExportSettings().getMcVersion() >= VERSION1_21) {
                zipOutput.putNextEntry(new ZipEntry("assets/minecraft/textures/entity/equipment/humanoid_leggings/leather.png"));
            } else {
                zipOutput.putNextEntry(new ZipEntry("assets/minecraft/textures/models/armor/leather_layer_2.png"));
            }
            ImageIO.write(layer2, "PNG", zipOutput);
            zipOutput.closeEntry();
        }
    }
}
