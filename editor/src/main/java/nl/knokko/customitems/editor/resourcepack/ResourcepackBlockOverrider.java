package nl.knokko.customitems.editor.resourcepack;

import nl.knokko.customitems.block.CustomBlockValues;
import nl.knokko.customitems.block.MushroomBlockMapping;
import nl.knokko.customitems.itemset.ItemSet;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Optional;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static nl.knokko.customitems.block.BlockConstants.MAX_BLOCK_ID;
import static nl.knokko.customitems.block.BlockConstants.MIN_BLOCK_ID;

class ResourcepackBlockOverrider {

    private final ItemSet itemSet;
    private final ZipOutputStream zipOutput;

    ResourcepackBlockOverrider(ItemSet itemSet, ZipOutputStream zipOutput) {
        this.itemSet = itemSet;
        this.zipOutput = zipOutput;
    }

    void overrideMushroomBlocks() throws IOException {
        for (MushroomBlockMapping.Type mushroomType : MushroomBlockMapping.Type.values()) {
            boolean hasSuchBlock = false;
            for (CustomBlockValues block : itemSet.getBlocks()) {
                if (MushroomBlockMapping.getType(block.getInternalID()) == mushroomType) {
                    hasSuchBlock = true;
                    break;
                }
            }

            if (hasSuchBlock) {
                ZipEntry blockStateEntry = new ZipEntry("assets/minecraft/blockstates/" + mushroomType.getResourceName() + ".json");
                zipOutput.putNextEntry(blockStateEntry);

                PrintWriter jsonWriter = new PrintWriter(zipOutput);
                jsonWriter.println("{");
                jsonWriter.println("    \"multipart\": [");

                // First write the vanilla entries
                for (MushroomBlockMapping.VanillaMushroomEntry vanillaEntry : MushroomBlockMapping.getVanillaEntries(mushroomType)) {
                    boolean[] directions = vanillaEntry.getDirections();
                    jsonWriter.println("        {");
                    jsonWriter.println("            \"when\": { \"down\": " + directions[0] + ", \"east\": " + directions[1] +
                            ", \"north\": " + directions[2] + ", \"south\": " + directions[3] + ", \"up\": " +
                            directions[4] + ", \"west\": " + directions[5] + " },");
                    jsonWriter.println("            \"apply\": { \"model\": \"block/lapisdemon/" +
                            mushroomType.getResourceName() + "/default_" + vanillaEntry.getFileName() + "\" }");
                    jsonWriter.println("        },");
                }

                // Then an empty line for the sake of readability
                jsonWriter.println();

                // And finally write the custom block entries
                for (int id = MIN_BLOCK_ID; id <= MAX_BLOCK_ID; id++) {
                    if (MushroomBlockMapping.getType(id) == mushroomType) {

                        boolean[] directions = MushroomBlockMapping.getDirections(id);
                        jsonWriter.println("        {");
                        jsonWriter.println("            \"when\": { \"down\": " + directions[0] + ", \"east\": " + directions[1] +
                                ", \"north\": " + directions[2] + ", \"south\": " + directions[3] + ", \"up\": " +
                                directions[4] + ", \"west\": " + directions[5] + " },");

                        Optional<CustomBlockValues> maybeBlock = itemSet.getBlock(id);
                        String blockResource = maybeBlock.map(
                                customBlockValues -> "customblocks/" + customBlockValues.getName()
                        ).orElseGet(() -> "block/lapisdemon/" + mushroomType.getResourceName() + "/default_true");

                        jsonWriter.println("            \"apply\": { \"model\": \"" + blockResource + "\" }");

                        boolean isLast = true;
                        for (int testId = id + 1; testId <= MAX_BLOCK_ID; testId++) {
                            if (MushroomBlockMapping.getType(testId) == mushroomType) {
                                isLast = false;
                                break;
                            }
                        }

                        if (isLast) {
                            jsonWriter.println("        }");
                        } else {
                            jsonWriter.println("        },");
                        }
                    }
                }

                // The finishing touch
                jsonWriter.println("    ]");
                jsonWriter.println("}");
                jsonWriter.flush();

                for (MushroomBlockMapping.VanillaMushroomEntry vanillaEntry : MushroomBlockMapping.getVanillaEntries(mushroomType)) {
                    ZipEntry vanillaModelEntry = new ZipEntry(
                            "assets/minecraft/models/block/lapisdemon/" + mushroomType.getResourceName() +
                                    "/default_" + vanillaEntry.getFileName() + ".json"
                    );
                    zipOutput.putNextEntry(vanillaModelEntry);
                    PrintWriter vanillaModelWriter = new PrintWriter(zipOutput);

                    InputStream defaultInput = ResourcepackBlockOverrider.class.getClassLoader().getResourceAsStream(
                            "lapisdemon/bonusblocks/defaultblocks/" + mushroomType.getResourceName() + "/" + vanillaEntry.getFileName() + ".json"
                    );
                    assert defaultInput != null;
                    Scanner defaultScanner = new Scanner(defaultInput);
                    while (defaultScanner.hasNextLine()) {
                        vanillaModelWriter.println(defaultScanner.nextLine());
                    }
                    vanillaModelWriter.flush();
                    defaultScanner.close();
                }
            }
        }
    }
}
