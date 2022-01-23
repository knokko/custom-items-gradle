package nl.knokko.customitems.editor.menu.edit.container.slot;

import nl.knokko.customitems.container.slot.ContainerSlotValues;
import nl.knokko.customitems.container.slot.StorageSlotValues;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class CreateStorageSlot extends GuiMenu {

    private final GuiComponent returnMenu;
    private final SItemSet itemSet;
    private final StorageSlotValues currentValues;
    private final Consumer<ContainerSlotValues> submitSlot;

    public CreateStorageSlot(
            GuiComponent returnMenu, SItemSet itemSet, Consumer<ContainerSlotValues> submitSlot
    ) {
        this.returnMenu = returnMenu;
        this.itemSet = itemSet;
        this.currentValues = new StorageSlotValues(true);
        this.submitSlot = submitSlot;
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.7f, 0.175f, 0.8f);

        addComponent(
                new DynamicTextComponent("Place holder:", LABEL),
                0.2f, 0.5f, 0.4f, 0.6f);
        addComponent(new DynamicTextButton("Change...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new CreateDisplay(
                    this, itemSet, currentValues::setPlaceholder, true
            ));
        }), 0.45f, 0.5f, 0.6f, 0.6f);

        addComponent(new DynamicTextButton("Done", SAVE_BASE, SAVE_HOVER, () -> {
            submitSlot.accept(currentValues);
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.1f, 0.175f, 0.2f);

        HelpButtons.addHelpLink(this, "edit menu/containers/slots/storage.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return EditProps.BACKGROUND;
    }
}
