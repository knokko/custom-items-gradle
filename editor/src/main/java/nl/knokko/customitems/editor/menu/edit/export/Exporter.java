package nl.knokko.customitems.editor.menu.edit.export;

import nl.knokko.customitems.bithelper.ByteArrayBitOutput;
import nl.knokko.customitems.editor.EditorFileManager;
import nl.knokko.customitems.editor.resourcepack.ResourcepackGenerator;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.settings.ExportSettingsValues;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ResourcePackHost;
import nl.knokko.customitems.util.StringEncoder;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.gui.component.GuiComponent;

import java.io.IOException;

import static nl.knokko.customitems.editor.menu.edit.export.ExportProgress.*;

public class Exporter {

    public static void attemptToExport(
            ItemSet itemSet, String fileName, ExportSettingsValues exportSettings,
            GuiComponent returnMenu, ExportProgress progress
    ) {
        String[] pResourcePackHash = { null };

        String error = Validation.toErrorString(() -> {
            try {
                itemSet.setExportSettings(exportSettings);
                itemSet.validateExportVersion(exportSettings.getMcVersion());

                progress.status = STATUS_GENERATING_RESOURCEPACK;
                if (exportSettings.getMode() == ExportSettingsValues.Mode.AUTOMATIC) {
                    pResourcePackHash[0] = uploadResourcePackToMyHost(itemSet, progress);
                } else if (exportSettings.getMode() == ExportSettingsValues.Mode.MIXED) {
                    EditorFileManager.exportFiles(itemSet);
                } else if (exportSettings.getMode() == ExportSettingsValues.Mode.MANUAL) {
                    EditorFileManager.exportFiles(itemSet);
                } else {
                    throw new ValidationException("Unknown export mode: " + exportSettings.getMode());
                }
            } catch (IOException ioFailure) {
                throw new ValidationException("Exporting failed: " + ioFailure.getLocalizedMessage());
            }
        });

        if (error != null) {
            progress.error = error;
            return;
        }

        progress.status = STATUS_SAVING_ITEM_SET;

        try {
            EditorFileManager.saveAndBackUp(itemSet, fileName);
        } catch (IOException saveFailed) {
            progress.error = "Saving failed: " + saveFailed.getLocalizedMessage();
            return;
        }

        if (exportSettings.getMode() == ExportSettingsValues.Mode.AUTOMATIC) {
            progress.nextMenu = new AfterExportMenuAutomatic(
                    returnMenu, pResourcePackHash[0], exportSettings.getHostAddress()
            );
        } else if (exportSettings.getMode() == ExportSettingsValues.Mode.MIXED) {
            progress.nextMenu = new AfterExportMenuMixed(returnMenu);
        } else if (exportSettings.getMode() == ExportSettingsValues.Mode.MANUAL) {
            progress.nextMenu = new AfterExportMenuManual(returnMenu);
        } else {
            progress.error = "Unknown export mode";
        }
    }

    private static String uploadResourcePackToMyHost(
            ItemSet itemSet, ExportProgress progress
    ) throws IOException, ValidationException, ProgrammingValidationException {

        // Ensure that the right item damages are used when writing the .cis file
        itemSet.assignInternalItemDamages();

        ByteArrayBitOutput cisOutput = new ByteArrayBitOutput();
        itemSet.save(cisOutput, ItemSet.Side.PLUGIN);
        cisOutput.terminate();

        byte[] cisBytes = cisOutput.getBytes();
        byte[] textyBytes = StringEncoder.encodeTextyBytes(cisBytes, true);

        return ResourcePackHost.upload(
                itemSet.getExportSettings().getHostAddress(),
                uploadOutput -> {
                    new ResourcepackGenerator(itemSet).write(uploadOutput, textyBytes, false);
                    progress.status = STATUS_UPLOADING_RESOURCEPACK;
                }, (responseCode, responseMessage, errorResponse) -> {
                    System.err.println("Uploading resource pack failed:");
                    for (String line : errorResponse) {
                        System.err.println(line);
                    }
                    throw new IOException("Upload failed: HTTP " + responseCode + " (" + responseMessage + ")");
                }
        );
    }
}
