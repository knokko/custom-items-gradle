package nl.knokko.customitems.editor.util;

import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.FileChooserMenu;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Platform;

import java.io.File;
import java.util.Locale;
import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;
import static org.lwjgl.system.MemoryUtil.memUTF8;
import static org.lwjgl.util.nfd.NativeFileDialog.*;

public class FileDialog {

    public static void open(
            String extension, Consumer<String> setError, GuiComponent returnMenu, Consumer<File> useFile
    ) {

        // MacOS is... special... see https://github.com/knokko/custom-items-gradle/issues/219
        if (Platform.get() == Platform.MACOSX) {
            returnMenu.getState().getWindow().setMainComponent(new FileChooserMenu(
                    returnMenu, useFile,
                    file -> file.getName().toLowerCase(Locale.ROOT).endsWith(extension.toLowerCase(Locale.ROOT)),
                    CANCEL_BASE, CANCEL_HOVER, CHOOSE_BASE, CHOOSE_HOVER, BACKGROUND, BACKGROUND2
            ));
        } else {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                returnMenu.getState().getWindow().setMainComponent(returnMenu);
                PointerBuffer pPath = stack.callocPointer(1);
                int result = NFD_OpenDialog(stack.UTF8(extension), null, pPath);
                if (result == NFD_OKAY) {
                    String path = memUTF8(pPath.get(0));
                    File chosenFile = new File(path);
                    nNFD_Free(pPath.get(0));
                    useFile.accept(chosenFile);
                } else if (result == NFD_ERROR) {
                    setError.accept("NFD_OpenDialog returned NFD_ERROR");
                }
            }
        }
    }
}
