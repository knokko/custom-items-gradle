package nl.knokko.customitems.editor;

import nl.knokko.customitems.editor.resourcepack.ResourcepackGenerator;
import nl.knokko.customitems.item.KciItem;
import nl.knokko.customitems.item.KciSimpleItem;
import nl.knokko.customitems.item.model.GeyserCustomModel;
import nl.knokko.customitems.item.model.ItemModel;
import nl.knokko.customitems.item.model.ModernCustomItemModel;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.util.IOHelper;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ModelConverter {

    public static Progress convert(KciItem item) {
        Progress progress = new Progress();
        ItemModel model = item.getModel();
        if (!(model instanceof ModernCustomItemModel)) {
            progress.error = "The item needs a modern custom model";
            return progress;
        }

        new Thread(() -> {
            try (Socket socket = new Socket("49.12.188.159", 21102)) {
                progress.connected = true;

                ItemSet miniSet = new ItemSet(ItemSet.Side.EDITOR);
                miniSet.textures.add(item.getTexture());

                KciItem miniItem = new KciSimpleItem(true);
                miniItem.setName(item.getName());
                miniItem.setTexture(miniSet.textures.getReference(item.getTexture().getName()));
                miniItem.setModel(item.getModel());
                miniSet.items.add(miniItem);

                OutputStream output = socket.getOutputStream();
                new ResourcepackGenerator(miniSet).write(output, null, null, false);
                progress.sent = true;

                byte[] animationFile = null;
                byte[] attachableFile = null;
                byte[] modelFile = null;
                byte[] textureFile = null;

                ZipInputStream input = new ZipInputStream(socket.getInputStream());
                ZipEntry entry = input.getNextEntry();
                while (entry != null) {
                    progress.receiving = true;
                    if (!entry.isDirectory()) {
                        String path = entry.getName();
                        if (path.startsWith("animations/minecraft/customitems"))
                            animationFile = IOHelper.readAllBytes(input);
                        if (path.startsWith("attachables/minecraft/customitems"))
                            attachableFile = IOHelper.readAllBytes(input);
                        if (path.startsWith("models/blocks/minecraft/customitems"))
                            modelFile = IOHelper.readAllBytes(input);
                        if (path.equals("textures/1.png"))
                            textureFile = IOHelper.readAllBytes(input);
                    }

                    entry = input.getNextEntry();
                }

                GeyserCustomModel.AttachableParseResult attachableResult = GeyserCustomModel.parseAttachable(attachableFile);
                if (attachableResult.error != null) {
                    progress.error = "Failed to parse attachable: " + attachableResult.error;
                    return;
                }

                progress.result = new GeyserCustomModel(
                        attachableResult.id, animationFile,
                        attachableResult.newJsonBytes, modelFile, textureFile
                );
            } catch (IOException io) {
                progress.error = "IO: " + io.getLocalizedMessage();
            } catch (ValidationException | ProgrammingValidationException invalid) {
                progress.error = "Unexpected validation error: " + invalid.getMessage();
            }
        }).start();

        return progress;
    }

    public static class Progress {

        public volatile String error;
        public volatile boolean connected;
        public volatile boolean sent;
        public volatile boolean receiving;
        public volatile GeyserCustomModel result;
    }
}
