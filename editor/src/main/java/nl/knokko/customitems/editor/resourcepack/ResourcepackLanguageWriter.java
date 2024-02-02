package nl.knokko.customitems.editor.resourcepack;

import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.item.TranslationEntry;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.sound.CustomSoundTypeValues;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ResourcepackLanguageWriter {
    private final ItemSet itemSet;
    private final ZipOutputStream zipOutput;

    ResourcepackLanguageWriter(ItemSet itemSet, ZipOutputStream zipOutput) {
        this.itemSet = itemSet;
        this.zipOutput = zipOutput;
    }

    void writeLanguageFiles() throws IOException {
        Set<String> languages = new HashSet<>();
        for (CustomItemValues item : itemSet.getItems()) {
            for (TranslationEntry translation : item.getTranslations()) {
                languages.add(translation.getLanguage());
            }
        }

        for (String language : languages) {
            ZipEntry entry = new ZipEntry("assets/minecraft/lang/" + language + ".json");
            zipOutput.putNextEntry(entry);
            PrintWriter jsonWriter = new PrintWriter(zipOutput);
            jsonWriter.println("{");

            boolean isFirst = true;
            for (CustomItemValues item : itemSet.getItems()) {
                for (TranslationEntry translation : item.getTranslations()) {
                    if (translation.getLanguage().equals(language)) {
                        if (!isFirst) jsonWriter.println(",");
                        isFirst = false;
                        jsonWriter.print("\t\"kci." + item.getName() + ".name\":\" " + translation.getDisplayName() + "\"");
                        for (int index = 0; index < translation.getLore().size(); index++) {
                            jsonWriter.println(",");
                            jsonWriter.print("\t\"kci." + item.getName() + ".lore." + index + "\": \"" + translation.getLore().get(index) + "\"");
                        }
                    }
                }
            }
            jsonWriter.println();
            jsonWriter.println("}");
            jsonWriter.flush();
            zipOutput.flush();
            zipOutput.closeEntry();
        }
    }
}
