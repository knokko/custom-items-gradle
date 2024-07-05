package nl.knokko.customitems.editor.resourcepack.geyser;

import nl.knokko.customitems.block.KciBlock;
import nl.knokko.customitems.block.model.CustomBlockModel;
import nl.knokko.customitems.block.model.SidedBlockModel;
import nl.knokko.customitems.item.KciItem;
import nl.knokko.customitems.item.model.GeyserCustomModel;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.texture.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;
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

    private void writeCustomModelTexture(GeyserCustomModel model) throws IOException {
        zipOutput.putNextEntry(new ZipEntry("textures/kci/models/" + model.attachableId + ".png"));
        zipOutput.write(model.textureFile);
        zipOutput.closeEntry();
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

        for (KciItem item : itemSet.items) {
            if (item.getGeyserModel() != null) writeCustomModelTexture(item.getGeyserModel());
        }

        for (KciBlock block : itemSet.blocks) {
            if (block.getModel() instanceof CustomBlockModel) {
                GeyserCustomModel geyserModel = ((CustomBlockModel) block.getModel()).getGeyserModel();
                if (geyserModel != null) {
                    writeCustomModelTexture(geyserModel);
                }
            }
            if (block.getModel() instanceof SidedBlockModel) {
                SidedBlockModel model = (SidedBlockModel) block.getModel();
                SidedBlockModel.TexturePair[] images = model.getTexturePairs();
                BufferedImage firstImage = images[0].texture.get().getImage();
                int baseWidth = firstImage.getWidth();
                int baseHeight = firstImage.getHeight();

                BufferedImage textureAtlas = new BufferedImage(3 * baseWidth, 2 * baseHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics2D graphics = textureAtlas.createGraphics();
                graphics.drawImage(firstImage, 0, 0, null);
                graphics.drawImage(images[1].texture.get().getImage(), baseWidth, 0, null);
                graphics.drawImage(images[2].texture.get().getImage(), 2 * baseWidth, 0, null);
                graphics.drawImage(images[3].texture.get().getImage(), 0, baseHeight, null);
                graphics.drawImage(images[4].texture.get().getImage(), baseWidth, baseHeight, null);
                graphics.drawImage(images[5].texture.get().getImage(), 2 * baseWidth, baseHeight, null);
                graphics.dispose();

                writeTexture("sided/" + block.getName(), textureAtlas);
            }
        }

        for (FancyPantsTexture armorTexture : itemSet.fancyPants) {
            writeTexture("armor/fp_" + armorTexture.getName() + "1", armorTexture.getFrames().get(0).getLayer1());
            writeTexture("armor/fp_" + armorTexture.getName() + "2", armorTexture.getFrames().get(0).getLayer2());
        }

        for (ArmorTexture armorTexture : itemSet.armorTextures) {
            writeTexture("armor/op_" + armorTexture.getName() + "1", armorTexture.getLayer1());
            writeTexture("armor/op_" + armorTexture.getName() + "2", armorTexture.getLayer2());
        }
    }

    public void writeTexturesJsons() throws IOException {
        writeTexturesJson("item_texture", "atlas.items");
        if (!itemSet.blocks.isEmpty()) {
            writeTexturesJson("terrain_texture", "atlas.terrain");
        }
    }

    private void writeTexturesJson(String fileName, String textureName) throws IOException {
        zipOutput.putNextEntry(new ZipEntry("textures/" + fileName + ".json"));
        PrintWriter jsonWriter = new PrintWriter(zipOutput);
        jsonWriter.println("{");
        jsonWriter.println("    \"resource_pack_name\": \"kci_textures\",");
        jsonWriter.println("    \"texture_name\": \"" + textureName + "\",");
        jsonWriter.println("    \"texture_data\": {");

        List<KciBlock> sidedBlocks = itemSet.blocks.stream().filter(
                block -> block.getModel() instanceof SidedBlockModel
        ).collect(Collectors.toList());

        List<KciBlock> customBlocks = itemSet.blocks.stream().filter(
                block -> block.getModel() instanceof CustomBlockModel &&
                        ((CustomBlockModel) block.getModel()).getGeyserModel() != null
        ).collect(Collectors.toList());

        int counter = 1;
        for (KciTexture texture : itemSet.textures) {
            String texturePath = "textures/kci/" + texture.getName();
            if (texture instanceof BowTexture) texturePath += "_standby";
            jsonWriter.println("        \"kci_" + texture.getName() + "\": {");
            jsonWriter.println("            \"textures\": \"" + texturePath + "\"");
            jsonWriter.print("        }");
            if (counter != itemSet.textures.size() || !sidedBlocks.isEmpty() || !customBlocks.isEmpty()) jsonWriter.print(",");
            jsonWriter.println();

            counter += 1;
        }

        counter = 1;
        for (KciBlock block : sidedBlocks) {

            jsonWriter.println("        \"sided_kci_" + block.getName() + "\": {");
            jsonWriter.println("            \"textures\": \"textures/kci/models/" + block.getName() + "\"");
            jsonWriter.print("        }");
            if (counter != sidedBlocks.size() || !customBlocks.isEmpty()) jsonWriter.print(',');
            jsonWriter.println();

            counter += 1;
        }

        counter = 1;
        for (KciBlock block : customBlocks) {
            String attachableId = ((CustomBlockModel) block.getModel()).getGeyserModel().attachableId;

            jsonWriter.println("        \"custom_kci_" + block.getName() + "\": {");
            jsonWriter.println("            \"textures\": \"textures/kci/models/" + attachableId + "\"");
            jsonWriter.print("        }");
            if (counter != customBlocks.size()) jsonWriter.print(',');
            jsonWriter.println();

            counter += 1;
        }

        jsonWriter.println("    }");
        jsonWriter.println("}");
        jsonWriter.flush();
        zipOutput.closeEntry();
    }
}
