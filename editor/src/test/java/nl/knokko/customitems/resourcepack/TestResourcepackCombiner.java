package nl.knokko.customitems.resourcepack;

import nl.knokko.customitems.editor.resourcepack.ResourcepackGenerator;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.misc.CombinedResourcepack;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

import static nl.knokko.customitems.resourcepack.ZipHelper.createSingleFileZip;
import static nl.knokko.customitems.resourcepack.ZipHelper.entries;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TestResourcepackCombiner {

    @Test
    public void testMixed() throws ValidationException, ProgrammingValidationException, IOException {
        CombinedResourcepack[] javaHello = new CombinedResourcepack[3];
        CombinedResourcepack[] javaMeta = new CombinedResourcepack[3];
        for (int prio = 1; prio <= 3; prio++) {
            CombinedResourcepack hello = new CombinedResourcepack(true);
            hello.setName("java hello " + prio);
            hello.setPriority(-prio);
            hello.setContent(createSingleFileZip("test/hello.txt", "Hello world " + prio));
            javaHello[prio - 1] = hello;

            CombinedResourcepack meta = new CombinedResourcepack(true);
            meta.setName("java meta " + prio);
            meta.setPriority(prio);
            meta.setContent(createSingleFileZip("pack.mcmeta", "Fake pack " + prio));
            javaMeta[prio - 1] = meta;
        }

        CombinedResourcepack[] geyserHello = new CombinedResourcepack[3];
        CombinedResourcepack[] geyserMeta = new CombinedResourcepack[3];
        for (int prio = 1; prio <= 3; prio++) {
            CombinedResourcepack hello = new CombinedResourcepack(true);
            hello.setName("geyser hello " + prio);
            hello.setPriority(-prio);
            hello.setGeyser(true);
            hello.setContent(createSingleFileZip("test/hello.txt", "Hello world " + prio));
            geyserHello[prio - 1] = hello;

            CombinedResourcepack meta = new CombinedResourcepack(true);
            meta.setName("geyser meta " + prio);
            meta.setPriority(prio);
            meta.setGeyser(true);
            meta.setContent(createSingleFileZip("manifest.json", "Fake pack " + prio));
            geyserMeta[prio - 1] = meta;
        }

        ItemSet itemSet = new ItemSet(ItemSet.Side.EDITOR);
        itemSet.combinedResourcepacks.add(javaHello[1]);
        itemSet.combinedResourcepacks.add(javaHello[0]);
        itemSet.combinedResourcepacks.add(javaHello[2]);
        itemSet.combinedResourcepacks.add(javaMeta[1]);
        itemSet.combinedResourcepacks.add(javaMeta[2]);
        itemSet.combinedResourcepacks.add(javaMeta[0]);

        itemSet.combinedResourcepacks.add(geyserHello[1]);
        itemSet.combinedResourcepacks.add(geyserHello[0]);
        itemSet.combinedResourcepacks.add(geyserHello[2]);
        itemSet.combinedResourcepacks.add(geyserMeta[1]);
        itemSet.combinedResourcepacks.add(geyserMeta[2]);
        itemSet.combinedResourcepacks.add(geyserMeta[0]);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        new ResourcepackGenerator(itemSet).write(output, null, () -> {}, true);

        Map<String, byte[]> javaEntries = entries(output.toByteArray());
        assertEquals("Hello world 1", parseLine(javaEntries.get("test/hello.txt")));
        assertEquals("Fake pack 3", parseLine(javaEntries.get("pack.mcmeta")));
        assertFalse(javaEntries.containsKey("manifest.json"));

        Map<String, byte[]> geyserEntries = entries(javaEntries.get("geyser.mcpack"));
        assertEquals("Hello world 1", parseLine(geyserEntries.get("test/hello.txt")));
        assertEquals("Fake pack 3", parseLine(geyserEntries.get("manifest.json")));
        assertFalse(geyserEntries.containsKey("pack.mcmeta"));
    }

    static String parseLine(byte[] bytes) {
        Scanner scanner = new Scanner(new ByteArrayInputStream(bytes));
        String result = scanner.nextLine();
        scanner.close();
        return result;
    }
}
