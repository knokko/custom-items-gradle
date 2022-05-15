package nl.knokko.customitems.editor.menu.edit.item.model;

import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.model.DefaultItemModel;
import nl.knokko.customitems.item.model.DefaultModelType;
import nl.knokko.customitems.item.model.ItemModel;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class ChooseDefaultModel extends GuiMenu {

    private final DefaultModelType type;
    private final Consumer<ItemModel> changeModel;
    private final GuiComponent returnMenu;

    public ChooseDefaultModel(DefaultModelType type, Consumer<ItemModel> changeModel, GuiComponent returnMenu) {
        this.type = type;
        this.changeModel = changeModel;
        this.returnMenu = returnMenu;
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.8f, 0.175f, 0.9f);

        addComponent(new DynamicTextComponent("Parent:", LABEL), 0.3f, 0.7f, 0.4f, 0.8f);
        TextEditField parentField = new TextEditField(type.recommendedParents.get(0), EDIT_BASE, EDIT_ACTIVE);
        addComponent(parentField, 0.425f, 0.7f, 0.6f, 0.8f);

        addComponent(new DynamicTextComponent("Recommended parents are:", LABEL), 0.3f, 0.5f, 0.6f, 0.6f);
        for (int index = 0; index < type.recommendedParents.size(); index++) {
            addComponent(
                    new DynamicTextComponent(type.recommendedParents.get(index), LABEL),
                    0.3f, 0.37f - index * 0.11f, 0.45f, 0.47f - index * 0.11f
            );
        }

        addComponent(new DynamicTextButton("Apply", SAVE_BASE, SAVE_HOVER, () -> {
            changeModel.accept(new DefaultItemModel(parentField.getText()));
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.2f, 0.175f, 0.3f);

        HelpButtons.addHelpLink(this, "edit menu/items/edit/model/default.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
