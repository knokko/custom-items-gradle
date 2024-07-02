package nl.knokko.customitems.editor;

import nl.knokko.customitems.editor.menu.edit.export.ExportProgress;
import nl.knokko.customitems.editor.resourcepack.ResourcepackGenerator;
import nl.knokko.customitems.editor.resourcepack.geyser.GeyserMappingsGenerator;
import nl.knokko.customitems.editor.resourcepack.geyser.GeyserPackGenerator;
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

import static nl.knokko.customitems.editor.menu.edit.export.ExportProgress.*;

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
            ItemSet itemSet, ExportProgress progress
    ) throws IOException, ValidationException, ProgrammingValidationException {

        FOLDER.mkdirs();

        if (!itemSet.getExportSettings().shouldSkipResourcepack()) {
            progress.status = STATUS_GENERATING_RESOURCEPACK;

            // Generate the resourcepack...
            // NOTE: This must happen BEFORE writing the .cis file since this also assigns internal item damages
            try (OutputStream outputStream = Files.newOutputStream(new File(FOLDER + "/resource-pack.zip").toPath())) {
                new ResourcepackGenerator(itemSet).write(outputStream, null, null, true);
            }
        }

        if (itemSet.getExportSettings().shouldGenerateGeyserPack()) {
            progress.status = STATUS_GENERATING_GEYSERPACK;
            try (OutputStream outputStream = Files.newOutputStream(new File(FOLDER + "/geyser.mcpack").toPath())) {
                new GeyserPackGenerator(itemSet, outputStream, true).write();
            }
            try (OutputStream outputStream = Files.newOutputStream(new File(FOLDER + "/geyser_mappings.json").toPath())) {
                new GeyserMappingsGenerator(itemSet, outputStream).writeMappings();
            }
        }

        progress.status = STATUS_SAVING_AFTER_GENERATION;

        ByteArrayBitOutput output = new ByteArrayBitOutput();
        itemSet.save(output, ItemSet.Side.PLUGIN);
        output.terminate();

        /*
         * The file is eventually stored as text file because some hosts like Aternos don't allow users to upload
         * binary files. Besides, the storage required for storing .cis files was never big anyway, so I no longer
         * bother to generate both a binary and a text file.
         *
         * This file is basically hexadecimal, except that it uses only characters from the alphabet rather than both
         * alphabet characters and digits. (When I wrote this, I didn't think about hexadecimal...)
         */
        byte[] textBytes = StringEncoder.encodeTextyBytes(output.getBytes(), true);
        File textFile = new File(FOLDER + "/items.cis.txt");
        OutputStream fileOutput = Files.newOutputStream(textFile.toPath());
        fileOutput.write(textBytes);
        fileOutput.flush();
        fileOutput.close();
    }

    public static void backUp(ItemSet itemSet, String fileName) {
        try {
            backUpAndMaybeSave(itemSet, fileName, false);
        } catch (IOException uhOoh) {
            // Since back-ups are done silently, there is no good place to log their failures
            //noinspection CallToPrintStackTrace
            uhOoh.printStackTrace();
        }
    }

    public static void saveAndBackUp(ItemSet itemSet, String fileName) throws IOException {
        backUpAndMaybeSave(itemSet, fileName, true);
    }

    private static void backUpAndMaybeSave(ItemSet itemSet, String fileName, boolean save) throws IOException {
        if (save) FOLDER.mkdirs();
        BACKUPS_FOLDER.mkdirs();

        ByteArrayBitOutput output = new ByteArrayBitOutput();
        itemSet.save(output, ItemSet.Side.EDITOR);
        output.terminate();
        byte[] bytes = output.getBytes();

        if (save) {
            // cisb stands for Custom Item Set Builder
            File file = new File(FOLDER + "/" + fileName + ".cisb");

            OutputStream mainOutput = Files.newOutputStream(file.toPath());
            mainOutput.write(bytes);
            mainOutput.flush();
            mainOutput.close();
        }

        OutputStream backupOutput = Files.newOutputStream(
                new File(BACKUPS_FOLDER + "/" + fileName + " " + System.currentTimeMillis() + ".cisb").toPath());
        backupOutput.write(bytes);
        backupOutput.flush();
        backupOutput.close();

        cleanOldBackUps();
    }

    private static void cleanOldBackUps() {
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
