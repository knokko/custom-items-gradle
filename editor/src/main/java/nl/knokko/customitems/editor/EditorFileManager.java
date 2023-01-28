package nl.knokko.customitems.editor;

import nl.knokko.customitems.editor.resourcepack.ResourcepackGenerator;
import nl.knokko.customitems.editor.util.ItemSetBackups;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.StringEncoder;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.ByteArrayBitOutput;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;

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

    public static void exportFiles(
            ItemSet itemSet
    ) throws IOException, ValidationException, ProgrammingValidationException {

        FOLDER.mkdirs();

        // Generate the resourcepack...
        // NOTE: This must happen BEFORE writing the .cis file since this also assigns internal item damages
        try (OutputStream outputStream = Files.newOutputStream(new File(FOLDER + "/resource-pack.zip").toPath())) {
            new ResourcepackGenerator(itemSet).write(outputStream, null, true);
        }

        ByteArrayBitOutput output = new ByteArrayBitOutput();
        itemSet.save(output, ItemSet.Side.PLUGIN);
        output.terminate();

        byte[] bytes = output.getBytes();

        /*
         * The file is eventually stored as text file because some hosts like Aternos don't allow users to upload
         * binary files. Besides, the storage required for storing .cis files was never big anyway, so I no longer
         * bother to generate both a binary and a text file.
         *
         * This file is basically hexadecimal, except that it uses only characters from the alphabet rather than both
         * alphabet characters and digits. (When I wrote this, I didn't think about hexadecimal...)
         */
        byte[] textBytes = StringEncoder.encodeTextyBytes(bytes, true);
        File textFile = new File(FOLDER + "/items.cis.txt");
        OutputStream fileOutput = Files.newOutputStream(textFile.toPath());
        fileOutput.write(textBytes);
        fileOutput.flush();
        fileOutput.close();
    }

    public static void saveAndBackUp(ItemSet itemSet, String fileName) throws IOException {
        FOLDER.mkdirs();
        BACKUPS_FOLDER.mkdirs();

        // cisb stands for Custom Item Set Builder
        File file = new File(FOLDER + "/" + fileName + ".cisb");

        ByteArrayBitOutput output = new ByteArrayBitOutput();
        itemSet.save(output, ItemSet.Side.EDITOR);
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

        Collection<ItemSetBackups> newBackups = getAllBackups();
        for (ItemSetBackups backup : newBackups) {
            Collection<Long> backupsToRemove = backup.cleanOldBackups(System.currentTimeMillis());
            for (Long saveTime : backupsToRemove) {
                try {
                    Files.delete(getBackupFile(backup.name, saveTime).toPath());
                } catch (IOException failedToDelete) {
                    System.err.println("Failed to delete back-up of " + backup.name + " at " + saveTime
                            + ": " + failedToDelete.getLocalizedMessage());
                }
            }
        }
    }

    public static Collection<ItemSetBackups> getAllBackups() {
        String[] backupFileNames = BACKUPS_FOLDER.list();
        if (backupFileNames != null) {
            return ItemSetBackups.getAll(backupFileNames);
        } else return new ArrayList<>(0);
    }

    public static File getBackupFile(String itemSetName, long saveTime) {
        return new File(BACKUPS_FOLDER + "/" + itemSetName + " " + saveTime + ".cisb");
    }
}
