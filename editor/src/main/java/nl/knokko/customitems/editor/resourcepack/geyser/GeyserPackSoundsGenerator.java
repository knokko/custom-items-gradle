package nl.knokko.customitems.editor.resourcepack.geyser;

import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.sound.KciSound;
import nl.knokko.customitems.sound.KciSoundType;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

class GeyserPackSoundsGenerator {

    private final ZipOutputStream zipOutput;
    private final ItemSet itemSet;

    GeyserPackSoundsGenerator(ZipOutputStream zipOutput, ItemSet itemSet) {
        this.zipOutput = zipOutput;
        this.itemSet = itemSet;
    }

    void generateSoundDefinitions() throws IOException {
        zipOutput.putNextEntry(new ZipEntry("sounds/sound_definitions.json"));
        PrintWriter writer = new PrintWriter(zipOutput);
        writer.println("{");
        writer.println("\t\"format_version\": \"1.14.0\",");
        writer.println("\t\"sound_definitions\": {");

        boolean isFirst = true;
        for (KciSoundType sound : itemSet.soundTypes) {
            if (!isFirst) writer.println(",");
            isFirst = false;

            writer.println("\t\t\"kci_" + sound.getName() + "\": {");
            writer.println("\t\t\t\"category\": \"neutral\",");
            writer.println("\t\t\t\"sounds\": [\"sounds/kci/" + sound.getName() + "\"]");
            writer.print("\t\t}");
        }

        writer.println();
        writer.println("\t}");
        writer.println("}");
        writer.flush();
        zipOutput.closeEntry();
    }

    void generateSounds() throws IOException {
        for (KciSoundType sound : itemSet.soundTypes) {
            zipOutput.putNextEntry(new ZipEntry("sounds/kci/" + sound.getName() + ".ogg"));
            zipOutput.write(sound.getOggData());
            zipOutput.closeEntry();
        }
    }
}
