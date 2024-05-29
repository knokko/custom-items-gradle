package nl.knokko.customitems.editor.resourcepack.geyser;

import nl.knokko.customitems.item.KciItemType;
import nl.knokko.customitems.item.KciItem;
import nl.knokko.customitems.itemset.ItemSet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class GeyserMappingsGenerator {

    private final ItemSet itemSet;
    private final OutputStream output;

    public GeyserMappingsGenerator(ItemSet itemSet, OutputStream output) {
        this.itemSet = itemSet;
        this.output = output;
    }

    private String getVanillaName(KciItem item) {
        if (item.getItemType() == KciItemType.OTHER) {
            return "minecraft:" + item.getOtherMaterial().name().toLowerCase(Locale.ROOT);
        } else return "minecraft:" + item.getItemType().getModelName14();
    }

    public void writeItemMappings() throws IOException {
        Set<String> vanillaItems = new HashSet<>();
        for (KciItem item : itemSet.items) {
            vanillaItems.add(getVanillaName(item));
        }

        PrintWriter jsonWriter = new PrintWriter(output);
        jsonWriter.println("{");
        jsonWriter.println("    \"format_version\": \"1\",");
        jsonWriter.println("    \"items\": {");

        int vanillaCounter = 0;
        for (String vanillaItem : vanillaItems) {
            vanillaCounter += 1;
            jsonWriter.println("        \"" + vanillaItem + "\": [");

            boolean isFirst = true;
            for (KciItem item : itemSet.items) {
                if (getVanillaName(item).equals(vanillaItem)) {
                    if (!isFirst) jsonWriter.println(',');
                    isFirst = false;
                    jsonWriter.println("            {");

                    String prefix = "                ";
                    jsonWriter.println(prefix + "\"name\": \"kci_" + item.getName() + "\",");
                    jsonWriter.println(prefix + "\"allow_offhand\": " + !item.isTwoHanded() + ",");
                    jsonWriter.println(prefix + "\"icon\": \"kci_" + item.getTexture().getName() + "\",");
                    jsonWriter.println(prefix + "\"custom_model_data\": " + item.getItemDamage());

                    jsonWriter.print("            }");
                }
            }

            jsonWriter.println();
            jsonWriter.print("        ]");
            if (vanillaCounter != vanillaItems.size()) jsonWriter.print(',');
            jsonWriter.println();
        }

        jsonWriter.println("    }");
        jsonWriter.println("}");
        jsonWriter.flush();
    }
}
