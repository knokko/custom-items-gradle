package nl.knokko.customitems.editor.menu.edit.container.slot;

import nl.knokko.customitems.container.slot.ContainerSlotValues;
import nl.knokko.customitems.container.slot.StorageSlotValues;
import nl.knokko.customitems.container.slot.display.SlotDisplayValues;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.function.Consumer;

public class CreateStorageSlot extends GuiMenu {

    private final GuiComponent returnMenu;
    private final Consumer<ContainerSlotValues> submitSlot;
    private final Iterable<ItemReference> customItems;

    public CreateStorageSlot(GuiComponent returnMenu, Consumer<ContainerSlotValues> submitSlot,
                                       Iterable<ItemReference> customItems) {
        this.returnMenu = returnMenu;
        this.submitSlot = submitSlot;
        this.customItems = customItems;
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.7f, 0.175f, 0.8f);

        SlotDisplayValues[] pDisplay = {null};

        addComponent(new DynamicTextComponent("Place holder:", EditProps.LABEL),
                0.2f, 0.5f, 0.4f, 0.6f);
        addComponent(new DynamicTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () -> {
            state.getWindow().setMainComponent(new CreateDisplay(this,
                    newDisplay -> pDisplay[0] = newDisplay, true, customItems)
            );
        }), 0.45f, 0.5f, 0.6f, 0.6f);

        addComponent(new DynamicTextButton("Done", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
            submitSlot.accept(StorageSlotValues.createQuick(pDisplay[0]));
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.1f, 0.175f, 0.2f);

        HelpButtons.addHelpLink(this, "edit menu/containers/slots/storage.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return EditProps.BACKGROUND;
    }
}
