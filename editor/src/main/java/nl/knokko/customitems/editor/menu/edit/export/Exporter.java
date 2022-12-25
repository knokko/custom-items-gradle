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

public class Exporter {

    public static String attemptToExport(
            ItemSet itemSet, String fileName, ExportSettingsValues exportSettings, GuiComponent returnMenu
    ) {
        String[] pResourcePackHash = { null };

        String error = Validation.toErrorString(() -> {
            try {
                itemSet.setExportSettings(exportSettings);
                itemSet.validateExportVersion(exportSettings.getMcVersion());

                if (exportSettings.getMode() == ExportSettingsValues.Mode.AUTOMATIC) {
                    pResourcePackHash[0] = uploadResourcePackToMyHost(itemSet);
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

        if (error != null) return error;

        try {
            EditorFileManager.saveAndBackUp(itemSet, fileName);
        } catch (IOException saveFailed) {
            return "Saving failed: " + saveFailed.getLocalizedMessage();
        }

        if (exportSettings.getMode() == ExportSettingsValues.Mode.AUTOMATIC) {
            returnMenu.getState().getWindow().setMainComponent(new AfterExportMenuAutomatic(returnMenu, pResourcePackHash[0]));
        } else if (exportSettings.getMode() == ExportSettingsValues.Mode.MIXED) {
            returnMenu.getState().getWindow().setMainComponent(new AfterExportMenuMixed(returnMenu));
        } else if (exportSettings.getMode() == ExportSettingsValues.Mode.MANUAL) {
            returnMenu.getState().getWindow().setMainComponent(new AfterExportMenuManual(returnMenu));
        } else {
            return "Unknown export mode";
        }

        return null;
    }

    private static String uploadResourcePackToMyHost(
            ItemSet itemSet
    ) throws IOException, ValidationException, ProgrammingValidationException {

        // Ensure that the right item damages are used when writing the .cis file
        itemSet.assignInternalItemDamages();

        ByteArrayBitOutput cisOutput = new ByteArrayBitOutput();
        itemSet.save(cisOutput, ItemSet.Side.PLUGIN);
        cisOutput.terminate();

        byte[] cisBytes = cisOutput.getBytes();
        byte[] textyBytes = StringEncoder.encodeTextyBytes(cisBytes, true);

        return ResourcePackHost.upload(
                uploadOutput -> new ResourcepackGenerator(itemSet).write(uploadOutput, textyBytes, false),
                (responseCode, responseMessage, errorResponse) -> {
                    System.err.println("Uploading resource pack failed:");
                    for (String line : errorResponse) {
                        System.err.println(line);
                    }
                    throw new IOException("Upload failed: HTTP " + responseCode + " (" + responseMessage + ")");
                }
        );
    }
}
