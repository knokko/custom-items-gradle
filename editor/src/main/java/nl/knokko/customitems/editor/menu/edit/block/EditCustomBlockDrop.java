package nl.knokko.customitems.editor.menu.edit.block;

import nl.knokko.customitems.block.drop.CustomBlockDrop;
import nl.knokko.customitems.block.drop.SilkTouchRequirement;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.menu.edit.container.recipe.EditOutputTable;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.util.Checks;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.Collection;

public class EditCustomBlockDrop extends GuiMenu  {

    private final CustomBlockDrop toModify;
    private final Collection<CustomBlockDrop> backingCollection;
    private final CustomBlockDrop currentDrop;

    private final ItemSet set;
    private final GuiComponent returnMenu;

    public EditCustomBlockDrop(
            CustomBlockDrop toModify, CustomBlockDrop startValues,
            ItemSet set, GuiComponent returnMenu
    ) {
        Checks.nonNull(toModify, startValues, set, returnMenu);
        this.toModify = toModify;
        this.backingCollection = null;
        this.currentDrop = new CustomBlockDrop(startValues, true);
        this.set = set;
        this.returnMenu = returnMenu;
    }

    public EditCustomBlockDrop(
            Collection<CustomBlockDrop> backingCollection, CustomBlockDrop startValues,
            ItemSet set, GuiComponent returnMenu
    ) {
        Checks.nonNull(backingCollection, startValues, set, returnMenu);
        this.toModify = null;
        this.backingCollection = backingCollection;
        this.currentDrop = new CustomBlockDrop(startValues, true);
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

        addComponent(new DynamicTextButton(toModify == null ? "Create" : "Apply", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
            String error = Validation.toErrorString(() -> currentDrop.validateComplete(set.getBackingItems()));
            if (error == null) {
                if (toModify == null) {
                    backingCollection.add(currentDrop);
                } else {
                    toModify.copyFrom(currentDrop);
                }
                state.getWindow().setMainComponent(returnMenu);
            } else {
                errorComponent.setText(error);
            }
        }), 0.025f, 0.1f, 0.175f, 0.2f);

        addComponent(new DynamicTextComponent("Items to drop:", EditProps.LABEL),
                0.3f, 0.7f, 0.49f, 0.8f);
        addComponent(new DynamicTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () ->
                state.getWindow().setMainComponent(new EditOutputTable(
                        this, currentDrop.getItemsToDrop(), currentDrop::setItemsToDrop, set
                ))
        ), 0.5f, 0.7f, 0.62f, 0.8f);

        addComponent(new DynamicTextComponent("Silk touch:", EditProps.LABEL),
                0.33f, 0.55f, 0.49f, 0.65f);
        addComponent(EnumSelect.createSelectButton(
                SilkTouchRequirement.class, currentDrop::setSilkTouchRequirement, currentDrop.getSilkTouchRequirement()
        ), 0.5f, 0.55f, 0.62f, 0.65f);

        addComponent(new DynamicTextComponent("Required held items:", EditProps.LABEL),
                0.25f, 0.4f, 0.49f, 0.5f);
        addComponent(new DynamicTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () ->
                state.getWindow().setMainComponent(new EditRequiredItems(
                        currentDrop.getRequiredItems(), currentDrop::setRequiredItems,
                        set, this
                ))
        ), 0.5f, 0.4f, 0.625f, 0.5f);

        // TODO Create help menu
    }

    @Override
    public GuiColor getBackgroundColor() {
        return EditProps.BACKGROUND;
    }
}
