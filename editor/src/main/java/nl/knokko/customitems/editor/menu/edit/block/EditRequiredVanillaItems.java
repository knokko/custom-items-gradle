package nl.knokko.customitems.editor.menu.edit.block;

import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.block.drop.RequiredItems;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.menu.edit.collection.InlineCollectionEdit;
import nl.knokko.customitems.item.VMaterial;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.image.ImageButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.Collection;
import java.util.function.Consumer;

public class EditRequiredVanillaItems extends InlineCollectionEdit<RequiredItems.VanillaEntry> {

    public EditRequiredVanillaItems(
            Collection<RequiredItems.VanillaEntry> currentCollection,
            Consumer<Collection<RequiredItems.VanillaEntry>> onApply,
            GuiComponent returnMenu
    ) {
        super(returnMenu, currentCollection, onApply);
    }

    @Override
    protected void addRowComponents(int itemIndex, float minY, float maxY) {
        RequiredItems.VanillaEntry original = ownCollection.get(itemIndex);

        addComponent(EnumSelect.createSelectButton(
                VMaterial.class, newMaterial -> {
                    ownCollection.get(itemIndex).setMaterial(newMaterial);
                }, candidate -> candidate.lastVersion >= MCVersions.VERSION1_13, original.getMaterial()
        ), 0.3f, minY, 0.6f, maxY);
        addComponent(new CheckboxComponent(original.shouldAllowCustomItems(), newValue -> {
            ownCollection.get(itemIndex).setAllowCustomItems(newValue);
        }), 0.65f, minY + 0.02f, 0.7f, maxY - 0.02f);
        addComponent(new DynamicTextComponent("Allow custom items", EditProps.LABEL),
                0.71f, minY, 0.89f, maxY);
        addComponent(new ImageButton(deleteBase, deleteHover, () -> {
            removeItem(itemIndex);
        }), 0.92f, minY + 0.02f, 0.98f, maxY - 0.02f);
    }

    @Override
    protected RequiredItems.VanillaEntry addNew() {
        return new RequiredItems.VanillaEntry(true);
    }

    @Override
    protected String getHelpPage() {
        return "edit menu/blocks/drops/required vanilla items.html";
    }
}
