package nl.knokko.customitems.resourcepack;

import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import nl.knokko.customitems.bithelper.BitInputStream;
import nl.knokko.customitems.editor.resourcepack.ResourcepackGenerator;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.settings.ExportSettingsValues;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static nl.knokko.customitems.MCVersions.*;
import static org.junit.jupiter.api.Assertions.*;

public class TestResourcepackGenerator {

    @Test
    public void testBasicResourcepackValidity() {
        assertValidResourcepack("item-sets/backward12old.cisb", VERSION1_12);

        int[] newVersions = { VERSION1_14, VERSION1_15, VERSION1_16, VERSION1_17, VERSION1_18, VERSION1_19, VERSION1_20 };
        for (int version : newVersions) assertValidResourcepack("item-sets/backward12new.cisb", version);
    }

    private void assertValidResourcepack(String path, int mcVersion) {
        try {
            InputStream setInput = TestResourcepackGenerator.class.getClassLoader().getResourceAsStream(path);
            ItemSet itemSet = new ItemSet(new BitInputStream(setInput), ItemSet.Side.EDITOR, false);
            setInput.close();

            ExportSettingsValues exportSettings = itemSet.getExportSettings().copy(true);
            exportSettings.setMcVersion(mcVersion);
            exportSettings.setMode(ExportSettingsValues.Mode.MANUAL);
            itemSet.setExportSettings(exportSettings);

            ByteArrayOutputStream rememberOutput = new ByteArrayOutputStream();
            new ResourcepackGenerator(itemSet).write(rememberOutput, new byte[0], true);

            int jsonCounter = 0;
            int pngCounter = 0;
            int packCounter = 0;
            ZipInputStream resourceInput = new ZipInputStream(new ByteArrayInputStream(rememberOutput.toByteArray()));

            ZipEntry entry = resourceInput.getNextEntry();
            while (entry != null) {
                if (entry.getName().contains("pack.mcmeta")) {
                    packCounter += 1;
                    assertTrue(Jsoner.deserialize(new InputStreamReader(resourceInput)) instanceof JsonObject);
                } else if (entry.getName().endsWith(".png")) {
                    pngCounter += 1;
                    assertNotNull(ImageIO.read(resourceInput));
                } else if (entry.getName().endsWith(".json")) {
                    jsonCounter += 1;
                    assertTrue(Jsoner.deserialize(new InputStreamReader(resourceInput)) instanceof JsonObject);
                }
                entry = resourceInput.getNextEntry();
            }

            resourceInput.close();

            assertEquals(1, packCounter);
            assertTrue(jsonCounter > 2);
            assertTrue(pngCounter > 2);
        } catch (Exception exception) {
            fail(exception);
        }
    }
}
