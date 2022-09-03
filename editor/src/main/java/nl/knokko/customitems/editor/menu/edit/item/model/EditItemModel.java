package nl.knokko.customitems.editor.menu.edit.item.model;

import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.model.DefaultItemModel;
import nl.knokko.customitems.item.model.DefaultModelType;
import nl.knokko.customitems.item.model.ItemModel;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditItemModel extends GuiMenu {

    private final ItemModel oldModel;
    private final Consumer<ItemModel> changeModel;
    private final String itemName;
    private final String textureName;
    private final DefaultModelType defaultModelType;
    private final boolean isLeatherArmor;
    private final GuiComponent returnMenu;

    public EditItemModel(
            ItemModel oldModel, Consumer<ItemModel> changeModel,
            String itemName, String textureName, DefaultModelType defaultModelType, boolean isLeatherArmor, GuiComponent returnMenu
    ) {
        this.oldModel = oldModel;
        this.changeModel = changeModel;
        this.itemName = itemName;
        this.textureName = textureName;
        this.defaultModelType = defaultModelType;
        this.isLeatherArmor = isLeatherArmor;
        this.returnMenu = returnMenu;
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.8f, 0.175f, 0.9f);

        String modelTypeString;
        if (oldModel == null) modelTypeString = "None";
        else if (oldModel instanceof DefaultItemModel) modelTypeString = "Default";
        else modelTypeString = "Custom";

        addComponent(new DynamicTextComponent("Current model: " + modelTypeString, LABEL), 0.1f, 0.7f, 0.3f, 0.8f);
        if (oldModel != null) {
            String[] currentModelLines = readModel(oldModel);
            float maxLineLength = 0.58f;
            for (int index = 0; index < currentModelLines.length; index++) {
                float minX = 0.11f;
                float minY = 0.65f - 0.05f * index;
                float maxY = minY + 0.05f;
                String line = currentModelLines[index];
                addComponent(new DynamicTextComponent(line, LABEL), minX, minY, minX + Math.min(0.01f * line.length(), maxLineLength), maxY);
            }
        }

        addComponent(new DynamicTextComponent("Change to:", LABEL), 0.6f, 0.7f, 0.8f, 0.8f);
        if (defaultModelType != null) {
            addComponent(new DynamicTextButton("Default model", BUTTON, HOVER, () -> {
                state.getWindow().setMainComponent(new ChooseDefaultModel(defaultModelType, changeModel, returnMenu));
            }), 0.65f, 0.55f, 0.85f, 0.65f);
        }
        addComponent(new DynamicTextButton("Custom model", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new ChooseCustomModel(changeModel::accept, returnMenu));
        }), 0.65f, 0.4f, 0.85f, 0.5f);

        HelpButtons.addHelpLink(this, "edit menu/items/edit/model/index.html");
    }

    private String[] readModel(ItemModel model) {
        try {
            ByteArrayOutputStream rememberOutput = new ByteArrayOutputStream();
            ZipOutputStream zipOutput = new ZipOutputStream(rememberOutput);
            zipOutput.putNextEntry(new ZipEntry("result.json"));
            model.write(zipOutput, itemName, textureName, defaultModelType, isLeatherArmor);
            zipOutput.closeEntry();
            zipOutput.close();

            ZipInputStream viewZip = new ZipInputStream(new ByteArrayInputStream(rememberOutput.toByteArray()));
            ZipEntry viewEntry = viewZip.getNextEntry();
            while (!viewEntry.getName().equals("result.json")) {
                viewEntry = viewZip.getNextEntry();
            }

            List<String> lineList = new ArrayList<>();
            Scanner jsonScanner = new Scanner(viewZip);
            while (jsonScanner.hasNextLine()) {
                lineList.add(jsonScanner.nextLine().replaceAll("\t", "  "));
            }
            jsonScanner.close();
            viewZip.close();

            return lineList.toArray(new String[0]);
        } catch (IOException ioTrouble) {
            return new String[] { "For some reason, this model can't be read" };
        }
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
