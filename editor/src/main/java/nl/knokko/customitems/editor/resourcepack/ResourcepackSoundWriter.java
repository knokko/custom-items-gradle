package nl.knokko.customitems.editor.resourcepack;

import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.sound.CustomSoundTypeValues;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

class ResourcepackSoundWriter {

    private final ItemSet itemSet;
    private final ZipOutputStream zipOutput;

    ResourcepackSoundWriter(ItemSet itemSet, ZipOutputStream zipOutput) {
        this.itemSet = itemSet;
        this.zipOutput = zipOutput;
    }

    void writeSoundsJson() throws IOException {
        ZipEntry jsonEntry = new ZipEntry("assets/minecraft/sounds.json");
        zipOutput.putNextEntry(jsonEntry);

        PrintWriter jsonWriter = new PrintWriter(zipOutput);
        jsonWriter.println("{");

        boolean hasPrevious = false;
        for (CustomSoundTypeValues soundType : itemSet.getSoundTypes()) {

            // All sound entries, except the first one, should start by adding a comma to separate it from the previous
            // sound entry
            if (hasPrevious) jsonWriter.println(",");
            hasPrevious = true;

            jsonWriter.println("  \"kci_" + soundType.getName() + "\": {");
            jsonWriter.println("    \"category\": \"" + soundType.getSoundCategory().name().toLowerCase(Locale.ROOT) + "\",");
            jsonWriter.println("    \"sounds\": [\"kci/" + soundType.getName() + "\"]");
            jsonWriter.print("  }");
        }
        jsonWriter.println();
        jsonWriter.println("}");

        jsonWriter.flush();
        zipOutput.closeEntry();
    }

    void writeSoundFiles() throws IOException {
        for (CustomSoundTypeValues soundType : itemSet.getSoundTypes()) {
            ZipEntry entry = new ZipEntry("assets/minecraft/sounds/kci/" + soundType.getName() + ".ogg");
            zipOutput.putNextEntry(entry);
            zipOutput.write(soundType.getOggData());
            zipOutput.flush();
            zipOutput.closeEntry();
        }
    }
}
