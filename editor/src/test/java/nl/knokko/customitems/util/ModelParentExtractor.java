package nl.knokko.customitems.util;

import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.item.CIMaterial;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ModelParentExtractor {

    public static void main(String[] args) throws IOException {
        extractModels("1.18.2");
    }

    private static String findJsonProperty(String candidateLine, String property) {
        int rawStartIndex = candidateLine.indexOf("\"" + property + "\": \"");
        if (rawStartIndex == -1) return null;

        int startIndex = rawStartIndex + 5 + property.length();
        int endIndex = candidateLine.indexOf('"', startIndex);
        return candidateLine.substring(startIndex, endIndex);
    }

    private static void extractModels(String mcVersion) throws IOException {
        // Note for other developers: change this to wherever your MC installation is
        File targetFile = new File("C:\\Users\\20182191\\AppData\\Roaming\\.minecraft\\versions\\" + mcVersion + "\\" + mcVersion + ".jar");

        Map<String, String> modelParentMap = new HashMap<>();
        Map<String, String> modelTextureMap = new HashMap<>();

        InputStream rawInputStream = Files.newInputStream(targetFile.toPath());
        ZipInputStream zipInput = new ZipInputStream(rawInputStream);

        while (true) {
            ZipEntry currentEntry = zipInput.getNextEntry();
            if (currentEntry == null) break;

            String prefix = "assets/minecraft/models/item/";
            if (currentEntry.getName().startsWith(prefix) && !currentEntry.isDirectory()) {
                String itemName = currentEntry.getName().substring(prefix.length(), currentEntry.getName().length() - 5);

                Scanner modelScanner = new Scanner(zipInput);

                String parent = null;
                String texture = null;

                while (modelScanner.hasNextLine()) {
                    String currentLine = modelScanner.nextLine();
                    String potentialParent = findJsonProperty(currentLine, "parent");
                    String potentialTexture = findJsonProperty(currentLine, "layer0");
                    if (potentialParent != null) parent = potentialParent;
                    if (potentialTexture != null) texture = '"' + potentialTexture + '"';
                }

                String enumItemName = itemName.toUpperCase(Locale.ROOT);
                modelParentMap.put(enumItemName, parent);
                modelTextureMap.put(enumItemName, texture);
            }
        }
        rawInputStream.close();

        for (String itemName : modelParentMap.keySet()) {
            try {
                if (CIMaterial.valueOf(itemName).lastVersion >= MCVersions.VERSION1_14) {
                    System.out.println("    " + itemName + "(\"" + modelParentMap.get(itemName) + "\", " + modelTextureMap.get(itemName) + "),");
                }
            } catch (IllegalArgumentException noSuchMaterial) {
                // Don't generate an enum constant when there is no corresponding material
            }
        }
    }
}
