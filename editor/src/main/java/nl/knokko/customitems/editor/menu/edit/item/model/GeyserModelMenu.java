package nl.knokko.customitems.editor.menu.edit.item.model;

import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.KciItem;
import nl.knokko.customitems.item.model.GeyserCustomModel;
import nl.knokko.customitems.item.model.ItemModel;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class GeyserModelMenu extends GuiMenu {

    private final KciItem item;
    private final GuiComponent returnMenu;

    public GeyserModelMenu(KciItem item, GuiComponent returnMenu) {
        this.item = item;
        this.returnMenu = returnMenu;
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextButton("Back", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.8f, 0.15f, 0.9f);

        GeyserCustomModel geyserModel = item.getGeyserModel();
        if (geyserModel == null) {
            addComponent(new DynamicTextComponent(
                    "This item doesn't have a custom Geyser model (yet)", LABEL
            ), 0.2f, 0.9f, 0.8f, 1f);
        } else {
            addComponent(new DynamicTextComponent(
                    "This item has a custom Geyser model with ID " + geyserModel.attachableId, LABEL
            ), 0.2f, 0.9f, 0.9f, 1f);
        }

        ItemModel javaModel = item.getModel();
        if (javaModel != null) {
            addComponent(new DynamicTextButton("Generate Geyser model from the custom Java model", BUTTON, HOVER, () -> {
                state.getWindow().setMainComponent(new ConvertModelMenu(returnMenu, item));
            }), 0.3f, 0.6f, 0.8f, 0.7f);
        }

        addComponent(new DynamicTextButton("Manually select Geyser model", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new SelectGeyserModel(item::setGeyserModel, returnMenu));
        }), 0.3f, 0.4f, 0.6f, 0.5f);

        addComponent(new DynamicTextButton("Remove Geyser model", BUTTON, HOVER, () -> {
            item.setGeyserModel(null);
            state.getWindow().setMainComponent(returnMenu);
        }), 0.3f, 0.2f, 0.5f, 0.3f);

        HelpButtons.addHelpLink(this, "edit menu/items/edit/model/geyser/index.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
