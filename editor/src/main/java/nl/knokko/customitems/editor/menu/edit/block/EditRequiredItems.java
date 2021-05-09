package nl.knokko.customitems.editor.menu.edit.block;

import nl.knokko.customitems.block.drop.RequiredItems;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.drops.ChooseRequiredHeldItems;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.WrapperComponent;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.function.Consumer;

public class EditRequiredItems extends GuiMenu  {

    private final RequiredItems requiredItems;

    private final Consumer<RequiredItems> changeRequiredItems;
    private final ItemSet set;
    private final GuiComponent returnMenu;

    public EditRequiredItems(
            RequiredItems oldItems, Consumer<RequiredItems> changeRequiredItems,
            ItemSet set, GuiComponent returnMenu
    ) {
        this.requiredItems = new RequiredItems(oldItems, true);
        this.changeRequiredItems = changeRequiredItems;
        this.set = set;
        this.returnMenu = returnMenu;
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () ->
                state.getWindow().setMainComponent(returnMenu)
        ), 0.025f, 0.7f, 0.175f, 0.8f);

        DynamicTextComponent errorComponent = new DynamicTextComponent("", EditProps.ERROR);
        addComponent(errorComponent, 0.025f, 0.9f, 0.975f, 1f);

        addComponent(new DynamicTextButton("Apply", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
            String error = Validation.toErrorString(() -> requiredItems.validateComplete(set.getBackingItems()));
            if (error == null) {
                changeRequiredItems.accept(requiredItems);
                state.getWindow().setMainComponent(returnMenu);
            } else {
                errorComponent.setText(error);
            }
        }), 0.025f, 0.2f, 0.175f, 0.3f);

        addComponent(new CheckboxComponent(
                requiredItems.isEnabled(), requiredItems::setEnabled
        ), 0.05f, 0.525f, 0.1f, 0.575f);
        addComponent(new DynamicTextComponent("Enabled", EditProps.LABEL),
                0.125f, 0.5f, 0.25f, 0.6f);

        addComponent(new WrapperComponent<>(new EditEnabled()){
            @Override
            public boolean isActive() {
                return requiredItems.isEnabled();
            }
        }, 0.26f, 0f, 1f, 0.9f);

        // TODO Create help menu
    }

    @Override
    public GuiColor getBackgroundColor() {
        return EditProps.BACKGROUND;
    }

    private class EditEnabled extends GuiMenu {

        @Override
        protected void addComponents() {
            addComponent(new DynamicTextComponent("Required custom items:", EditProps.LABEL),
                    0.01f, 0.8f, 0.2f, 0.9f);
            addComponent(new DynamicTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () ->
                    state.getWindow().setMainComponent(new ChooseRequiredHeldItems(
                            set.getBackingItems(), requiredItems.getCustomItems(),
                            requiredItems::setCustomItems, EditRequiredItems.this
                    ))
            ), 0.21f, 0.8f, 0.3f, 0.9f);

            addComponent(new DynamicTextComponent("Required vanilla items:", EditProps.LABEL),
                    0f, 0.6f, 0.2f, 0.7f);
            // TODO Create menu to select multiple vanilla items

            addComponent(new CheckboxComponent(
                    requiredItems.isInverted(), requiredItems::setInverted
            ), 0.05f, 0.325f, 0.1f, 0.375f);
            addComponent(new DynamicTextComponent("Invert", EditProps.LABEL),
                    0.12f, 0.3f, 0.22f, 0.4f);
        }

        @Override
        public GuiColor getBackgroundColor() {
            return EditProps.BACKGROUND;
        }
    }
}
