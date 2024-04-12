package nl.knokko.customitems.editor.resourcepack.geyser;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

class IOHelper {

    static void propagate(
            String resourceName,
            ZipOutputStream zipOutput, String entryName,
            Function<String, String> processLine
    ) throws IOException {
        Scanner scanner = new Scanner(IOHelper.class.getClassLoader().getResourceAsStream(
                "nl/knokko/customitems/editor/geyser/" + resourceName
        ));

        zipOutput.putNextEntry(new ZipEntry(entryName));
        PrintWriter writer = new PrintWriter(zipOutput);

        while (scanner.hasNextLine()) {
            if (processLine == null) writer.println(scanner.nextLine());
            else writer.println(processLine.apply(scanner.nextLine()));
        }

        writer.flush();
        scanner.close();
        zipOutput.closeEntry();
    }
}
