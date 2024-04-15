package nl.knokko.customitems.editor.wiki;

import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.sound.CustomSoundTypeValues;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

class WikiSoundGenerator {

    private final ItemSet itemSet;

    WikiSoundGenerator(ItemSet itemSet) {
        this.itemSet = itemSet;
    }

    void generate(File soundsFolder) throws IOException {
        for (CustomSoundTypeValues sound : itemSet.soundTypes) {
            Files.write(new File(soundsFolder + "/" + sound.getName() + ".ogg").toPath(), sound.getOggData());
        }
    }
}
