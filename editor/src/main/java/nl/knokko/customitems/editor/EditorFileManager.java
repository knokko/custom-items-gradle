package nl.knokko.customitems.editor;

import nl.knokko.customitems.editor.resourcepack.ResourcepackGenerator;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.StringEncoder;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.ByteArrayBitOutput;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class EditorFileManager {

    public static final File FOLDER = new File(FileSystemView.getFileSystemView().getDefaultDirectory() + "/Custom Item Sets");
    private static final File LOGS_FOLDER = new File(FOLDER + "/logs");
    public static final File BACKUPS_FOLDER = new File(FOLDER + "/backups");

    static void startLogging() {
        FOLDER.mkdirs();
        try {
            LOGS_FOLDER.mkdir();
            long time = System.currentTimeMillis();
            System.setOut(new Logger(new File(LOGS_FOLDER + "/out " + time + ".txt"), System.out));
            System.setErr(new Logger(new File(LOGS_FOLDER + "/err " + time + ".txt"), System.err));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        System.out.println("test out");
        System.err.println("test error");
    }

    public static void export(
            SItemSet itemSet, int mcVersion, String fileName
    ) throws IOException, ValidationException, ProgrammingValidationException {

        FOLDER.mkdirs();

        ByteArrayBitOutput output = new ByteArrayBitOutput();
        itemSet.save(output, SItemSet.Side.PLUGIN);
        output.terminate();

        byte[] bytes = output.getBytes();

        // Write the .cis file, which stands for Custom Item Set
        File file = new File(FOLDER + "/" + fileName + ".cis");
        OutputStream fileOutput = Files.newOutputStream(file.toPath());
        fileOutput.write(bytes);
        fileOutput.flush();
        fileOutput.close();

        /*
         * Write the .txt file, which can be used as alternative for the .cis file.
         * It has a bigger file size and will be a bit slower to read, but it is useful
         * for servers hosts like Aternos that do not allow users to upload (binary files).
         *
         * This file is basically hexadecimal, except that it uses only characters from the alphabet rather than both
         * alphabet characters and digits. (When I wrote this, I didn't think about hexadecimal...)
         */
        byte[] textBytes = StringEncoder.encodeTextyBytes(bytes, true);
        File textFile = new File(FOLDER + "/" + fileName + ".txt");
        fileOutput = Files.newOutputStream(textFile.toPath());
        fileOutput.write(textBytes);
        fileOutput.flush();
        fileOutput.close();

        // Generate the resourcepack...
        try (OutputStream outputStream = Files.newOutputStream(new File(FOLDER + "/" + fileName + ".zip").toPath())) {
            new ResourcepackGenerator(itemSet, mcVersion).write(outputStream);
        }
    }

    public static void saveAndBackUp(SItemSet itemSet, String fileName) throws IOException {
        FOLDER.mkdirs();
        BACKUPS_FOLDER.mkdirs();

        // cisb stands for Custom Item Set Builder
        File file = new File(FOLDER + "/" + fileName + ".cisb");

        ByteArrayBitOutput output = new ByteArrayBitOutput();
        itemSet.save(output, SItemSet.Side.EDITOR);
        output.terminate();
        byte[] bytes = output.getBytes();

        OutputStream mainOutput = Files.newOutputStream(file.toPath());
        mainOutput.write(bytes);
        mainOutput.flush();
        mainOutput.close();

        OutputStream backupOutput = Files.newOutputStream(
                new File(BACKUPS_FOLDER + "/" + fileName + " " + System.currentTimeMillis() + ".cisb").toPath());
        backupOutput.write(bytes);
        mainOutput.flush();
        backupOutput.close();
    }
}
