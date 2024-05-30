package nl.knokko.customitems.resourcepack;

import java.io.*;
import java.util.Arrays;
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

        byte[] rawContent = new byte[1000];
        ZipEntry currentEntry = zip.getNextEntry();
        while (currentEntry != null) {
            if (!currentEntry.isDirectory()) {
                int numReadBytes = 0;
                while (true) {
                    int nextReadBytes = zip.read(rawContent, numReadBytes, rawContent.length - numReadBytes);
                    if (nextReadBytes == -1) break;

                    numReadBytes += nextReadBytes;
                    if (numReadBytes == rawContent.length) rawContent = Arrays.copyOf(rawContent, 2 * numReadBytes);
                }

                entries.put(currentEntry.getName(), Arrays.copyOf(rawContent, numReadBytes));
            }
            currentEntry = zip.getNextEntry();
        }

        zip.close();
        return entries;
    }
}
