package nl.knokko.customitems.resourcepack;

import nl.knokko.customitems.util.IOHelper;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipHelper {

    static byte[] createSingleFileZip(String path, String content) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(bytes);
        zip.putNextEntry(new ZipEntry(path));
        PrintWriter writer = new PrintWriter(zip);
        writer.println(content);
        writer.flush();
        zip.closeEntry();
        zip.close();

        return bytes.toByteArray();
    }

    static Map<String, byte[]> entries(byte[] rawZip) throws IOException {
        Map<String, byte[]> entries = new HashMap<>();
        ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(rawZip));

        ZipEntry currentEntry = zip.getNextEntry();
        while (currentEntry != null) {
            if (!currentEntry.isDirectory()) {
                entries.put(currentEntry.getName(), IOHelper.readAllBytes(zip));
            }
            currentEntry = zip.getNextEntry();
        }

        zip.close();
        return entries;
    }
}
